package org.nico.vaultfall.item;

import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public enum ModToolMaterials implements ToolMaterial {
    STEEL_BASE(BlockTags.INCORRECT_FOR_IRON_TOOL, 1555, 6.0f, 2.0f, 14);

    private final TagKey<Block> inverseTag;
    private final int durability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;

    ModToolMaterials(TagKey<Block> inverseTag, int durability, float miningSpeed, float attackDamage, int enchantability) {
        this.inverseTag = inverseTag;
        this.durability = durability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
    }

    @Override
    public int getDurability() { return this.durability; }
    @Override
    public float getMiningSpeedMultiplier() { return this.miningSpeed; }
    @Override
    public float getAttackDamage() { return this.attackDamage; }
    @Override
    public TagKey<Block> getInverseTag() { return this.inverseTag; }
    @Override
    public int getEnchantability() { return this.enchantability; }

    @Override
    public Ingredient getRepairIngredient() {
        // Se reparará usando tu nuevo lingote de acero en el yunque
        return Ingredient.ofItems(ModItems.LINGOTE_ACERO);
    }
}