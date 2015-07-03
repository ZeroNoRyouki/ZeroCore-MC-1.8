package zero.mods.zerocore.common.blocks;

import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBlockStateAware {

    String getBlockNameStateSuffix(ItemStack stack);
    int getMetaDataFromItemDamage(int damage);
}
