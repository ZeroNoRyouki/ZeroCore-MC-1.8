package zero.mods.zerocore.client.model.obj;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Vertex {

    public float x, y, z;

    public Vertex(float x, float y) {

        this(x, y, 0F);
    }

    public Vertex(float x, float y, float z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }
}