package zero.mods.zerocore.common.helpers;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

/**
 * A collection of helper function for handling mod objects
 */
public final class ModObjects {

    public static final char FQN_SEPARATOR = ':';

    /**
     * Format the fully qualified name for an object
     *
     * @param modId The ID of the mod that own the object
     * @param objectName The mod-unique name of the object
     */
    public static String formatFullyQualifiedName(String modId, String objectName) {

        return modId + ModObjects.FQN_SEPARATOR + objectName;
    }

    /**
     * Return the object name from it's fully qualified name
     *
     * @param fullyQualifiedName The ID of the mod that own the object
     */
    public static String getNameFromFullyQualifiedName(String fullyQualifiedName) {

        int index = (null == fullyQualifiedName) || fullyQualifiedName.isEmpty() ? -1 : fullyQualifiedName.indexOf(ModObjects.FQN_SEPARATOR);

        return (-1 == index) ? fullyQualifiedName : fullyQualifiedName.substring(index + 1);
    }

    /**
     * Retrieve the ID of the mod from FML active mod container
     * Only call this method while processing a FMLEvent (or derived classes)
     */
    public static String getModIdFromActiveModContainer() {

        ModContainer mc = Loader.instance().activeModContainer();
        String modId = null != mc ? mc.getModId() : null;

        if (null == modId)
            throw new RuntimeException("Cannot retrieve the MOD ID from FML");

        return modId;
    }



}
