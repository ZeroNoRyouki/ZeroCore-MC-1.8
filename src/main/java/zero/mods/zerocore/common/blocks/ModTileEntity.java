package zero.mods.zerocore.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import zero.mods.zerocore.common.IModAccessor;

/**
 * A base class for all the tile entities of a mod
 */
public abstract class ModTileEntity extends TileEntity implements IModAccessor {

    /**
     * Check if the tile entity has a GUI or not
     * Override in derived classes to return true if your tile entity got a GUI
     */
    public boolean canOpenGui() {

        return false;
    }

    /**
     * Open the default GUI of this tile entity
     * Use along with ModGuiHandler
     *
     * @param player the player currently interacting with your block/tile entity
     * @return true if the GUI was opened, false otherwise
     */
    public boolean openGui(EntityPlayer player) {

        return this.openGui(player, -1);
    }

    /**
     * Open the specified GUI
     *
     * @param player the player currently interacting with your block/tile entity
     * @param guiId the GUI to open
     * @return true if the GUI was opened, false otherwise
     */
    public boolean openGui(EntityPlayer player, int guiId) {

        if (this.canOpenGui()) {

            player.openGui(this.getMod(), guiId, this.worldObj, this.pos.getX(), this.pos.getY(), this.pos.getZ());
            return true;
        }

        return false;
    }

    /**
     * Returns a Server side Container to be displayed to the user.
     *
     * @param playerInventory The inventory of the player
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    public Object getServerGuiElement(InventoryPlayer playerInventory) {

        return null;
    }

    /**
     * Returns a Container to be displayed to the user. On the client side, this
     * needs to return a instance of GuiScreen On the server side, this needs to
     * return a instance of Container
     *
     * @param playerInventory The inventory of the player
     * @return A GuiScreen/Container to be displayed to the user, null if none.
     */
    public Object getClientGuiElement(InventoryPlayer playerInventory) {

        return null;
    }

}
