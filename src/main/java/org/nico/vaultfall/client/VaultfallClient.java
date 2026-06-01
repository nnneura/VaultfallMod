package org.nico.vaultfall.client;

import org.nico.vaultfall.block.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class VaultfallClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Le decimos al motor gráfico que renderice los huecos transparentes de estos bloques
        //BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REJILLA_ACERO, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LAMPARA_SELETHILITE, RenderLayer.getCutout());
    }
}