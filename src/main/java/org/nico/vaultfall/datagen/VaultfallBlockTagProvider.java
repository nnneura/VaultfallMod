package org.nico.vaultfall.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import org.nico.vaultfall.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class VaultfallBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public VaultfallBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        // 1. En Yarn se llama PICKAXE_MINEABLE en lugar de MINEABLE_WITH_PICKAXE
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.SELETHILITE_ORE);

        // 2. En Yarn se llama NEEDS_IRON_TOOL de igual forma, pero si te sale en rojo,
        // puedes usar su equivalente exacto en Yarn: NEEDS_IRON_LEVEL
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.SELETHILITE_ORE);
    }
}