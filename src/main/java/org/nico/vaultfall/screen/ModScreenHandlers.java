package org.nico.vaultfall.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.nico.vaultfall.Vaultfall;

public class ModScreenHandlers {

    public static final ScreenHandlerType<ExoModuleScreenHandler> EXO_MODULE_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(Vaultfall.MOD_ID, "exo_module"),
                    new ExtendedScreenHandlerType<>(ExoModuleScreenHandler::new, ItemStack.OPTIONAL_PACKET_CODEC.cast())
            );
    public static void registerScreenHandlers() {
        Vaultfall.LOGGER.info("Registering Screen Handlers for " + Vaultfall.MOD_ID);
    }
}