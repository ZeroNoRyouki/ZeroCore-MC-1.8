package zero.mods.zerocore.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import java.util.Random;

/**
 * A block that drop a custom item when mined
 */
public abstract class ModBlockWithCustomDrop extends ModBlock {

    /**
     * Constructor
     *
     * @param name The mod-unique name of the block
     * @param material The base material for the block
     * @param droppedItem The item to drop when the block is mined
     * @param droppedItemDamage The damage value for the dropped item
     */
    public ModBlockWithCustomDrop(String name, Material material, Item droppedItem, int droppedItemDamage) {

        super(name, material);

        if (null == droppedItem)
            throw new IllegalArgumentException("Argument droppedItem cannot be null");

        if (droppedItemDamage < 0 || droppedItemDamage > Short.MAX_VALUE)
            throw new IllegalArgumentException("Invalid droppedItemDamage value");

        this._drop = droppedItem;
        this._damage = (short)droppedItemDamage;
    }

    public Item getItemDropped(IBlockState state, Random random, int fortune) {

        return this._drop;
    }

    public int damageDropped(IBlockState state) {

        return this._damage;
    }

    private final Item _drop;
    private final short _damage;
}
