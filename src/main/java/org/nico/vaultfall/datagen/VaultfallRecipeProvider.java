package org.nico.vaultfall.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import org.nico.vaultfall.block.ModBlocks;
import org.nico.vaultfall.item.ModItems;


import java.util.concurrent.CompletableFuture;

public class VaultfallRecipeProvider extends FabricRecipeProvider {

    public VaultfallRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {

        ShapedRecipeJsonBuilder.create(
                        RecipeCategory.MISC,
                        ModItems.NUCLEO_PROPULSION
                )
                .pattern("RGR")
                .pattern("GDG")
                .pattern("RGR")
                .input('R', Items.REDSTONE)
                .input('G', ModBlocks.ENGRANAJE.asItem())
                .input('D', Items.DIAMOND)

                .criterion(
                        RecipeProvider.hasItem(ModBlocks.ENGRANAJE.asItem()),
                        RecipeProvider.conditionsFromItem(ModBlocks.ENGRANAJE.asItem())
                )

                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CELULA_ENERGIA, 1)
                .pattern("III") //
                .pattern("SSS") //
                .pattern("III") //

                .input('I', Items.IRON_INGOT)
                .input('S', ModItems.SELETHILITE)

                .criterion(hasItem(ModItems.SELETHILITE), conditionsFromItem(ModItems.SELETHILITE))

                .offerTo(exporter);
    }
}