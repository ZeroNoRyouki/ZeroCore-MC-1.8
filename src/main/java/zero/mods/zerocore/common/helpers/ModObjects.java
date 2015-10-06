package zero.mods.zerocore.common.helpers;

import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

/**
 * A collection of helper function for handling mod objects
 */
public final class ModObjects {

    public static final char FQN_SEPARATOR_NAME = ':';
    public static final char FQN_SEPARATOR_META = '_';
    public static final char FQN_SEPARATOR_SUFFIX = '.';

    /**
     * Format the fully qualified name for an object
     *
     * @param modId The ID of the mod that own the object. If null or an empty string, the ID from the active mod container will be used instead (see {@see getModIdFromActiveModContainer}
     * @param objectName The mod-unique name of the object
     */
    public static String formatFullyQualifiedName(String modId, String objectName) {

        if ((null == modId) || modId.isEmpty())
            modId = ModObjects.getModIdFromActiveModContainer();

        if ((null == objectName) || objectName.isEmpty())
            throw new IllegalArgumentException("Invalid object name");


        StringBuilder sb = new StringBuilder(modId.length() + 1 + objectName.length());

        sb.append(modId);
        sb.append(ModObjects.FQN_SEPARATOR_NAME);
        sb.append(objectName);

        return sb.toString();
    }

    /**
     * Format the fully qualified name for a sub-object
     *
     * @param modId The ID of the mod that own the object. If null or an empty string, the ID from the active mod container will be used instead (see {@see getModIdFromActiveModContainer}
     * @param objectName The mod-unique name of the object
     * @param metaData An optional meta data value for the sub object, values lower than zero are ignored. You must supply a meta data value and/ore a suffix value
     * @param suffix An optional suffix. You must supply a meta data value and/ore a suffix value
     */
    public static String formatSubOjectFullyQualifiedName(String modId, String objectName, int metaData, String suffix) {

        if ((null == modId) || modId.isEmpty())
            modId = ModObjects.getModIdFromActiveModContainer();

        if ((null == objectName) || objectName.isEmpty())
            throw new IllegalArgumentException("Invalid object name");


        boolean useMetadata = metaData >= 0;
        boolean useSuffix = (null != suffix) && !suffix.isEmpty();

        if (!useMetadata && !useSuffix)
            throw new IllegalArgumentException("You must supply a meta data value and/or a suffix");


        int length = modId.length() + 1 + objectName.length() + (useMetadata ? 6 : 0) + (useSuffix ? suffix.length() + 1 : 0);
        StringBuilder sb = new StringBuilder(length);

        sb.append(modId);
        sb.append(ModObjects.FQN_SEPARATOR_NAME);
        sb.append(objectName);

        if (useMetadata) {

            sb.append(ModObjects.FQN_SEPARATOR_META);
            sb.append(metaData);
        }

        if (useSuffix) {

            sb.append(ModObjects.FQN_SEPARATOR_SUFFIX);
            sb.append(suffix);
        }

        return sb.toString();
    }

    /**
     * Format the fully qualified name for a sub-object
     *
     * @param objectFullyQualifiedName The main object fully qualified name
     * @param metaData An optional meta data value for the sub object, values lower than zero are ignored. You must supply a meta data value and/ore a suffix value
     * @param suffix An optional suffix. You must supply a meta data value and/ore a suffix value
     */
    public static String formatSubOjectFullyQualifiedName(String objectFullyQualifiedName, int metaData, String suffix) {

        if ((null == objectFullyQualifiedName) || objectFullyQualifiedName.isEmpty())
            throw new IllegalArgumentException("Invalid object name");

        boolean useMetadata = metaData >= 0;
        boolean useSuffix = (null != suffix) && !suffix.isEmpty();

        if (!useMetadata && !useSuffix)
            throw new IllegalArgumentException("You must supply a meta data value and/or a suffix");


        int length = objectFullyQualifiedName.length() + (useMetadata ? 6 : 0) + (useSuffix ? suffix.length() + 1 : 0);
        StringBuilder sb = new StringBuilder(length);

        sb.append(objectFullyQualifiedName);

        if (useMetadata) {

            sb.append(ModObjects.FQN_SEPARATOR_META);
            sb.append(metaData);
        }

        if (useSuffix) {

            sb.append(ModObjects.FQN_SEPARATOR_SUFFIX);
            sb.append(suffix);
        }

        return sb.toString();
    }

