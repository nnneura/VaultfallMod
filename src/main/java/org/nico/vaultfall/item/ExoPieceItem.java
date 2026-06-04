package org.nico.vaultfall.item;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.nico.vaultfall.screen.ExoModuleScreenHandler;

import java.util.List;

public class ExoPieceItem extends TrinketItem {

    private static final Identifier TOUGHNESS_ID = Identifier.of("vaultfall", "exo_piece_toughness");
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
        return modifiers;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld().isClient()) return;

        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container == null) return;

        ItemStack module = container.stream().findFirst().orElse(ItemStack.EMPTY);
        if (module.isEmpty()) return;

        if (module.isIn(this.validModuleTag)) {
            if (module.isOf(ModItems.MODULO_VISION_NOCTURNA)) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 220, 0, false, false, true));
            }
            if (module.isOf(ModItems.MODULO_RESPIRACION)) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 220, 0, false, false, true));
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