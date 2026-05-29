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