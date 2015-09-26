package zero.mods.zerocore.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;


public class PersistentInventoryCrafting extends InventoryCrafting implements IPersistInventoryClient {

    public PersistentInventoryCrafting(Container container, int width, int height) {

        super(container, width, height);

        this._inventory = null;
        this._container = container;
        this._slotsCount = width * height;
        this._firstSlotIndex = -1;
    }

    @Override
    public void connectToInventory(IPersistInventory inventory, int firstSlotIndex, int reservedSlotsCount) {

        this._inventory = inventory;
        this._firstSlotIndex = firstSlotIndex;

        if ((null == inventory) || (firstSlotIndex < 0) || (this._slotsCount != reservedSlotsCount))
            throw new IllegalArgumentException("Invalid inventory configuration");
    }

    @Override
    public ItemStack getStackInRowAndColumn(int row, int column) {

        return this.getStackInSlot(row + column * this.getWidth());
    }

    @Override
    public void clear() {

        for (int i = 0, idx = this._firstSlotIndex; i < this._slotsCount; ++i, ++idx)
            this._inventory.setInventorySlotContents(idx, null);
    }

    @Override
    public int getSizeInventory() {

        return this._slotsCount;
    }

    @Override
    public ItemStack getStackInSlot(int index) {

        return this.isSlotIndexValid(index) ? this._inventory.getStackInSlot(this._firstSlotIndex + index) : null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {

        if (!this.isSlotIndexValid(index))
            return null;

        ItemStack result = this._inventory.decrStackSize(this._firstSlotIndex + index, count);

        if (null != result)
            this._container.onCraftMatrixChanged(this);

        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {

        //return this.isSlotIndexValid(index) ? this._inventory.getStackInSlotOnClosing(this._firstSlotIndex + index) : null;
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

        if (this.isSlotIndexValid(index)) {

            this._inventory.setInventorySlotContents(this._firstSlotIndex + index, stack);
            this._container.onCraftMatrixChanged(this);
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {

        return this._inventory.isUseableByPlayer(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {

        return this.isSlotIndexValid(index) ? this._inventory.isItemValidForSlot(this._firstSlotIndex + index, stack) : false;
    }


    protected boolean isSlotIndexValid(int index) {

        return (index >= 0) && (index < this._slotsCount);
    }

    protected IPersistInventory _inventory;
    protected Container _container;
    protected int _firstSlotIndex;
    protected int _slotsCount;
}
