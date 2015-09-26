package zero.mods.zerocore.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import zero.mods.zerocore.common.helpers.CodeHelper;
import zero.mods.zerocore.common.helpers.InventoryHelper;
import zero.mods.zerocore.common.inventory.IPersistInventory;
import java.util.Arrays;

public abstract class ModTileEntityContainer extends ModTileEntity implements IPersistInventory {

    public ModTileEntityContainer(int inventorySize, int maxStackSize) {

        if (inventorySize <= 0)
            throw new IllegalArgumentException("Invalid inventory size");

        this._slots = new ItemStack[inventorySize];
        this._maxStackSize = maxStackSize;
    }

    @Override
    public void clear() {

        Arrays.fill(this._slots, null);
    }

    @Override
    public int getSizeInventory() {

        return this._slots.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {

        return this.isSlotIndexValid(index) ? this._slots[index] : null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {

        return InventoryHelper.decreaseStackSize(this, index, count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {

        ItemStack itemStack = this.getStackInSlot(index);

        if (itemStack != null)
            this.setInventorySlotContents(index, null);

        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

        if (!this.isSlotIndexValid(index))
            return;

        this._slots[index] = stack;

        if ((stack != null) && (stack.stackSize > this._maxStackSize))
            stack.stackSize = this._maxStackSize;

        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {

        return this._maxStackSize;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {

        return CodeHelper.isEntityInRange(player, this.pos, 8.0);
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {

        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatComponent getDisplayName() {

        return InventoryHelper.getDisplayName(this);
    }


    protected boolean isSlotIndexValid(int index) {

        return (index >= 0) && (index < this._slots.length);
    }

    private final ItemStack[] _slots;
    private final int _maxStackSize;
}
