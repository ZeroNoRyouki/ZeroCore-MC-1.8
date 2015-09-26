package zero.mods.zerocore.client.model;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ILoader {

    ICustomModel loadModel(ResourceLocation model, ResourceLocation texture);
}
