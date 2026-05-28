package org.nico.vaultfall;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ModInitializer;
import org.nico.vaultfall.item.ModItems;
import org.nico.vaultfall.item.ModItemGroups;

public class Vaultfall implements ModInitializer {
	public static final String MOD_ID = "vaultfall";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
	}
}