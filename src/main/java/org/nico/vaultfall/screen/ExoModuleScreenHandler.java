package org.nico.vaultfall.screen;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.nico.vaultfall.item.ExoPieceItem;
import org.nico.vaultfall.util.ModTags;

import java.util.ArrayList;
import java.util.List;

public class ExoModuleScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final ItemStack exoPieceStack;
    private final TagKey<Item> allowedModules;
    private final int capacity;

    // --- CONSTRUCTOR DEL CLIENTE (1.21.1 ExtendedScreenHandlerType) ---
    // El cliente recibe el ItemStack directamente gracias al PacketCodec
    public ExoModuleScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack exoPieceStack) {
        // Lógica defensiva: Si el ítem no es ExoPieceItem, usa valores por defecto para evitar ClassCastException
        this(syncId, playerInventory,
                new SimpleInventory(exoPieceStack.getItem() instanceof ExoPieceItem exoItem ? exoItem.getModuleCapacity() : 1),
                exoPieceStack,
                exoPieceStack.getItem() instanceof ExoPieceItem exoItem ? exoItem.getValidModuleTag() : ModTags.Items.EXO_MODULES,
                exoPieceStack.getItem() instanceof ExoPieceItem exoItem ? exoItem.getModuleCapacity() : 1);
    }

    // --- CONSTRUCTOR DEL SERVIDOR ---
    public ExoModuleScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
                                  ItemStack exoPieceStack, TagKey<Item> allowedModules, int capacity) {
        super(ModScreenHandlers.EXO_MODULE_SCREEN_HANDLER, syncId);

        this.capacity = capacity;
        this.allowedModules = allowedModules;
        this.exoPieceStack = exoPieceStack;

        checkSize(inventory, this.capacity);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        // 1. Generación Dinámica y Centrada de Slots de Módulos
        int startX = 88 - ((this.capacity * 18) / 2) + 1;
        int startY = 25;

        for (int i = 0; i < this.capacity; i++) {
            this.addSlot(new Slot(inventory, i, startX + (i * 18), startY) {
                @Override
                public boolean canInsert(ItemStack stack) {
                    return stack.isIn(allowedModules);
                }
            });
        }

        // 2. Inventario del Jugador (Estático, 36 slots)
        int m, l;
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 51 + m * 18));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 109));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (!player.getWorld().isClient() && !exoPieceStack.isEmpty()) {
            List<ItemStack> moduleList = new ArrayList<>();

            for (int i = 0; i < this.capacity; i++) {
                ItemStack stackInSlot = inventory.getStack(i);
                if (!stackInSlot.isEmpty()) {
                    moduleList.add(stackInSlot.copy());
                }
            }

            exoPieceStack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(moduleList));
        }
        inventory.onClose(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot < this.capacity) {
                if (!this.insertItem(originalStack, this.capacity, this.capacity + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(originalStack, 0, this.capacity, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }
}