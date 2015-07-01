package zero.mods.zerocore.common;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IModInitializationHandler {

    /**
     * Handle the mod pre-initialization event
     *
     * Common tasks:
     *  - create and register items
     *  - create and register blocks
     *
     * @param event the event data
     */
    void onPreInit(FMLPreInitializationEvent event);

    /**
     * Handle the mod initialization event
     *
     * Common tasks:
     *  - register recipes
     *  - register world gen handlers
     *  - register loot items
     *
     * Client tasks:
     *  - register items and blocks models
     *
     * @param event the event data
     */
    void onInit(FMLInitializationEvent event);

    /**
     * Handle the mod post-initialization event
     *
     * @param event the event data
     */
    void onPostInit(FMLPostInitializationEvent event);

}
