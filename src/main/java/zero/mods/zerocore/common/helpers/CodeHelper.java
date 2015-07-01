package zero.mods.zerocore.common.helpers;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by marco on 27/06/2015.
 */
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


}
