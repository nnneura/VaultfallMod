package org.nico.vaultfall.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    // Creamos tu pestaña personalizada "Vaultfall"
    public static final ItemGroup VAULTFALL_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of("vaultfall", "vaultfall_group"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.vaultfall.vaultfall_group"))
                    .icon(() -> new ItemStack(ModItems.ENGRANAJE)) // El ícono de la pestaña
                    .entries((displayContext, entries) -> {
                        // Aquí agregamos todos los ítems que aparecerán en la pestaña
                        entries.add(ModItems.ENGRANAJE);
                        entries.add(ModItems.ENGRANAJE_OXIDADO);
                        entries.add(ModItems.NUCLEO_PROPULSION);

                    }).build());

    public static void registerItemGroups() {
        // Método vacío solo para forzar a que la clase se cargue en la inicialización
    }
}
