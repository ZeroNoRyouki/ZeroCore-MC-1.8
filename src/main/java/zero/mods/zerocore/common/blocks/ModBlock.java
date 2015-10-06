package zero.mods.zerocore.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zero.mods.zerocore.client.helpers.Render;
import zero.mods.zerocore.common.helpers.ModObjects;

/**
 * A base class for all blocks of a mod
 */
public abstract class ModBlock extends Block implements IModBlock {

    /**
     * Constructor
     *
     * @param name The mod-unique name of the block
     * @param material The base material for the block
     */
    protected ModBlock(String name, Material material) {

        this(name, material, ItemBlock.class, new Object[]{});
    }

    /**
     * Constructor
     *
     * @param name The mod-unique name of the block
     * @param material The base material for the block
     * @param itemClass The associated item type. If null, no item will be associated
     */
    protected ModBlock(String name, Material material, Class<? extends ItemBlock> itemClass) {

        this(name, material, itemClass, new Object[]{});
    }

    /**
     * Constructor
     *
     * @param name The mod-unique name of the block
     * @param material The base material for the block
     * @param itemClass The associated item type. If null, no item will be associated
     * @param itemCtorArgs Arguments to pass (after the required {@code Block} parameter) to the ItemBlock constructor
     */
    protected ModBlock(String name, Material material, Class<? extends ItemBlock> itemClass, Object... itemCtorArgs) {

        super(material);

        this._fqName = ModObjects.formatFullyQualifiedName(null, name);
        this.setUnlocalizedName(this._fqName);

        // init the block BEFORE registering it
        this.initBlock();
        this.register(name, itemClass, itemCtorArgs);
    }

    /**
     * Retrieve the fully qualified name of the block
     */
    public String getFullyQualifiedName() {

        return this._fqName;
    }

    /**
     * register the model for rendering an item in an inventory
     * Call this method while handling the FMLInitializationEvent event in your client proxy
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels() {

        Render.registerItemModel(this);
    }

    /**
     * Register a tile entity for the block
     *
     * @param tileEntityClass the tile entity class to register
     */
    public void registerBlockTileEntity(Class<? extends TileEntity> tileEntityClass) {

        GameRegistry.registerTileEntity(tileEntityClass, ModTileEntity.formatFullyQualifiedName(this.getFullyQualifiedName()));
    }

    /**
     * Register a tile entity for the block
     *
     * @param tileEntityClass the tile entity class to register
     */
    public void registerBlockTileEntity(Class<? extends TileEntity> tileEntityClass, int metaData) {

        GameRegistry.registerTileEntity(tileEntityClass, ModTileEntity.formatFullyQualifiedName(this.getFullyQualifiedName(), metaData));
    }

    /**
     * Test if a block in the world is of the given type
     *
     * @param world A valid world instance
     * @param position Coordinates of the starting block
     * @param facing Facing to look at
     * @param type The block type to check for
     */
    public boolean isAdjacentBlockOfType(IBlockAccess world, BlockPos position, EnumFacing facing, Block type) {

        assert null != world : "world cannot be null";
        assert null != position : "position cannot be null";
        assert null != type : "type cannot be null";

        BlockPos newPosition = position.offset(facing);
        IBlockState blockState = world.getBlockState(newPosition);
        Block block = (null == blockState) ? null : blockState.getBlock();

        return type == block;
    }

    /**
     * Test if a block in the world is of the the same type of the caller block
     *
     * @param world A valid world instance
     * @param position Coordinates of the starting block
     * @param facing Facing to look at
     */
    public boolean isAdjacentBlockOfMyType(IBlockAccess world, BlockPos position, EnumFacing facing) {

        return this.isAdjacentBlockOfType(world, position, facing, this);
    }



    protected void debugDumpBlockState(IBlockState state, BlockPos position) {

        StringBuilder sb = new StringBuilder();

        sb.append("Block ");
        //sb.append(state.getBlock().getUnlocalizedName());
        sb.append(state.toString());

        if (null != position)
            sb.append(String.format(" at [%d, %d, %d]", position.getX(), position.getY(), position.getZ()));

        FMLLog.getLogger().info(sb.toString());
    }






    /**
     * Called during object construction just before the block is registered in the GameRegistry
     * Implement this method to configure your block (creative-tab, hardness, resistance, etc)
     */
    protected abstract void initBlock();

    /**
     * Register the block in the GameRegistry. Called at construction time
     */
    protected void register(String name, Class<? extends ItemBlock> itemClass, Object... itemCtorArgs) {

        GameRegistry.registerBlock(this, itemClass, name, itemCtorArgs);
    }



    private final String _fqName;
}