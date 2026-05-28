package org.nico.vaultfall.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItemGroups {
    public static final ItemGroup VAULTFALL_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.ENGRANAJE_OXIDADO)) // El icono será tu engranaje
            .displayName(Text.translatable("itemgroup.vaultfall.vaultfall")) // Nombre que aparecerá
            .entries((displayContext, entries) -> {
                entries.add(ModItems.ENGRANAJE_OXIDADO);
                entries.add(ModItems.NUCLEO_PROPULSION);
                entries.add(ModItems.ENGRANAJE);
            })
            .build();

    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of("vaultfall", "vaultfall"), VAULTFALL_GROUP);
    }
}
