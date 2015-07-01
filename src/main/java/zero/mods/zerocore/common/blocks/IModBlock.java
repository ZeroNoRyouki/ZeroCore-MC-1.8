package zero.mods.zerocore.common.blocks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by marco on 29/05/2015.
 */
public interface IModBlock {

    /**
     * Get the fully qualified name of the item
     */
    String getFullyQualifiedName();

    /**
     * register the model for rendering an item in an inventory
     * Call this method while handling the FMLInitializationEvent event in your client proxy
     */

    @SideOnly(Side.CLIENT)
    void registerModels();
}
