package org.nico.vaultfall.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item ENGRANAJE_OXIDADO = registerItem("engranaje_oxidado", new Item(new Item.Settings()));
    public static final Item NUCLEO_PROPULSION = registerItem("nucleo_propulsion", new Item(new Item.Settings()));
    public static final Item ENGRANAJE = registerItem("engranaje", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of("vaultfall", name), item);
    }

    public static void registerModItems() {
        System.out.println("Registrando ítems");
    }
    
}