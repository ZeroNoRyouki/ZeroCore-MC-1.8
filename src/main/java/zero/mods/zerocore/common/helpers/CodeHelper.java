package zero.mods.zerocore.common.helpers;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Random;

public class CodeHelper {

    /**
     * Get the side we are called on
     */
    public static Side getCallingSide() {

        return FMLCommonHandler.instance().getSide();
    }

    /**
     * Test if we were called by the Server thread or by another thread in a server environment
     *
     * @param world A valid world instance
     */
    public static boolean calledByServer(IBlockAccess world) {

        // World.isRemote : true = client thread; false = server thread
        return !((World)world).isRemote;
    }

    /**
     * Test if we were called by the Server thread or by another thread in a server environment
     */
    public static boolean calledByServer() {

        return Side.SERVER == CodeHelper.getCallingSide();
    }

    /**
     * Test if we were called by the Client thread or by another thread in a client-only or combined environment
     *
     * @param world A valid world instance
     */
    public static boolean calledByClient(IBlockAccess world) {

        // World.isRemote : true = client thread; false = server thread
        return ((World)world).isRemote;
    }

    /**
     * Test if we were called by the Client thread or by another thread in a client-only or combined environment
     */
    public static boolean calledByClient() {

        return Side.CLIENT == CodeHelper.getCallingSide();
    }

    /**
     * Retrieve the world random numbers generator from an IBlockAccess
     * In case of failure, a new Random object is instantiated
     *
     * @param world the IBlockAccess for the world
     */
    public static Random getRNGFromWorld(IBlockAccess world) {

        return world instanceof World ? ((World)world).rand : new Random();
    }

}
