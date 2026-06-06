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

                                // === 1. BLOQUES NATURALES Y MINERALES ===
                                entries.add(ModBlocks.SELETHILITE_ORE.asItem());
                                entries.add(ModBlocks.DEEPSLATE_SELETHILITE_ORE.asItem());

// === 2. BLOQUES DE ALMACENAMIENTO (METALES/GEMAS) ===
                                entries.add(ModBlocks.SELETHILITE_BLOCK.asItem());
                                entries.add(ModBlocks.REINFORCED_STEEL_BLOCK.asItem());
                                entries.add(ModBlocks.RUSTY_REINFORCED_STEEL_BLOCK.asItem());

// === 3. BLOQUES DE DECORACIÓN / ESTRUCTURA ===
                                entries.add(ModBlocks.VIGA_ACERO.asItem());
                                entries.add(ModBlocks.REJILLA_ACERO.asItem());
                                entries.add(ModBlocks.LAMPARA_SELETHILITE.asItem());
                                entries.add(ModBlocks.ENGRANAJE.asItem());
                                entries.add(ModBlocks.ENGRANAJE_OXIDADO.asItem());

// === 4. MATERIALES BASE (INGOTES, POLVOS, GEMAS) ===
                                entries.add(ModItems.SELETHILITE);
                                entries.add(ModItems.POLVO_ACERO);
                                entries.add(ModItems.LINGOTE_ACERO);

// === 5. COMPONENTES DE CRAFTEO INTERMEDIOS ===
                                entries.add(ModItems.BARRA_ACERO);
                                entries.add(ModItems.MANGO_ACERO);
                                entries.add(ModItems.HOJA_ACERO);
                                entries.add(ModItems.CELULA_ENERGIA);
                                entries.add(ModItems.NUCLEO_PROPULSION);
                                entries.add(ModItems.PROPULSOR);
                                entries.add(ModItems.NUCLEO_SELETHILITA);

// === 6. PLANTILLAS Y COMPONENTES DE MEJORA ===
                                entries.add(ModItems.PLANTILLA_SELETHILITE);
                                entries.add(ModItems.MECHANICAL_SWORD_UPGRADE_COMPONENT);
                                entries.add(ModItems.SELETHILITE_UPGRADE_COMPONENT);

// === 7. HERRAMIENTAS Y ARMAS (PROGRESIÓN) ===
                                entries.add(ModItems.ESPADA_MECANICA_BASE);
                                entries.add(ModItems.ESPADA_MECANICA_MEJORADA);
                                entries.add(ModItems.ESPADA_MECANICA_SELETHILITE);

// === 8. ARMADURAS (DE CABEZA A PIES) ===
                                entries.add(ModItems.EXO_CASCO);
                                entries.add(ModItems.EXO_TORSO);
                                entries.add(ModItems.EXO_PIERNAS);
                                entries.add(ModItems.EXO_BOTAS);

// === 9. MÓDULOS DE MEJORA (EXOESQUELETO) ===
                                entries.add(ModItems.MODULO_VISION_NOCTURNA);
                                entries.add(ModItems.MODULO_RESPIRACION);
                                entries.add(ModItems.MODULO_ADRENALINA);
                                entries.add(ModItems.MODULO_ELYTRA);
                                entries.add(ModItems.MODULO_SPEED);
                                entries.add(ModItems.MODULO_KB_RESISTANCE);
                                entries.add(ModItems.MODULO_SUPER_SALTO);
                                entries.add(ModItems.MODULO_DOBLE_SALTO);






                            })

                            .build()
            );
    public static void registerItemGroups() {
        // Método vacío solo para forzar a que la clase se cargue en la inicialización
    }
}
