package zero.mods.zerocore.common.lib;

import net.minecraft.util.EnumFacing;
import java.util.HashMap;

/**
 * A general purpose class to track the state of all 6 faces of a block
 *
 * Example usages:
 * - track which faces are exposed on the outside walls of a complex structure
 * - track which faces is connected to a face of a similar block
 *
 */
public class BlockFacings {

    public static final BlockFacings NONE;
    public static final BlockFacings ALL;
    public static final BlockFacings DOWN;
    public static final BlockFacings UP;
    public static final BlockFacings NORTH;
    public static final BlockFacings SOUTH;
    public static final BlockFacings WEST;
    public static final BlockFacings EAST;

    /**
     * Check if a specific face is "set"
     * @param facing the face to check
     * @return true if the face is "set", false otherwise
     */
    public boolean isSet(EnumFacing facing) {

        return 0 != (this._value & (1 << facing.getIndex()));
    }

    public boolean none() {
        return 0 == this._value;
    }

    public boolean all() {
        return 0x3f == this._value;
    }

    public boolean down() {
        return this.isSet(EnumFacing.DOWN);
    }

    public boolean up() {
        return this.isSet(EnumFacing.UP);
    }

    public boolean north() {
        return this.isSet(EnumFacing.NORTH);
    }

    public boolean south() {
        return this.isSet(EnumFacing.SOUTH);
    }

    public boolean west() {
        return this.isSet(EnumFacing.WEST);
    }

    public boolean east() {
        return this.isSet(EnumFacing.EAST);
    }

    /**
     * Return a BlockFacing object that describe the current facing with the given face set or unset
     *
     * @param facing the face to modify
     * @param value the new value for the state of the face
     * @return a BlockFacing object
     */
    public BlockFacings set(EnumFacing facing, boolean value) {

        byte newHash = this._value;

        if (value)
            newHash |= (1 << facing.getIndex());
        else
            newHash &= ~(1 << facing.getIndex());

        return BlockFacings.from(Byte.valueOf(newHash));
    }

    /**
     * Return a BlockFacing object that describe the passed in state
     * @param down the state of the "down" face
     * @param up the state of the "up" face
     * @param north the state of the "north" face
     * @param south the state of the "south" face
     * @param west the state of the "west" face
     * @param east the state of the "east" face
     * @return a BlockFacing object
     */
    public static BlockFacings from(boolean down, boolean up, boolean north, boolean south, boolean west, boolean east) {

        return BlockFacings.from(BlockFacings.computeHash(down, up, north, south, west, east));
    }

    private static BlockFacings from(Byte hash) {

        BlockFacings facings = BlockFacings.s_cache.get(hash);

        if (null == facings) {

            facings = new BlockFacings(hash.byteValue());
            BlockFacings.s_cache.put(hash, facings);
        }

        return facings;
    }

    private BlockFacings(byte value) {

        this._value = value;
    }

    private static Byte computeHash(boolean down, boolean up, boolean north, boolean south, boolean west, boolean east) {

        byte hash = 0;

        if (down)
            hash |= (1 << EnumFacing.DOWN.getIndex());

        if (up)
            hash |= (1 << EnumFacing.UP.getIndex());

        if (north)
            hash |= (1 << EnumFacing.NORTH.getIndex());

        if (south)
            hash |= (1 << EnumFacing.SOUTH.getIndex());

        if (west)
            hash |= (1 << EnumFacing.WEST.getIndex());

        if (east)
            hash |= (1 << EnumFacing.EAST.getIndex());

        return Byte.valueOf(hash);
    }

    private byte _value;


    private static HashMap<Byte, BlockFacings> s_cache;

    static {

        Byte hash;

        s_cache = new HashMap<Byte, BlockFacings>(8);

        hash = BlockFacings.computeHash(false, false, false, false, false, false);
        s_cache.put(hash, NONE = new BlockFacings(hash.byteValue()));

        hash = BlockFacings.computeHash(true, true, true, true, true, true);
        s_cache.put(hash, ALL = new BlockFacings(hash.byteValue()));

        hash = BlockFacings.computeHash(true, false, false, false, false, false);
        s_cache.put(hash, DOWN = new BlockFacings(hash.byteValue()));

        hash = BlockFacings.computeHash(false, true, false, false, false, false);
        s_cache.put(hash, UP = new BlockFacings(hash.byteValue()));

        hash = BlockFacings.computeHash(false, false, true, false, false, false);
        s_cache.put(hash, NORTH = new BlockFacings(hash.byteValue()));

        hash = BlockFacings.computeHash(false, false, false, true, false, false);
        s_cache.put(hash, SOUTH = new BlockFacings(hash.byteValue()));

        hash = BlockFacings.computeHash(false, false, false, false, true, false);
        s_cache.put(hash, WEST = new BlockFacings(hash.byteValue()));

        hash = BlockFacings.computeHash(false, false, false, false, false, true);
        s_cache.put(hash, EAST = new BlockFacings(hash.byteValue()));

    }

}
