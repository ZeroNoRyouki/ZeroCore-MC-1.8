package zero.mods.zerocore.client.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zero.mods.zerocore.common.blocks.ModBlock;
import zero.mods.zerocore.common.items.ModItem;

/**
 * A collection of helper function for handling client-size rendering
 */
@SideOnly(Side.CLIENT)
public final class Render {

    /**
     * register the model for rendering a block in an inventory as an item
     * The model json file must be placed in resources\assets\MOD_ID\models\item and named with the mod-unique name of the block as passed to the block constructor
     *
     * @param block the block witch model should be registered
     */
    public static void registerItemModel(ModBlock block) {

        Render.registerItemModel(Item.getItemFromBlock(block), 0, block.getFullyQualifiedName());
    }

    /**
     * register the model for rendering an item in an inventory
     * The model json file must be placed in resources\assets\MOD_ID\models\item and named with the mod-unique name of the item as passed to the item constructor
     *
     * @param item the item witch model should be registered
     */
    public static void registerItemModel(ModItem item) {

        Render.registerItemModel(item, 0, item.getFullyQualifiedName());
    }


    /**
     * register the model for rendering a block in an inventory as an item
     * The model json file must be placed in resources\assets\MOD_ID\models\item and named with the mod-unique name of the block as passed to the block constructor
     *
     * @param item the Item witch model should be registered
     * @param metaData the meta data for witch the model should be registered
     * @param modelName the name of the model to register
     */
    public static void registerItemModel(Item item, int metaData, String modelName) {

        Render.getItemModelMesher().register(item, metaData, new ModelResourceLocation(modelName, "inventory"));
    }


    private static ItemModelMesher getItemModelMesher() {

        if (null == Render.s_itemModelMesher)
            Render.s_itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        return Render.s_itemModelMesher;
    }

    private static ItemModelMesher s_itemModelMesher;
}
