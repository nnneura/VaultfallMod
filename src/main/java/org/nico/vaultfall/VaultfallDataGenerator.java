package org.nico.vaultfall;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.nico.vaultfall.datagen.VaultfallBlockLootTableProvider;
import org.nico.vaultfall.datagen.VaultfallBlockTagProvider;
import org.nico.vaultfall.datagen.VaultfallModelProvider;
import org.nico.vaultfall.datagen.VaultfallRecipeProvider;

public class VaultfallDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(VaultfallRecipeProvider::new);
		pack.addProvider(VaultfallModelProvider::new);
		pack.addProvider(VaultfallBlockLootTableProvider::new);
		pack.addProvider(VaultfallBlockTagProvider::new);
	}
}