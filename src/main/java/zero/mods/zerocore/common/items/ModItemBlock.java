package zero.mods.zerocore.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zero.mods.zerocore.common.blocks.IModBlock;

/**
 * A base class for all items of a mod that represent a block
 */
public abstract class ModItemBlock extends ItemBlock implements IModItem {

    /**
     * Constructor
     *
     * @param block The block for which create the item. It must implement IModBlock
     */
    public ModItemBlock(Block block) {

        super(block);

        if (!(block instanceof IModBlock))
            throw new IllegalArgumentException("The block must implement IModBlock");

        this.setUnlocalizedName(((IModBlock)block).getFullyQualifiedName());
        this.initItem();
    }

    /**
     * Get the fully qualified name of the item
     */
    public String getFullyQualifiedName() {

        return ((IModBlock)this.block).getFullyQualifiedName();
    }

    /**
     * register the model for rendering an item in an inventory
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels() {
    }

    /**
     * Called during object construction just before the item is registered in the GameRegistry
     * Implement this method to configure your item (creative-tab, etc)
     */
    protected abstract void initItem();


}
