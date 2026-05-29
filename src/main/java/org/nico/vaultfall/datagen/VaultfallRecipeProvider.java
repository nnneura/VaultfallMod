package org.nico.vaultfall.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import org.nico.vaultfall.item.ModItems;


import java.util.concurrent.CompletableFuture;

public class VaultfallRecipeProvider extends FabricRecipeProvider {

    public VaultfallRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.NUCLEO_PROPULSION)
                .pattern("RGR")
                .pattern("GDG")
                .pattern("RGR")
                .input('R', Items.REDSTONE)
                .input('G', ModItems.ENGRANAJE)
                .input('D', Items.DIAMOND)
                .criterion(RecipeProvider.hasItem(ModItems.ENGRANAJE), RecipeProvider.conditionsFromItem(ModItems.ENGRANAJE))
                .offerTo(exporter);
    }
}