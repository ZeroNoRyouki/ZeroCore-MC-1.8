package zero.mods.zerocore.client.model;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FilenameUtils;
import zero.mods.zerocore.client.model.obj.ObjLoader;

@SideOnly(Side.CLIENT)
public class Loader {

    public static ICustomModel loadModel(ResourceLocation model, ResourceLocation texture) {

        assert model != null : "Model cannot be null";

        String ext = FilenameUtils.getExtension(model.getResourcePath());

        if ("obj".equalsIgnoreCase(ext))
            return ObjLoader.getInstance().loadModel(model, texture);

        return null;
    }

}
