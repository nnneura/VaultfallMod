package org.nico.vaultfall.event;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.nico.vaultfall.item.ModItems;

/**
 * Registra eventos para habilitar el vuelo con el módulo Elytra en Trinkets.
 *
 * <p>Este evento se llama:</p>
 * <ul>
 *   <li>Cuando el jugador intenta iniciar el vuelo (presiona espacio en el aire)</li>
 *   <li>Cada tick mientras está volando (para verificar si puede continuar)</li>
 * </ul>
 *
 * <p>Fabric API maneja automáticamente toda la física, animaciones y sincronización.</p>
 *
 * @author Vaultfall Team
 * @since 1.0.0
 */
public class ElytraModuleEvents {

    /**
     * Registra el evento de vuelo de Elytra para el módulo.
     * Debe llamarse en ModInitializer.onInitialize().
     */
    public static void register() {
        EntityElytraEvents.CUSTOM.register((entity, tickElytra) -> {
            // Solo aplicamos esto a jugadores
            if (!(entity instanceof PlayerEntity player)) {
                return false;
            }

            // Buscar el módulo Elytra en los slots de Trinkets
            TrinketComponent trinkets = TrinketsApi.getTrinketComponent(player).orElse(null);
            if (trinkets == null) {
                return false;
            }

            // Verificar si tiene el módulo Elytra equipado
            boolean hasModule = trinkets.isEquipped(stack -> {
                ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
                if (container == null) {
                    return false;
                }

                ItemStack module = container.stream().findFirst().orElse(ItemStack.EMPTY);
                return module.isOf(ModItems.MODULO_ELYTRA);
            });

            // Si no tiene el módulo, no puede volar
            if (!hasModule) {
                return false;
            }

            // Si tickElytra es false, solo estamos verificando si puede iniciar el vuelo
            // Retornamos true para permitir que Fabric API inicie el vuelo
            if (!tickElytra) {
                return true;
            }

            // Si tickElytra es true, estamos en un tick de vuelo activo
            // Aquí podemos aplicar lógica adicional como daño por durabilidad

            // Obtener el ItemStack del módulo para aplicar daño
            ItemStack moduleStack = findEquippedModule(player);
            if (moduleStack == null || moduleStack.isEmpty()) {
                return false;
            }

            // Aplicar lógica de tick de Elytra vanilla cada 20 ticks
            World world = player.getWorld();
            int fallFlyingTicks = player.getFallFlyingTicks() + 1;

            if (!world.isClient() && fallFlyingTicks % 20 == 0) {
                // Dañar el módulo cada 20 ticks (1 segundo)
                // Ajusta la cantidad de daño según tu balance
                if (moduleStack.isDamageable()) {
                    moduleStack.setDamage(moduleStack.getDamage() + 1);

                    // Si se rompe, reproducir sonido de rotura
                    if (moduleStack.getDamage() >= moduleStack.getMaxDamage()) {
                        world.playSound(
                                null,
                                player.getX(), player.getY(), player.getZ(),
                                SoundEvents.ENTITY_ITEM_BREAK,
                                SoundCategory.PLAYERS,
                                1.0f, 1.0f
                        );
                    }
                }

                // Enviar evento de juego para animaciones
                player.emitGameEvent(GameEvent.ELYTRA_GLIDE);
            }

            // Retornar true para mantener el vuelo activo
            return true;
        });
    }

    /**
     * Busca el ItemStack del módulo Elytra equipado en los Trinkets del jugador.
     *
     * @param player El jugador
     * @return El ItemStack del módulo, o null si no está equipado
     */
    private static ItemStack findEquippedModule(PlayerEntity player) {
        TrinketComponent trinkets = TrinketsApi.getTrinketComponent(player).orElse(null);
        if (trinkets == null) {
            return null;
        }

        // Buscar en todos los slots de Trinkets
        for (var group : trinkets.getInventory().values()) {
            for (var inventory : group.values()) {
                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack stack = inventory.getStack(i);
                    ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);

                    if (container != null) {
                        ItemStack module = container.stream().findFirst().orElse(ItemStack.EMPTY);
                        if (module.isOf(ModItems.MODULO_ELYTRA)) {
                            return module;
                        }
                    }
                }
            }
        }

        return null;
    }
}