package zero.mods.zerocore.common.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zero.mods.zerocore.client.helpers.Render;
import zero.mods.zerocore.common.helpers.ModObjects;

/**
 * A base class for all items of a mod
 */
public abstract class ModItem extends Item implements IModItem {

    /**
     * Constructor
     *
     * @param name The mod-unique name of the item
     */
    public ModItem(String name) {

        this._fqName = ModObjects.formatFullyQualifiedName(ModObjects.getModIdFromActiveModContainer(), name);
        this.setUnlocalizedName(this._fqName);

        // init the item BEFORE registering it
        this.initItem();
        this.register(name);
    }

    /**
     * Get the fully qualified name of the item
     */
    public String getFullyQualifiedName() {

        return this._fqName;
    }

    /**
     * register the model for rendering an item in an inventory
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels() {

        Render.registerItemModel(this);
    }

    /**
     * Called during object construction just before the item is registered in the GameRegistry
     * Implement this method to configure your item (creative-tab, etc)
     */
    protected abstract void initItem();

    /**
     * register the item in the GameRegistry. Called at construction time
     */
    protected void register(String name) {

        GameRegistry.registerItem(this, name);
    }

    private final String _fqName;
}
