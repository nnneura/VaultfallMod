package org.nico.vaultfall.item;

import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item NUCLEO_PROPULSION =
            registerItem("nucleo_propulsion",
                    new Item(new Item.Settings()));

    public static final Item CELULA_ENERGIA =
            registerItem("celula_energia",
                    new Item(new Item.Settings()));

    public static final Item SELETHILITE =
            registerItem("selethilite",
                    new Item(new Item.Settings()));

    public static final Item POLVO_ACERO =
            registerItem("polvo_acero",
                    new Item(new Item.Settings()));

    public static final Item LINGOTE_ACERO =
            registerItem("lingote_acero",
                    new Item(new Item.Settings()));

    public static final Item PROPULSOR =
            registerItem("propulsor",
                    new Item(new Item.Settings()));

    public static final Item HOJA_ACERO =
            registerItem("hoja_acero",
                    new Item(new Item.Settings()));

    public static final Item MANGO_ACERO =
            registerItem("mango_acero",
                    new Item(new Item.Settings()));

    public static final Item BARRA_ACERO =
            registerItem("barra_acero",
                    new Item(new Item.Settings()));

    public static final Item MECHANICAL_SWORD_UPGRADE_COMPONENT =
            registerItem("mechanical_sword_upgrade_component",
                    new Item(new Item.Settings()));

    public static final Item PLANTILLA_SELETHILITE =
            registerItem("plantilla_selethilite",
                    new Item(new Item.Settings()));

    public static final Item SELETHILITE_UPGRADE_COMPONENT =
            registerItem("selethilite_upgrade_component",
                    new Item(new Item.Settings()));

    public static final Item NUCLEO_SELETHILITA =
            registerItem("nucleo_selethilita",
                    new Item(new Item.Settings()));

    public static final Item ESPADA_MECANICA_BASE = registerItem("espada_mecanica_base",
            new SwordItem(ModToolMaterials.STEEL_BASE, new Item.Settings()
                    .attributeModifiers(SwordItem.createAttributeModifiers(
                            ModToolMaterials.STEEL_BASE, 5, -2.2f
                    ))
            )
    );

    public static final Item ESPADA_MECANICA_MEJORADA = registerItem("espada_mecanica_mejorada",
            new SwordItem(

                    new ToolMaterial() {
                        @Override
                        public int getDurability() {
                            return 2500;
                        }

                        @Override
                        public float getMiningSpeedMultiplier() {
                            return ModToolMaterials.STEEL_BASE.getMiningSpeedMultiplier();
                        }

                        @Override
                        public float getAttackDamage() {
                            return ModToolMaterials.STEEL_BASE.getAttackDamage(); // 2.0f
                        }

                        @Override
                        public net.minecraft.registry.tag.TagKey<net.minecraft.block.Block> getInverseTag() {
                            return ModToolMaterials.STEEL_BASE.getInverseTag();
                        }

                        @Override
                        public int getEnchantability() {
                            return ModToolMaterials.STEEL_BASE.getEnchantability();
                        }

                        @Override
                        public net.minecraft.recipe.Ingredient getRepairIngredient() {
                            return ModToolMaterials.STEEL_BASE.getRepairIngredient();
                        }
                    },
                    new Item.Settings().attributeModifiers(
                            SwordItem.createAttributeModifiers(
                                    ModToolMaterials.STEEL_BASE,
                                    9,
                                    -2.2f
                            )
                    )
            )
    );

    public static final Item ESPADA_MECANICA_SELETHILITE = registerItem("espada_mecanica_selethilite",
            new SwordItem(

                    new ToolMaterial() {
                        @Override
                        public int getDurability() {
                            return 7555;
                        }

                        @Override
                        public float getMiningSpeedMultiplier() {
                            return ModToolMaterials.STEEL_BASE.getMiningSpeedMultiplier();
                        }

                        @Override
                        public float getAttackDamage() {
                            return ModToolMaterials.STEEL_BASE.getAttackDamage(); // 2.0f
                        }

                        @Override
                        public net.minecraft.registry.tag.TagKey<net.minecraft.block.Block> getInverseTag() {
                            return ModToolMaterials.STEEL_BASE.getInverseTag();
                        }

                        @Override
                        public int getEnchantability() {
                            return ModToolMaterials.STEEL_BASE.getEnchantability();
                        }

                        @Override
                        public net.minecraft.recipe.Ingredient getRepairIngredient() {
                            return ModToolMaterials.STEEL_BASE.getRepairIngredient();
                        }
                    },
                    new Item.Settings().attributeModifiers(
                            SwordItem.createAttributeModifiers(
                                    ModToolMaterials.STEEL_BASE,
                                    11,
                                    -2.2f
                            )
                    )
            )
    );

    private static Item registerItem(String name, Item item) {
        return Registry.register(
                Registries.ITEM,
                Identifier.of("vaultfall", name),
                item
        );
    }

    public static void registerModItems() {
        System.out.println("Registrando ítems");
    }
}