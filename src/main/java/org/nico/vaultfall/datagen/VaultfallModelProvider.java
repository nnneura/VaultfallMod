package org.nico.vaultfall.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;
import org.nico.vaultfall.block.ModBlocks;
import org.nico.vaultfall.item.ModItems;

public class VaultfallModelProvider extends FabricModelProvider {

    public VaultfallModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SELETHILITE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_SELETHILITE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SELETHILITE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RUSTY_REINFORCED_STEEL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.REINFORCED_STEEL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.REJILLA_ACERO);
        TextureMap vigaTexturas = new TextureMap()
                .put(TextureKey.SIDE, Identifier.of("vaultfall", "block/viga_acero"))
                .put(TextureKey.END, Identifier.of("vaultfall", "block/viga_acero_top"));

        // 2. Subimos el modelo usando el cubo columna estándar (un cubo perfecto que rota)
        Identifier vigaModeloId = Models.CUBE_COLUMN.upload(
                ModBlocks.VIGA_ACERO,
                vigaTexturas,
                blockStateModelGenerator.modelCollector
        );

        // 3. Registramos los estados de bloque para que Minecraft sepa cómo rotarlo en los ejes X, Y, Z
        blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createAxisRotatedBlockState(ModBlocks.VIGA_ACERO, vigaModeloId)
        );

        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                        ModBlocks.LAMPARA_SELETHILITE,
                        BlockStateVariant.create().put(
                                VariantSettings.MODEL,
                                Identifier.of("vaultfall", "block/lampara_selethilite")
                        )
                )
        );
        blockStateModelGenerator.registerParentedItemModel(
                ModBlocks.LAMPARA_SELETHILITE,
                Identifier.of("vaultfall", "block/lampara_selethilite")
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        itemModelGenerator.register(ModItems.NUCLEO_PROPULSION, Models.GENERATED);
        itemModelGenerator.register(ModItems.CELULA_ENERGIA, Models.GENERATED);
        itemModelGenerator.register(ModItems.SELETHILITE, Models.GENERATED);
        itemModelGenerator.register(ModItems.PROPULSOR, Models.GENERATED);
        itemModelGenerator.register(ModItems.POLVO_ACERO, Models.GENERATED);
        itemModelGenerator.register(ModItems.LINGOTE_ACERO, Models.GENERATED);
        itemModelGenerator.register(ModItems.ESPADA_MECANICA_BASE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.HOJA_ACERO, Models.GENERATED);
        itemModelGenerator.register(ModItems.BARRA_ACERO, Models.GENERATED);
        itemModelGenerator.register(ModItems.MANGO_ACERO, Models.GENERATED);
        itemModelGenerator.register(ModItems.MECHANICAL_SWORD_UPGRADE_COMPONENT, Models.GENERATED);
        itemModelGenerator.register(ModItems.ESPADA_MECANICA_MEJORADA, Models.HANDHELD);
        itemModelGenerator.register(ModItems.ESPADA_MECANICA_SELETHILITE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.PLANTILLA_SELETHILITE, Models.GENERATED);
        itemModelGenerator.register(ModItems.NUCLEO_SELETHILITA, Models.GENERATED);
        itemModelGenerator.register(ModItems.SELETHILITE_UPGRADE_COMPONENT, Models.GENERATED);
        itemModelGenerator.register(ModItems.EXO_CASCO, Models.GENERATED);
        itemModelGenerator.register(ModItems.EXO_TORSO, Models.GENERATED);
        itemModelGenerator.register(ModItems.EXO_PIERNAS, Models.GENERATED);
        itemModelGenerator.register(ModItems.EXO_BOTAS, Models.GENERATED);
        itemModelGenerator.register(ModItems.MODULO_VISION_NOCTURNA, Models.GENERATED);
        itemModelGenerator.register(ModItems.MODULO_RESPIRACION, Models.GENERATED);



        itemModelGenerator.register(
                ModBlocks.ENGRANAJE.asItem(),
                Models.GENERATED
        );

        itemModelGenerator.register(
                ModBlocks.ENGRANAJE_OXIDADO.asItem(),
                Models.GENERATED
        );
    }
}