package zero.mods.zerocore.client.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
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

    /**
     * bind the given texture in the minecraft render engine
     *
     * @param resourceLocation the texture to bind
     */
    public static void minecraftBindTexture(ResourceLocation resourceLocation) {

        Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);
    }

    /**
     * compute the width of a generic progress bar (or similar widget) based on the current state of a processing job
     *
     * @param maxWidth the maximum width, in pixels, of the progress bar
     * @param maxProcessing the value corresponding to the "end-of-job" state
     * @param currentProcessing the value corresponding to the current state of the job
     * @return the width, in pixel, of the progress bar for the current state of the processing job
     */
    public static int computeGuiProgressBarWidth(int maxWidth, int maxProcessing, int currentProcessing) {

        return maxProcessing != 0 && currentProcessing != 0 ? currentProcessing * maxWidth / maxProcessing : 0;
    }

    /**
     * an helper method to retrive the ItemModelMesher
     *
     * @return the ItemModelMesher. this value is cached
     */
    private static ItemModelMesher getItemModelMesher() {

        if (null == Render.s_itemModelMesher)
            Render.s_itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        return Render.s_itemModelMesher;
    }

    private Render() {
    }

    private static ItemModelMesher s_itemModelMesher;
}
