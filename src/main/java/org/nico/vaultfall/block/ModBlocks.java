package org.nico.vaultfall.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    // Bloque del Engranaje Limpio (isOxidized = false)
    public static final Block ENGRANAJE_BLOCK = registerBlock("engranaje_block",
            new GearBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS)
                    .breakInstantly()
                    .nonOpaque(), // Aquí se cierran los Settings
                    false));              // Aquí se cierra el constructor de GearBlock

    // Bloque del Engranaje Oxidado (isOxidized = true)
    public static final Block ENGRANAJE_OXIDADO_BLOCK = registerBlock("engranaje_oxidado_block",
            new GearBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS)
                    .breakInstantly()
                    .nonOpaque(), // Aquí se cierran los Settings
                    true));               // Aquí se cierra el constructor de GearBlock

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of("vaultfall", name), block);
    }

    public static void registerModBlocks() {
        // Método llamado para cargar los bloques estáticos al iniciar
    }
}