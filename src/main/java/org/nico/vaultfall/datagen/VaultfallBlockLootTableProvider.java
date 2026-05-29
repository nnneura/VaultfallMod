package org.nico.vaultfall.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import org.nico.vaultfall.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class VaultfallBlockLootTableProvider
        extends FabricBlockLootTableProvider {

    public VaultfallBlockLootTableProvider(
            FabricDataOutput dataOutput,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {

        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate() {

        addDrop(ModBlocks.ENGRANAJE);
        addDrop(ModBlocks.ENGRANAJE_OXIDADO);
    }
}