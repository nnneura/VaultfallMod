package org.nico.vaultfall.util;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.nico.vaultfall.Vaultfall;

public class ModTags {
    public static class Items {
        // Tag para identificar qué ítems son módulos válidos para el exoesqueleto
        public static final TagKey<Item> EXO_MODULES = of("exo_modules");
        public static final TagKey<Item> HEAD_MODULES = of("head_modules");
        public static final TagKey<Item> CHEST_MODULES = of("chest_modules");
        public static final TagKey<Item> LEGS_MODULES = of("legs_modules");
        public static final TagKey<Item> FEET_MODULES = of("feet_modules");

        private static TagKey<Item> of(String name) {
            // En 1.21+ la ruta es RegistryKeys.ITEM
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(Vaultfall.MOD_ID, name));
        }
    }
}