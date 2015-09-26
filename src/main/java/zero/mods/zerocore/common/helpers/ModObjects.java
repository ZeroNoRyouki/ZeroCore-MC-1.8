package zero.mods.zerocore.common.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.IChunkProvider;
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