    /**
     * Return the object name from it's fully qualified name or from a sub-object fully qualified name
     *
     * @param fullyQualifiedName The fully qualified name of the object or sub-object
     */
    public static String getNameFromFullyQualifiedName(String fullyQualifiedName) {

        if ((null == fullyQualifiedName) || fullyQualifiedName.isEmpty())
            throw new IllegalArgumentException("Invalid object name");

        int indexNameSeparator = fullyQualifiedName.indexOf(ModObjects.FQN_SEPARATOR_NAME);
        int indexMetaSeparator = fullyQualifiedName.indexOf(ModObjects.FQN_SEPARATOR_META, indexNameSeparator);

        if (indexNameSeparator < 0)
            throw new IllegalArgumentException("Invalid object name");

        ++indexNameSeparator;

        return indexMetaSeparator > indexNameSeparator ?
                fullyQualifiedName.substring(indexNameSeparator, indexMetaSeparator - indexNameSeparator) :
                fullyQualifiedName.substring(indexNameSeparator);
    }


    public static boolean isFullyQualifiedName(String name) {

        return (null != name) && !name.isEmpty() && name.indexOf(ModObjects.FQN_SEPARATOR_NAME) > 0;
    }

    /**
     * Retrieve the ID of the mod from FML active mod container
     * Only call this method while processing a FMLEvent (or derived classes)
     */
    public static String getModIdFromActiveModContainer() {

        ModContainer mc = Loader.instance().activeModContainer();
        String modId = null != mc ? mc.getModId() : null;

        if ((null == modId) || modId.isEmpty())
            throw new RuntimeException("Cannot retrieve the MOD ID from FML");

        return modId;
    }

    /**
     * expand the size of the vanilla potions registry to accomodate the given number of custom potions
     *
     * @param customPotionsCount number of custom potions to make space for in the vanilla registry
     * @return pass this value to getNextCustomPotionId() to get the first valid id for the new potions
     */
    public static int expandVanillaPotionsRegistry(int customPotionsCount) {

        int suggestedFirstId = -1;
        int currentSize = Potion.potionTypes.length;

        if (currentSize < ModObjects.MAX_POTIONS_ARRAY_LENGTH - customPotionsCount) {

            Potion[] newArray = new Potion[currentSize + customPotionsCount];
            System.arraycopy(Potion.potionTypes, 0, newArray, 0, currentSize);
            CodeHelper.setObjectPrivateFinalField(Potion.class, null, newArray, new String[]{"potionTypes", "field_76425_a", "a"});
            suggestedFirstId = currentSize;
        }

        return suggestedFirstId;
    }

    /**
     * return a valid id for a new custom potion registration
     *
     * @param suggestedId for the first call, pass in the value returned by expandVanillaPotionsRegistry() and then the value returned by this function
     * @return a valid id for a new custom potion
     */
    public static int getNextCustomPotionId(int suggestedId) {

        if ((null != Potion.potionTypes) && (suggestedId > 0) && (suggestedId < Potion.potionTypes.length) &&
                (null == Potion.potionTypes[suggestedId]))
            return suggestedId;

        ++suggestedId;

        return (suggestedId < ModObjects.MAX_POTIONS_ARRAY_LENGTH) ? ModObjects.getNextCustomPotionId(suggestedId) : -1;
    }




    private static final int MAX_POTIONS_ARRAY_LENGTH = 128;

    private ModObjects() {
    }
}
