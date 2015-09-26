package zero.mods.zerocore.client.model.obj;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureCoordinate {

    public float u, v, w;

    public TextureCoordinate(float u, float v) {

        this(u, v, 0F);
    }

    public TextureCoordinate(float u, float v, float w) {

        this.u = u;
        this.v = v;
        this.w = w;
    }
}