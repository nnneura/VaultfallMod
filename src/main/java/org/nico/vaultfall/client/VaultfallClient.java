package org.nico.vaultfall.client;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import org.nico.vaultfall.block.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import org.nico.vaultfall.client.render.ExoTorsoRenderer;
import org.nico.vaultfall.item.ModItems;
import org.nico.vaultfall.screen.ExoModuleScreen;
import org.nico.vaultfall.screen.ModScreenHandlers;

public class VaultfallClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Le decimos al motor gráfico que renderice los huecos transparentes de estos bloques
        HandledScreens.register(ModScreenHandlers.EXO_MODULE_SCREEN_HANDLER, ExoModuleScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REJILLA_ACERO, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LAMPARA_SELETHILITE, RenderLayer.getCutout());
        TrinketRendererRegistry.registerRenderer(ModItems.EXO_TORSO, new ExoTorsoRenderer());
    }
}