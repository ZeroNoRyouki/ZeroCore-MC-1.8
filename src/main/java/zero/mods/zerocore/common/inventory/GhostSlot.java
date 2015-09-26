package zero.mods.zerocore.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GhostSlot extends Slot implements IGhostSlot {

    public GhostSlot(IInventory inventory, int index, int xPosition, int yPosition) {

        super(inventory, index, xPosition, yPosition);
    }

    @Override
    public boolean playerInteractionAllowed() {

        return true;
    }



    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    @Override
    public ItemStack decrStackSize(int amount) {

        return null;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    /*@Override
    public boolean isItemValid(ItemStack stack) {

        return false;
    }*/

    /**
     * Helper method to put a stack in the slot.
     */
    /*
    @Override
    public void putStack(ItemStack stack) {

        if (null != stack)
            stack = stack.copy();

        super.putStack(stack);
    }
    */

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    @Override
    public boolean canTakeStack(EntityPlayer player) {

        return false;
    }
}
