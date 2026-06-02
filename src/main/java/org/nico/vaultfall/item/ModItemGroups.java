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
                                entries.add(ModBlocks.SELETHILITE_ORE.asItem());
                                entries.add(ModBlocks.DEEPSLATE_SELETHILITE_ORE.asItem());
                                entries.add(ModBlocks.SELETHILITE_BLOCK.asItem());
                                entries.add(ModBlocks.RUSTY_REINFORCED_STEEL_BLOCK.asItem());
                                entries.add(ModBlocks.REINFORCED_STEEL_BLOCK.asItem());
                                entries.add(ModBlocks.VIGA_ACERO.asItem());
                                entries.add(ModBlocks.LAMPARA_SELETHILITE.asItem());
                                entries.add(ModBlocks.REJILLA_ACERO.asItem());
                                entries.add(ModItems.NUCLEO_PROPULSION);
                                entries.add(ModItems.CELULA_ENERGIA);
                                entries.add(ModItems.SELETHILITE);
                                entries.add(ModItems.PROPULSOR);
                                entries.add(ModItems.POLVO_ACERO);
                                entries.add(ModItems.LINGOTE_ACERO);
                                entries.add(ModItems.ESPADA_MECANICA_BASE);
                                entries.add(ModItems.MANGO_ACERO);
                                entries.add(ModItems.HOJA_ACERO);
                                entries.add(ModItems.BARRA_ACERO);
                                entries.add(ModItems.MECHANICAL_SWORD_UPGRADE_COMPONENT);
                                entries.add(ModItems.ESPADA_MECANICA_MEJORADA);
                                entries.add(ModItems.PLANTILLA_SELETHILITE);



                            })

                            .build()
            );
    public static void registerItemGroups() {
        // Método vacío solo para forzar a que la clase se cargue en la inicialización
    }
}
