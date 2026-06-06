package org.nico.vaultfall;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.screenhandler.Networking;
import org.nico.vaultfall.block.ModBlocks;
import org.nico.vaultfall.component.ModDataComponents;
import org.nico.vaultfall.effect.ModEffects;
import org.nico.vaultfall.event.ElytraModuleEvents;
import org.nico.vaultfall.networking.ModNetworking;
import org.nico.vaultfall.networking.ServerJumpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.nico.vaultfall.item.ModItems;
import org.nico.vaultfall.item.ModItemGroups;

public class Vaultfall implements ModInitializer {
	public static final String MOD_ID = "vaultfall";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ElytraModuleEvents.register();
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();
		ModEffects.registerEffects();
		ModDataComponents.register();
		ModItems.registerModItems();

		// REGISTRO DE RED: SOLO C2S EN EL SERVIDOR
		ModNetworking.registerC2SPayloads();
		ServerJumpHandler.register();

		LOGGER.info("[VAULTFALL] Inicialización completada");
	}
}