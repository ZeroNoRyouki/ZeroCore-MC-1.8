package zero.mods.zerocore.common.blocks;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zero.mods.zerocore.client.helpers.Render;
import zero.mods.zerocore.common.helpers.ModObjects;
import zero.mods.zerocore.common.items.ModItemBlock;

import java.util.Map;

/**
 * A block with multiple states
 */
public abstract class ModBlockWithState extends ModBlock implements IBlockStateAware {

    public ModBlockWithState(String name, Material material, boolean hasSubTypes) {

        this(name, material, hasSubTypes, ItemBlockWithState.class);
    }

    public ModBlockWithState(String name, Material material, boolean hasSubTypes, Class<? extends ItemBlockWithState> itemClass) {

        super(name, material, itemClass, Boolean.valueOf(hasSubTypes));
    }

    @Override
    public int getMetaDataFromItemDamage(int damage) {

        return damage;
    }

    /**
     * register the model for rendering an item in an inventory
     * Call this method while handling the FMLInitializationEvent event in your client proxy
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels() {

        Map<Integer, String> map = ModBlockWithState.s_modelSuffixMap;

        if (null == map)
            ModBlockWithState.s_modelSuffixMap = map = Maps.newHashMap();

        this.getSubBlocksNamesMap(map);

        if (map.isEmpty()) {

            super.registerModels();
            return;
        }


        Item item = Item.getItemFromBlock(this);
        String subBlockName, modId = ModObjects.getModIdFromActiveModContainer();
        String[] modelNames = new String[map.size()];
        int modelNameIndex = 0;

        for (Map.Entry<Integer, String> e : map.entrySet()) {

            subBlockName = e.getValue();

            if ((null == subBlockName) || subBlockName.isEmpty())
                continue;

            if (-1 == subBlockName.indexOf(':'))
                subBlockName = ModObjects.formatFullyQualifiedName(modId, subBlockName);

            modelNames[modelNameIndex++] = subBlockName;
            Render.registerItemModel(item, e.getKey(), subBlockName);
        }

        ModelBakery.addVariantName(item, modelNames);

        map.clear();
    }


    /**
     * Return the suggested facing for a indirectly block placed in the world (by World.setBlockState for example)
     *
     * @param world the current world
     * @param position position of the block
     * @param currentFacing the current facing
     * @return the new facing for the block based on the surrounding blocks
     */
    protected EnumFacing suggestDefaultFacing(World world, BlockPos position, EnumFacing currentFacing) {

        EnumFacing oppositeFacing = currentFacing.getOpposite();
        Block facingBlock = world.getBlockState(position.offset(currentFacing)).getBlock();
        Block oppositeBlock = world.getBlockState(position.offset(oppositeFacing)).getBlock();

        return facingBlock.isFullBlock() && !oppositeBlock.isFullBlock() ? oppositeFacing : currentFacing;
    }



    /**
     * Get the meta-data / models names map for all the sub-blocks of this block
     * Called during models registration by {@code registerModels}
     *
     * @param map Add all the model names of the sub-blocks using the respective meta-data as the map key.
     *            If the model name is not fully qualified, the modId of the calling mod will be used to qualify the name
     */
    @SideOnly(Side.CLIENT)
    protected void getSubBlocksNamesMap(Map<Integer, String> map) {
    }

    @SideOnly(Side.CLIENT)
    private static Map<Integer, String> s_modelSuffixMap = null;



    protected static class ItemBlockWithState extends ModItemBlock {

        public ItemBlockWithState(Block block, Boolean hasSubTypes) {

            super(block);

            if (!(block instanceof ModBlockWithState))
                throw new IllegalArgumentException("The block must be a subclass of ModBlockWithState");

            if (hasSubTypes) {

                this.setHasSubtypes(true);
                this.setMaxDamage(0);
            }
        }

        @Override
        protected void initItem() {
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {

            String baseName = super.getUnlocalizedName(stack);
            String stateSuffix = ((IBlockStateAware)this.block).getBlockNameStateSuffix(stack);

            return (null != stateSuffix) && !stateSuffix.isEmpty() ? baseName + "_" + stateSuffix : baseName;
        }

        /**
         * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
         * placed as a Block (mostly used with ItemBlocks).
         */
        @Override
        public int getMetadata(int damage) {

            return ((IBlockStateAware)this.block).getMetaDataFromItemDamage(damage);
        }

    }
}
