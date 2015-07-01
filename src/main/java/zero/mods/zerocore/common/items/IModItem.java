package zero.mods.zerocore.common.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by marco on 29/05/2015.
 */
public interface IModItem {

    /**
     * Get the fully qualified name of the item
     */
    String getFullyQualifiedName();

    /**
     * register the model for rendering an item in an inventory
     */
    @SideOnly(Side.CLIENT)
    void registerModels();
}
