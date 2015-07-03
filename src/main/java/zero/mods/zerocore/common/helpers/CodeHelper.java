package zero.mods.zerocore.common.helpers;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class CodeHelper {

    /**
     * Test if we were called by the Server thread
     *
     * @param world A valid world instance
     */
    public static boolean calledByServer(IBlockAccess world) {

        // World.isRemote : true = client thread; false = server thread
        return !((World)world).isRemote;
    }

    /**
     * Test if we were called by the Client thread
     *
     * @param world A valid world instance
     */
    public static boolean calledByClient(IBlockAccess world) {

        // World.isRemote : true = client thread; false = server thread
        return ((World)world).isRemote;
    }

    /**
     * Retrieve the world random numbers generator from an IBlockAccess
     * In case of failure, a new Random object is retrived
     *
     * @param world the IBlockAccess for the world
     */
    public static Random getRNGFromWorld(IBlockAccess world) {

        return world instanceof World ? ((World)world).rand : new Random();
    }

}
