package zero.mods.zerocore.internal;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import zero.mods.zerocore.common.IModInstance;
import zero.mods.zerocore.common.ISidedProxy;


@Mod(modid = References.MOD_ID, name = References.MOD_NAME, version = References.MOD_VERSION, dependencies = References.MOD_DEPENDENCIES)
public class ZeroCoreMod implements IModInstance {

    public static ZeroCoreMod getInstance() {

        return ZeroCoreMod.s_instance;
    }

    @Override
    public String getModId() {

        return References.MOD_ID;
    }

    @Override
    public ISidedProxy getProxy() {

        return ZeroCoreMod.s_proxy;
    }


    @Mod.EventHandler
    @Override
    public void onPreInit(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    @Override
    public void onInit(FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    @Override
    public void onPostInit(FMLPostInitializationEvent event) {
    }


    @Mod.Instance
    private static ZeroCoreMod s_instance;

    @SidedProxy (modId = References.MOD_ID, serverSide = References.COMMON_PROXY_CLASS, clientSide = References.CLIENT_PROXY_CLASS)
    private static CommonProxy s_proxy;

}
