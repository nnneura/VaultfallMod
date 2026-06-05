package org.nico.vaultfall.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.nico.vaultfall.util.ModTags;

import java.util.List;

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
                    new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));

    public static final Item PLANTILLA_SELETHILITE =
            registerItem("plantilla_selethilite",
                    new Item(new Item.Settings().rarity(Rarity.RARE)));

    public static final Item SELETHILITE_UPGRADE_COMPONENT =
            registerItem("selethilite_upgrade_component",
                    new Item(new Item.Settings().rarity(Rarity.RARE)));

    public static final Item NUCLEO_SELETHILITA =
            registerItem("nucleo_selethilita",
                    new Item(new Item.Settings()));

    public static final Item EXO_CASCO = registerItem("exo_casco",
            new ExoPieceItem(new Item.Settings().rarity(Rarity.RARE).maxCount(1),
                    ModTags.Items.HEAD_MODULES)
    );

    public static final Item EXO_TORSO = registerItem("exo_torso",
            new ExoPieceItem(new Item.Settings().rarity(Rarity.RARE).maxCount(1),
                    ModTags.Items.CHEST_MODULES)
    );

    public static final Item EXO_PIERNAS = registerItem("exo_piernas",
            new ExoPieceItem(new Item.Settings().rarity(Rarity.RARE).maxCount(1),
                    ModTags.Items.LEGS_MODULES)
    );

    public static final Item EXO_BOTAS = registerItem("exo_botas",
            new ExoPieceItem(new Item.Settings().rarity(Rarity.RARE).maxCount(1),
                    ModTags.Items.FEET_MODULES)
    );

    public static final Item MODULO_VISION_NOCTURNA = registerItem("modulo_vision_nocturna",
            new Item(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).component(DataComponentTypes.LORE,
                    new LoreComponent(List.of(
                            Text.translatable("tooltip.vaultfall.modulo_vision_nocturna_line1").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_vision_nocturna_line2").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_vision_nocturna_line3").formatted(Formatting.DARK_PURPLE)
                    ))
            ))
    );

    public static final Item MODULO_RESPIRACION = registerItem("modulo_respiracion",
            new Item(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).component(DataComponentTypes.LORE,
                    new LoreComponent(List.of(
                            Text.translatable("tooltip.vaultfall.modulo_respiracion_line1").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_respiracion_line2").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_respiracion_line3").formatted(Formatting.DARK_PURPLE)
                    ))
            ))
    );

    public static final Item MODULO_ADRENALINA = registerItem("modulo_adrenalina",
            new Item(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).component(DataComponentTypes.LORE,
                    new LoreComponent(List.of(
                            Text.translatable("tooltip.vaultfall.modulo_adrenalina_line1").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_adrenalina_line2").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_adrenalina_line3").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_adrenalina_line4").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_adrenalina_line5").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_adrenalina_line6").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_adrenalina_line7").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_adrenalina_line8").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_adrenalina_line9").formatted(Formatting.DARK_PURPLE)
                    ))
            ))
    );

    public static final Item MODULO_ELYTRA = registerItem("modulo_elytra",
            new Item(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).component(DataComponentTypes.LORE,
                    new LoreComponent(List.of(
                            Text.translatable("tooltip.vaultfall.modulo_elytra_line1").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_elytra_line2").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_elytra_line3").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_elytra_line4").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_elytra_line5").formatted(Formatting.DARK_PURPLE)
                    ))
            ))
    );

    public static final Item MODULO_SPEED = registerItem("modulo_speed",
            new Item(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).component(DataComponentTypes.LORE,
                    new LoreComponent(List.of(
                            Text.translatable("tooltip.vaultfall.modulo_speed_line1").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_speed_line2").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_speed_line3").formatted(Formatting.DARK_PURPLE)

                    ))
            ))
    );

    public static final Item MODULO_KB_RESISTANCE = registerItem("modulo_kb_resistance",
            new Item(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).component(DataComponentTypes.LORE,
                    new LoreComponent(List.of(
                            Text.translatable("tooltip.vaultfall.modulo_kb_resistance_line1").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_kb_resistance_line2").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_kb_resistance_line3").formatted(Formatting.DARK_GRAY),
                            Text.translatable("tooltip.vaultfall.modulo_kb_resistance_line4").formatted(Formatting.DARK_PURPLE)
                    ))
            ))
    );


    public static final Item ESPADA_MECANICA_BASE = registerItem("espada_mecanica_base",
            new CustomSwordItem(
                    ModToolMaterials.STEEL_BASE,
                    new Item.Settings().attributeModifiers(
                            SwordItem.createAttributeModifiers(ModToolMaterials.STEEL_BASE, 5, -2.2f)
                    ),
                    "tooltip.vaultfall.espada_mecanica_base.line1",
                    "tooltip.vaultfall.espada_mecanica_base.line2"


            )
    );

    public static final Item ESPADA_MECANICA_MEJORADA = registerItem("espada_mecanica_mejorada",
            new CustomSwordItem(
                    ModToolMaterials.ENHANCED_STEEL, // Usamos el nuevo material del enum
                    new Item.Settings().rarity(Rarity.RARE).attributeModifiers(
                            SwordItem.createAttributeModifiers(ModToolMaterials.ENHANCED_STEEL, 9, -2.2f)
                    ),
                    "tooltip.vaultfall.espada_mecanica_mejorada.line1",
                    "tooltip.vaultfall.espada_mecanica_mejorada.line2",
                    "tooltip.vaultfall.espada_mecanica_mejorada.line3",
                    "tooltip.vaultfall.espada_mecanica_mejorada.line4"
            )
    );

    public static final Item ESPADA_MECANICA_SELETHILITE = registerItem("espada_mecanica_selethilite",
            new CustomSwordItem(
                    ModToolMaterials.SELETHILITE, // Usamos el nuevo material del enum
                    new Item.Settings().rarity(Rarity.EPIC).attributeModifiers(
                            SwordItem.createAttributeModifiers(ModToolMaterials.SELETHILITE, 11, -2.2f)
                    ),
                    "tooltip.vaultfall.espada_selethilite.line1",
                    "tooltip.vaultfall.espada_selethilite.line2",
                    "tooltip.vaultfall.espada_selethilite.line3",
                    "tooltip.vaultfall.espada_selethilite.line4",
                    "tooltip.vaultfall.espada_selethilite.line5",
                    "tooltip.vaultfall.espada_selethilite.line6"
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