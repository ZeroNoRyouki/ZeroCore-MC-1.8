package zero.mods.zerocore.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import java.util.Random;

/**
 * A ore-like block that drop a random number of custom items (of the same tipe) when mined
 */
public class ModOreBlock extends ModBlockWithCustomDrop {

    /**
     * Constructor
     *
     * @param name The mod-unique name of the block
     * @param material The base material for the block
     * @param droppedItem The item to drop when the block is mined
     * @param droppedItemDamage The damage value of the item to drop
     * @param minDropped The minumum number of items to drop when the block is mined. The block will drop a random number of item between minDropped and maxDropped
     * @param maxDropped The maximum number of items to drop when the block is mined. The block will drop a random number of item between minDropped and maxDropped
     */
    public ModOreBlock(String name, Material material, Item droppedItem, int droppedItemDamage, int minDropped, int maxDropped) {

        super(name, material, droppedItem, droppedItemDamage);

        if (minDropped < 1 || minDropped > Short.MAX_VALUE || maxDropped < 1 || maxDropped > Short.MAX_VALUE)
            throw new IllegalArgumentException("minDropped and maxDropped must be greater than zero and lower or equal to Short.MAX_VALUE");

        if (maxDropped < minDropped)
            throw new IllegalArgumentException("maxDropped must be greater or equal to minDropped");

        this._minDropped = (short)minDropped;
        this._maxDropped = (short)maxDropped;
    }

    public int quantityDropped(Random random) {

        // Return how many item to drop choosing randomly in the range provided during construction

        return MathHelper.getRandomIntegerInRange(random, this._minDropped, this._maxDropped);
    }

    public int quantityDroppedWithBonus(int fortune, Random random) {

        // Return how many item to drop choosing randomly in the range provided during construction and applying the Fortune level of the tool used to mine the block

        int baseQuantity = this.quantityDropped(random);

        return fortune > 0 ? (baseQuantity * MathHelper.getRandomIntegerInRange(random, 1, fortune + 1)) : baseQuantity;
    }

    @Override
    protected void initBlock() {

        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setStepSound(this.soundTypePiston);
    }

    private final short _minDropped;
    private final short _maxDropped;
}
