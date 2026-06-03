package org.nico.vaultfall.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class CustomSwordItem extends SwordItem {

    private final String[] tooltipKeys;

    public CustomSwordItem(ToolMaterial material, Settings settings, String... tooltipKeys) {
        super(material, settings);
        this.tooltipKeys = tooltipKeys;
    }

    @Override
    public Text getName(ItemStack stack) {
        // Forzamos el color de la rareza (ej. Amarillo para UNCOMMON) para evitar
        // que Vanilla tiña el nombre de azul claro automáticamente al encantar el arma.
        return super.getName(stack).copy().formatted(stack.getRarity().getFormatting());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        // 1. Renderizado Nativo de Vanilla
        // Esto delega a Minecraft la tarea de pintar el daño base, la velocidad de ataque,
        // la lista de encantamientos visuales y cualquier modificador inyectado por mods de cliente.
        super.appendTooltip(stack, context, tooltip, type);

        // 2. Renderizado de tu Lore Custom (Pasivas de Vaultfall)
        // Añadimos tu texto personalizado al final del tooltip nativo.
        for (String key : tooltipKeys) {
            tooltip.add(Text.translatable(key).formatted(Formatting.GRAY));
        }
    }
}