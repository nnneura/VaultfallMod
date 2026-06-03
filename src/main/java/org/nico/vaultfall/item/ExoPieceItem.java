package org.nico.vaultfall.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class ExoPieceItem extends Item {

    public ExoPieceItem(Settings settings) {
        // Forzamos que el ítem tenga un componente de contenedor de 1 slot por defecto
        super(settings
                .maxCount(1) // Las piezas de exoesqueleto no se pueden apilar
                .component(
                        DataComponentTypes.CONTAINER,
                        ContainerComponent.fromStacks(DefaultedList.ofSize(1, ItemStack.EMPTY))
                )
        );
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.vaultfall.exo_slots").formatted(Formatting.GRAY));

        // Más adelante, aquí leeremos el componente CONTAINER
        // para imprimir el nombre del módulo instalado directamente en el tooltip.
    }
}