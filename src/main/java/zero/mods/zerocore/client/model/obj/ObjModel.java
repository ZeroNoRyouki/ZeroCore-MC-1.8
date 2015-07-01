package zero.mods.zerocore.client.model.obj;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import zero.mods.zerocore.client.model.ICustomModel;

import java.util.ArrayList;


@SideOnly(Side.CLIENT)
class ObjModel implements ICustomModel {

    @Override
    public void renderAll() {

        Tessellator tessellator = Tessellator.getInstance();

        tessellator.getWorldRenderer().startDrawing(this._currentGroupObject != null ? this._currentGroupObject.GLDrawingMode : GL11.GL_TRIANGLES);
        this.renderAll(tessellator);
        tessellator.draw();
    }

    @Override
    public void renderAll(Tessellator tessellator) {

        for (GroupObject group : this._groupObjects)
            group.render(tessellator);
    }

    @Override
    public void renderOnly(String... groupNames) {

        for (GroupObject group : this._groupObjects) {

            for (String groupName : groupNames) {

                if (groupName.equalsIgnoreCase(group.Name)) {

                    group.render();
                    break;
                }
            }
        }
    }

    @Override
    public void renderOnly(Tessellator tessellator, String... groupNames) {

        for (GroupObject group : this._groupObjects) {

            for (String groupName : groupNames) {

                if (groupName.equalsIgnoreCase(group.Name)) {

                    group.render(tessellator);
                    break;
                }
            }
        }
    }

    @Override
    public void renderPart(String partName) {

        for (GroupObject groupObject : this._groupObjects) {

            if (partName.equalsIgnoreCase(groupObject.Name))
                groupObject.render();
        }
    }

    @Override
    public void renderPart(Tessellator tessellator, String partName) {

        for (GroupObject groupObject : this._groupObjects) {

            if (partName.equalsIgnoreCase(groupObject.Name))
                groupObject.render(tessellator);
        }
    }

    @Override
    public void renderAllExcept(String... excludedGroupNames) {

        for (GroupObject groupObject : this._groupObjects) {

            boolean skip = false;

            for (String excludedGroupName : excludedGroupNames) {

                if (excludedGroupName.equalsIgnoreCase(groupObject.Name)) {

                    skip = true;
                    break;
                }
            }

            if (!skip)
                groupObject.render();
        }
    }

    @Override
    public void renderAllExcept(Tessellator tessellator, String... excludedGroupNames) {

        for (GroupObject groupObject : this._groupObjects) {

            boolean skip = false;

            for (String excludedGroupName : excludedGroupNames) {

                if (excludedGroupName.equalsIgnoreCase(groupObject.Name)) {

                    skip = true;
                    break;
                }
            }

            if (!skip)
                groupObject.render(tessellator);
        }
    }

    @Override
    public ResourceLocation getTexture() {

        return this._texture;
    }


    public ObjModel(ArrayList<GroupObject> groups, GroupObject currentGroupObject, ResourceLocation texture) {

        this._groupObjects = groups;
        this._currentGroupObject = currentGroupObject;
        this._texture = texture;
    }

    private final ArrayList<GroupObject> _groupObjects;
    private final GroupObject _currentGroupObject;
    private final ResourceLocation _texture;
}
