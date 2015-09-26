package zero.mods.zerocore.common.inventory;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.FMLLog;
import zero.mods.zerocore.common.helpers.ModObjects;
import zero.mods.zerocore.common.helpers.StacksHelper;

public abstract class ModContainer extends Container {

    public static final int VANILLA_HOTBAR_SLOTS_COUNT = InventoryPlayer.getHotbarSize();
    public static final int VANILLA_PLAYER_INVENTORY_COLUMNS = 9;
    public static final int VANILLA_PLAYER_INVENTORY_ROWS = 3;
    public static final int VANILLA_GUI_SLOT_SIZE = 18;

    public void addVanillaInventorySlots(InventoryPlayer playerInventory, int x, int yInventory, int yHotbar) {

        int slotIndex = 0, slotX, slotY, row, column;

        for (slotX = x; slotIndex < ModContainer.VANILLA_HOTBAR_SLOTS_COUNT; ++slotIndex, slotX += ModContainer.VANILLA_GUI_SLOT_SIZE)
            this.addSlotToContainer(new Slot(playerInventory, slotIndex, slotX, yHotbar));

        for (row = 0, slotY = yInventory; row < ModContainer.VANILLA_PLAYER_INVENTORY_ROWS; ++row, slotY += ModContainer.VANILLA_GUI_SLOT_SIZE)
            for (column = 0, slotX = x; column < ModContainer.VANILLA_PLAYER_INVENTORY_COLUMNS; ++column, ++slotIndex, slotX += ModContainer.VANILLA_GUI_SLOT_SIZE)
                this.addSlotToContainer(new Slot(playerInventory, slotIndex, slotX, slotY));
    }

    @Override
    public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer player) {

        //FMLLog.info("SLOTCK %d %d %d", slotId, clickedButton, mode);

        Slot clickedSlot = slotId >= 0 ? this.getSlot(slotId) : null;

        if (clickedSlot instanceof IGhostSlot)
            return this.handleGhostSlotClick(clickedSlot, clickedButton, mode, player);

        return super.slotClick(slotId, clickedButton, mode, player);
    }


    protected ItemStack handleGhostSlotClick(Slot clickedSlot, int clickedButton, int mode, EntityPlayer player) {

        FMLLog.info("GHOST SLOTCK %d %d %d", clickedSlot.getSlotIndex(), clickedButton, mode);

        boolean writable = ((IGhostSlot)clickedSlot).playerInteractionAllowed();
        int slotStackLimit = clickedSlot.getSlotStackLimit();
        boolean tunable = slotStackLimit > 1;
        ItemStack resultStack = null;

        if (2 == clickedButton) {

            if (writable)
                clickedSlot.putStack(null);

        } else if ((0 == clickedButton) || (1 == clickedButton)) {

            InventoryPlayer playerInventory = player.inventory;
            ItemStack slotStack, heldStack;

            clickedSlot.onSlotChanged();
            slotStack = clickedSlot.getStack();
            heldStack = playerInventory.getItemStack();

            if (null != slotStack)
                resultStack = slotStack.copy();

            if (null == slotStack) {

                if ((null != heldStack) && clickedSlot.isItemValid(heldStack) && writable)
                    this.fillGhostSlot(clickedSlot, heldStack, clickedButton, slotStackLimit);

            } else if (null == heldStack) {

                if (writable && tunable)
                    this.tuneGhostSlotStackSize(clickedSlot, clickedButton, slotStackLimit);

                clickedSlot.onPickupFromSlot(player, playerInventory.getItemStack());

            } else if (clickedSlot.isItemValid(heldStack) && writable) {

                if (StacksHelper.canItemStacksMerge(slotStack, heldStack) && tunable)
                    this.tuneGhostSlotStackSize(clickedSlot, clickedButton, slotStackLimit);
                else
                    this.fillGhostSlot(clickedSlot, heldStack, clickedButton, slotStackLimit);
            }
        }

        return resultStack;
    }

    private void fillGhostSlot(Slot clickedSlot, ItemStack heldStack, int clickedButton, int slotStackLimit) {

        ItemStack stack = heldStack.copy();

        stack.stackSize = MathHelper.clamp_int(0 == clickedButton ? heldStack.stackSize : 1, 0, slotStackLimit);
        clickedSlot.putStack(stack.stackSize > 0 ? stack : null);
    }

    private void tuneGhostSlotStackSize(Slot clickedSlot, int clickedButton, int slotStackLimit) {

        ItemStack slotStack = clickedSlot.getStack();

        if (null != slotStack) {

            int newSize = 0 == clickedButton ? slotStack.stackSize - 1 : slotStack.stackSize + 1;

            slotStack.stackSize = MathHelper.clamp_int(newSize, 0, slotStackLimit);

            if (0 == slotStack.stackSize)
                clickedSlot.putStack(null);
        }
    }
}
