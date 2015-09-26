package zero.mods.zerocore.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import zero.mods.zerocore.common.helpers.InventoryHelper;

public class PersistentInventoryCraftingResult implements IInventory, IPersistInventoryClient {

    public PersistentInventoryCraftingResult() {

        this._inventory = null;
        this._firstSlotIndex = -1;
    }

    @Override
    public void connectToInventory(IPersistInventory inventory, int firstSlotIndex, int reservedSlotsCount) {

        this._inventory = inventory;
        this._firstSlotIndex = firstSlotIndex;

        if ((null == inventory) || (firstSlotIndex < 0) || (1 != reservedSlotsCount))
            throw new IllegalArgumentException("Invalid inventory configuration");
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {

        return 1;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int index) {

        return this._inventory.getStackInSlot(this._firstSlotIndex);
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getName() {

        return "Result";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName() {

        return false;
    }

    public IChatComponent getDisplayName() {

        return InventoryHelper.getDisplayName(this);
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int index, int count) {

        ItemStack result = this.getStackInSlot(0);

        if (null != result)
            this.setInventorySlotContents(0, null);

        return result;
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int index) {

        return null;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack) {

        this._inventory.setInventorySlotContents(this._firstSlotIndex, stack);
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit() {

        return 64;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty() {
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player) {

        return this._inventory.isUseableByPlayer(player);
    }

    public void openInventory(EntityPlayer player) {
    }

    public void closeInventory(EntityPlayer player) {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int index, ItemStack stack) {

        return true;
    }

    public int getField(int id) {

        return 0;
    }

    public void setField(int id, int value) {
    }

    public int getFieldCount() {

        return 0;
    }

    public void clear() {

        this.setInventorySlotContents(0, null);
    }

    protected IPersistInventory _inventory;
    protected int _firstSlotIndex;
}
