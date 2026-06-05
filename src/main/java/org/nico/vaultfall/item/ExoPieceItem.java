package org.nico.vaultfall.item;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.nico.vaultfall.component.ModDataComponents;
import org.nico.vaultfall.effect.ModEffects;
import org.nico.vaultfall.screen.ExoModuleScreenHandler;
import org.joml.Vector3f;

import java.util.List;

public class ExoPieceItem extends TrinketItem {

    private static final Identifier TOUGHNESS_ID = Identifier.of("vaultfall", "exo_piece_toughness");
    private static final Identifier SPEED_MOD_ID = Identifier.of("vaultfall", "module_speed_boost");
    private static final Identifier KB_RESIST_MOD_ID = Identifier.of("vaultfall", "module_kb_resist");
    private final TagKey<Item> validModuleTag;

    public ExoPieceItem(Settings settings, TagKey<Item> validModuleTag) {
        super(settings);
        this.validModuleTag = validModuleTag;
    }

    // --- Single Source of Truth para la capacidad ---
    public int getModuleCapacity() {
        return 1; // Futuras piezas pueden sobrescribir esto
    }

    // --- Getter seguro para el Tag ---
    public TagKey<Item> getValidModuleTag() {
        return this.validModuleTag;
    }

    @Override
    public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier id) {
        var modifiers = super.getModifiers(stack, slot, entity, id);
        modifiers.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(
                TOUGHNESS_ID, 1.0, EntityAttributeModifier.Operation.ADD_VALUE));
        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container != null) {
            // Obtenemos el primer (y único) ítem en el slot del módulo
            ItemStack module = container.stream().findFirst().orElse(ItemStack.EMPTY);

            if (!module.isEmpty()) {
                // --- MÓDULO DE VELOCIDAD ---
                if (module.isOf(ModItems.MODULO_SPEED)) {
                    // +0.04 velocidad (Aprox 20-30% más rápido en juego)
                    modifiers.put(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                            new EntityAttributeModifier(SPEED_MOD_ID, 0.04, EntityAttributeModifier.Operation.ADD_VALUE));
                }

                // --- MÓDULO DE RESISTENCIA AL EMPUJE ---
                if (module.isOf(ModItems.MODULO_KB_RESISTANCE)) {
                    // +0.4 (40% de probabilidad de ignorar knockback)
                    // Usamos ADD_VALUE porque KB Resistance funciona como un pool acumulativo en vanilla
                    modifiers.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                            new EntityAttributeModifier(KB_RESIST_MOD_ID, 0.4, EntityAttributeModifier.Operation.ADD_VALUE));
                }
            }
        }
        return modifiers;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld().isClient()) return;

        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container == null) return;

        ItemStack module = container.stream().findFirst().orElse(ItemStack.EMPTY);
        if (module.isEmpty()) return;

        if (!module.isIn(this.validModuleTag)) return;

        World world = entity.getWorld();
        long now = world.getTime();

        // --- Efectos simples ya existentes ---
        if (module.isOf(ModItems.MODULO_VISION_NOCTURNA)) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 220, 0, false, false, true));
        }
        if (module.isOf(ModItems.MODULO_RESPIRACION)) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 220, 0, false, false, true));
        }

        // --- NUEVO: ADRENALINA & SHOCKWAVE ---
        if (module.isOf(ModItems.MODULO_ADRENALINA) && entity instanceof PlayerEntity player) {
            // Activación: vida < 30% de la máxima
            float maxHp = player.getMaxHealth();
            float threshold = maxHp * 0.30f;

            if (player.getHealth() < threshold) {
                Long last = module.get(ModDataComponents.LAST_ACTIVATION_TIMESTAMP);
                long lastActivation = last != null ? last : 0L;

                // 1200 ticks = 60 segundos de cooldown
                long cooldownTicks = 1200L;

                if (now - lastActivation >= cooldownTicks) {
                    // 1) Guardamos el nuevo timestamp en la copia temporal del módulo
                    module.set(ModDataComponents.LAST_ACTIVATION_TIMESTAMP, now);

                    // 2) [¡LA LÍNEA CLAVE!] Sobrescribimos el contenedor de la pieza de armadura (stack)
                    // para que el juego guarde el nuevo timestamp permanentemente.
                    stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(List.of(module)));

                    // 3) Cura instantánea: 4 HP = 2 corazones
                    player.heal(4.0f);

                    // 4) Regeneración I por 10s (200 ticks), amplificador 0
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0, false, false, true));

                    // 5) Resistencia V por 2s (40 ticks), amplificador 4
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 4, false, false, true));

                    // 6) Shockwave: Parálisis en 15 bloques (5s = 100 ticks)
                    Box area = player.getBoundingBox().expand(15.0);
                    List<Entity> victims = world.getOtherEntities(player, area);
                    for (Entity victim : victims) {
                        if (victim instanceof LivingEntity le && !le.isDead()) {
                            le.addStatusEffect(new StatusEffectInstance(ModEffects.PARALISIS_ENTRY, 100, 0, false, true, true));
                        }
                    }

                    // Feedback al jugador (sonido + partículas)
                    world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 1.5f, 0.6f);

                    if (world instanceof ServerWorld serverWorld) {
                        DustParticleEffect selethiliteDust = new DustParticleEffect(new Vector3f(0.2f, 1.0f, 0.6f), 2.5f);

                        // A. Explosión central
                        serverWorld.spawnParticles(selethiliteDust,
                                player.getX(), player.getBodyY(0.5), player.getZ(),
                                150, 2.0, 2.0, 2.0, 0.0);

                        // B. Anillo expansivo
                        double radius = 15.0;
                        int points = 180;
                        for (int i = 0; i < points; i++) {
                            double angle = i * (Math.PI * 2) / points;
                            double offsetX = Math.cos(angle) * radius;
                            double offsetZ = Math.sin(angle) * radius;

                            serverWorld.spawnParticles(selethiliteDust,
                                    player.getX() + offsetX, player.getBodyY(0.5), player.getZ() + offsetZ,
                                    1, 0.0, 0.0, 0.0, 0.0);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        final int MAX_SLOTS = this.getModuleCapacity();
        int filledSlots = 0;

        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container != null) {
            filledSlots = (int) container.stream().filter(s -> !s.isEmpty()).count();
        }

        Formatting stateColor;
        if (filledSlots == 0) {
            stateColor = Formatting.GRAY;
        } else if (filledSlots >= MAX_SLOTS) {
            stateColor = Formatting.GOLD;
        } else {
            stateColor = Formatting.GREEN;
        }

        Text baseText = Text.translatable("tooltip.vaultfall.exo_piece.modules").formatted(Formatting.GRAY);
        Text slotsValue = Text.literal("[" + filledSlots + "/" + MAX_SLOTS + "]").formatted(stateColor);

        tooltip.add(((MutableText) baseText).append(slotsValue));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
            SimpleInventory inventory = new SimpleInventory(this.getModuleCapacity());

            if (container != null) {
                List<ItemStack> items = container.stream().toList();
                for (int i = 0; i < Math.min(items.size(), this.getModuleCapacity()); i++) {
                    inventory.setStack(i, items.get(i));
                }
            }

            // ARQUITECTURA DE RED 1.21.1: Payload Genérico
            user.openHandledScreen(new ExtendedScreenHandlerFactory<ItemStack>() {
                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new ExoModuleScreenHandler(syncId, inv, inventory, stack, ExoPieceItem.this.validModuleTag, ExoPieceItem.this.getModuleCapacity());
                }

                @Override
                public Text getDisplayName() {
                    return stack.getName();
                }

                @Override
                public ItemStack getScreenOpeningData(ServerPlayerEntity player) { // <--- FIRMA CORRECTA (Exigida por Fabric 1.21.1)
                    return stack; // <--- LÓGICA CORRECTA (Envía el item real, no null)
                }
            });
        }

        return TypedActionResult.success(stack);
    }
}