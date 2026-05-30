package org.nico.vaultfall.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models; // <-- Cambió a Models
import org.nico.vaultfall.block.ModBlocks;
import org.nico.vaultfall.item.ModItems;

public class VaultfallModelProvider extends FabricModelProvider {

    public VaultfallModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SELETHILITE_ORE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        itemModelGenerator.register(ModItems.NUCLEO_PROPULSION, Models.GENERATED);
        itemModelGenerator.register(ModItems.CELULA_ENERGIA, Models.GENERATED);
        itemModelGenerator.register(ModItems.SELETHILITE, Models.GENERATED);

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