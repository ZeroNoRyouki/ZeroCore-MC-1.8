package zero.mods.zerocore.client.model.obj;


import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class GroupObject {

    public final String Name;
    public final ArrayList<Face> Faces;
    public int GLDrawingMode;

    public GroupObject(String name) {

        this(name, -1);
    }

    public GroupObject(String name, int glDrawingMode) {

        this.Name = name;
        this.Faces = new ArrayList<Face>();
        this.GLDrawingMode = glDrawingMode;
    }

    public void render() {

        if (this.Faces.size() > 0) {

            Tessellator tessellator = Tessellator.getInstance();

            tessellator.getWorldRenderer().startDrawing(this.GLDrawingMode);
            this.render(tessellator);
            tessellator.draw();
        }
    }

    public void render(Tessellator tessellator) {

        if (this.Faces.size() > 0) {
            for (Face face : this.Faces)
                face.addFaceForRender(tessellator);
        }
    }


}

