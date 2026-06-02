package org.nico.vaultfall.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.nico.vaultfall.block.ModBlocks;
import org.nico.vaultfall.item.ModItems;


import java.util.List;
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
                .pattern("ITI") //
                .pattern("SSS") //
                .pattern("ITI") //

                .input('I', Items.IRON_INGOT)
                .input('S', ModItems.SELETHILITE)
                .input('T', ModItems.LINGOTE_ACERO)

                .criterion(hasItem(ModItems.SELETHILITE), conditionsFromItem(ModItems.SELETHILITE))

                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.PROPULSOR, 1)
                .pattern("TCT") //
                .pattern("CNC") //
                .pattern("IUI") //

                .input('C', ModItems.CELULA_ENERGIA)
                .input('N', ModItems.NUCLEO_PROPULSION)
                .input('T', ModItems.LINGOTE_ACERO)
                .input('I', Items.IRON_INGOT)
                .input('U', Items.COPPER_INGOT)

                .criterion(hasItem(ModItems.CELULA_ENERGIA), conditionsFromItem(ModItems.CELULA_ENERGIA))

                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SELETHILITE_BLOCK, 1)
                .pattern("SSS") //
                .pattern("SSS") //
                .pattern("SSS") //

                .input('S', ModItems.SELETHILITE)

                .criterion(hasItem(ModItems.SELETHILITE), conditionsFromItem(ModItems.SELETHILITE))

                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.POLVO_ACERO, 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")

                .input('I', Items.IRON_INGOT)
                .input('C', Items.COAL)

                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))

                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.BARRA_ACERO, 1)
                .pattern("   ")
                .pattern(" S ")
                .pattern(" S ")

                .input('S', ModItems.LINGOTE_ACERO)

                .criterion(hasItem(ModItems.LINGOTE_ACERO), conditionsFromItem(ModItems.LINGOTE_ACERO))

                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.HOJA_ACERO, 1)
                .pattern(" S ")
                .pattern(" S ")
                .pattern(" S ")

                .input('S', ModItems.LINGOTE_ACERO)

                .criterion(hasItem(ModItems.LINGOTE_ACERO), conditionsFromItem(ModItems.LINGOTE_ACERO))

                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MANGO_ACERO, 1)
                .pattern("   ")
                .pattern("RG ")
                .pattern("BR ")

                .input('G', ModBlocks.ENGRANAJE)
                .input('B', ModItems.BARRA_ACERO)
                .input('R', Items.REDSTONE)

                .criterion(hasItem(ModItems.BARRA_ACERO), conditionsFromItem(ModItems.BARRA_ACERO))

                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ESPADA_MECANICA_BASE, 1)
                .pattern("  H")
                .pattern("CG ")
                .pattern("MC ")

                .input('C', ModItems.CELULA_ENERGIA)
                .input('M', ModItems.MANGO_ACERO)
                .input('H', ModItems.HOJA_ACERO)
                .input('G', ModBlocks.ENGRANAJE)

                .criterion(hasItem(ModItems.MANGO_ACERO), conditionsFromItem(ModItems.MANGO_ACERO))

                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.MECHANICAL_SWORD_UPGRADE_COMPONENT, 1)
                .pattern("   ")
                .pattern("NHN")
                .pattern("   ")

                .input('H', ModItems.HOJA_ACERO)
                .input('N', Items.NETHERITE_INGOT)

                .criterion(hasItem(ModItems.HOJA_ACERO), conditionsFromItem(ModItems.HOJA_ACERO))

                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.POLVO_ACERO, 1)
                .pattern(" I ")
                .pattern("ICI")
                .pattern(" I ")

                .input('I', Items.IRON_INGOT)
                .input('C', Items.CHARCOAL)

                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))

                .offerTo(exporter, Identifier.of("vaultfall", "polvo_acero_charcoal"));

        offerBlasting(
                exporter,
                List.of(ModItems.POLVO_ACERO),
                RecipeCategory.MISC,
                ModItems.LINGOTE_ACERO,
                0.7f,
                200,
                "lingote_acero"
        );

        SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(ModItems.PLANTILLA_SELETHILITE),
                        Ingredient.ofItems(ModItems.ESPADA_MECANICA_BASE),
                        Ingredient.ofItems(ModItems.MECHANICAL_SWORD_UPGRADE_COMPONENT),
                        RecipeCategory.COMBAT,
                        ModItems.ESPADA_MECANICA_MEJORADA
                )
                .criterion(hasItem(ModItems.MECHANICAL_SWORD_UPGRADE_COMPONENT), conditionsFromItem(ModItems.MECHANICAL_SWORD_UPGRADE_COMPONENT))
                .offerTo(exporter, net.minecraft.util.Identifier.of("vaultfall", "upgraded_mechanical_sword_smithing"));
    }
}