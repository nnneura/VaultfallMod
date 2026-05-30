package org.nico.vaultfall.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.nico.vaultfall.block.ModBlocks;

public class ModItemGroups {

    // Creamos tu pestaña personalizada "Vaultfall"
    public static final ItemGroup VAULTFALL_GROUP =
            Registry.register(
                    Registries.ITEM_GROUP,
                    Identifier.of("vaultfall", "vaultfall_group"),

                    FabricItemGroup.builder()
                            .displayName(Text.translatable("itemgroup.vaultfall.vaultfall_group"))

                            .icon(() ->
                                    new ItemStack(ModBlocks.ENGRANAJE)
                            )

                            .entries((displayContext, entries) -> {

                                entries.add(ModBlocks.ENGRANAJE.asItem());
                                entries.add(ModBlocks.ENGRANAJE_OXIDADO.asItem());
                                entries.add(ModItems.NUCLEO_PROPULSION);
                                entries.add(ModItems.CELULA_ENERGIA);
                                entries.add(ModItems.SELETHILITE);
                                entries.add(ModBlocks.SELETHILITE_ORE.asItem());


                            })

                            .build()
            );
    public static void registerItemGroups() {
        // Método vacío solo para forzar a que la clase se cargue en la inicialización
    }
}
