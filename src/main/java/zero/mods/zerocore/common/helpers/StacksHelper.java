package zero.mods.zerocore.common.helpers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class StacksHelper {


    /**
     * Checks if two ItemStacks can be merged together
     *
     * @param a the first stack
     * @param b the second stack
     * @return true if stacks can be merged, false otherwise
     */
    public static boolean canItemStacksMerge(ItemStack a, ItemStack b) {

        return (null != a) && (null != b) && a.isItemEqual(b) && ItemStack.areItemStackTagsEqual(a, b);
    }




    private StacksHelper(){

    }
}
