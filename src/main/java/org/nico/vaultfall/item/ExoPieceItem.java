package org.nico.vaultfall.item;

import dev.emi.trinkets.api.Trinket; // <-- IMPORTANTE IMPORTAR ESTO
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

// Añadimos "implements Trinket"
public class ExoPieceItem extends Item implements Trinket {

    public ExoPieceItem(Settings settings) {
        super(settings
                .maxCount(1)
                .component(
                        DataComponentTypes.CONTAINER,
                        ContainerComponent.fromStacks(DefaultedList.ofSize(1, ItemStack.EMPTY))
                )
        );
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.vaultfall.exo_slots").formatted(Formatting.GRAY));
    }

    // Al implementar Trinket, heredamos mágicamente el método "tick".
    // Lo usaremos más adelante para darle poder a los módulos.
}