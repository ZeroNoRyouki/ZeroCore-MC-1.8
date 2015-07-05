package zero.mods.zerocore.common;

public interface IModInstance extends IModInitializationHandler {

    /**
     * Get the mod-id of the mod instance
     * Please note that the value returned MUST be in it's lower case form
     *
     * @return the mod-id
     */
    String getModId();

    /**
     * Get the currently active sided proxy
     *
     * @return the current proxy for the mod
     */
    ISidedProxy getProxy();

}
