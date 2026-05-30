package org.nico.vaultfall.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block ENGRANAJE = registerBlock(
            "engranaje",
            new GearBlock(
                    AbstractBlock.Settings.create() // ¡Creamos los settings desde cero!
                            .sounds(BlockSoundGroup.METAL) // Para que suene a metal
                            .breakInstantly()
                            .nonOpaque(),
                    false
            )
    );

    public static final Block ENGRANAJE_OXIDADO = registerBlock(
            "engranaje_oxidado",
            new GearBlock(
                    AbstractBlock.Settings.create() // ¡Creamos los settings desde cero!
                            .sounds(BlockSoundGroup.METAL) // Para que suene a metal
                            .breakInstantly()
                            .nonOpaque(),
                    true
            )
    );

    public static final Block SELETHILITE_ORE = registerBlock(
            "selethilite_ore",
            new Block(
                    AbstractBlock.Settings.create()
                            .requiresTool() // ¡Ojo! Este sí requiere herramienta por ser un mineral duro
                            .strength(3.0f, 3.0f) // Dureza idéntica al mineral de hierro vanilla
            )
    );

    public static final Block SELETHILITE_BLOCK = registerBlock(
            "selethilite_block",
            new Block(
                    AbstractBlock.Settings.create()
                            .requiresTool() // Requiere un pico para ser recuperado
                            .strength(5.0f, 6.0f) // Dureza y resistencia idénticas al bloque de esmeralda
                            .sounds(net.minecraft.sound.BlockSoundGroup.METAL) // Sonido metálico/cristalino al pisarlo o romperlo
            )
    );

    private static Block registerBlock(String name, Block block) {

        Registry.register(
                Registries.BLOCK,
                Identifier.of("vaultfall", name),
                block
        );

        Registry.register(
                Registries.ITEM,
                Identifier.of("vaultfall", name),
                new BlockItem(block, new Item.Settings())
        );

        return block;
    }

    public static void registerModBlocks() {
        System.out.println("Registrando bloques de Vaultfall");
    }
}