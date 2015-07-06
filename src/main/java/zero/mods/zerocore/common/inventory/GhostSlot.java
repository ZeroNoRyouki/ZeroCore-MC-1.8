package zero.mods.zerocore.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class GhostSlot extends Slot {

    public GhostSlot(IInventory inventory, int index, int xPosition, int yPosition) {

        super(inventory, index, xPosition, yPosition);
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {

        return false;
    }
}
