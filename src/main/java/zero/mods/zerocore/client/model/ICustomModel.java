package zero.mods.zerocore.client.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ICustomModel {

    void renderAll();
    void renderAll(Tessellator tessellator);
    void renderOnly(String... groupNames);
    void renderOnly(Tessellator tessellator, String... groupNames);
    void renderPart(String partName);
    void renderPart(Tessellator tessellator, String partName);
    void renderAllExcept(String... excludedGroupNames);
    void renderAllExcept(Tessellator tessellator, String... excludedGroupNames);
    ResourceLocation getTexture();
}
