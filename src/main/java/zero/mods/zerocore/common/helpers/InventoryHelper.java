package zero.mods.zerocore.common.helpers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public final class InventoryHelper {


    public static ItemStack decreaseStackSize(IInventory inventory, int slotIndex, int count) {

        ItemStack currentStack = inventory.getStackInSlot(slotIndex);

        if (null == currentStack)
            return null;

        ItemStack removed;

        if (currentStack.stackSize <= count) {

            removed = currentStack;
            inventory.setInventorySlotContents(slotIndex, null);

        } else {

            removed = currentStack.splitStack(count);

            if (0 == currentStack.stackSize)
                inventory.setInventorySlotContents(slotIndex, null);
        }

        inventory.markDirty();

        return removed;
    }

    public static IChatComponent getDisplayName(IInventory inventory) {

        return inventory.hasCustomName() ? new ChatComponentText(inventory.getName()) : new ChatComponentTranslation(inventory.getName(), new Object[0]);
    }




    private InventoryHelper() {
    }

}
