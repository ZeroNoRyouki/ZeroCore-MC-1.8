package zero.mods.zerocore.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderProjectile extends Render {

    public RenderProjectile() {

        this(null);
    }

    public RenderProjectile(ResourceLocation textureLocation) {

        super(Minecraft.getMinecraft().getRenderManager());
        this._textureLocation = textureLocation;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float par8, float par9) {

        if (entity instanceof IProjectile) {

            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer wr = tessellator.getWorldRenderer();

            GL11.glPushMatrix();
            GL11.glTranslatef((float)x, (float)y, (float)z);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glScalef(0.1F, 0.1F, 0.1F);
            this.bindTexture(this.getEntityTexture(entity));
            GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

            wr.startDrawingQuads();
            wr.setNormal(0.0F, 1.0F, 0.0F);
            wr.addVertexWithUV(-0.5F, -0.25F, 0.0D, 0, 1);
            wr.addVertexWithUV(0.5F, -0.25F, 0.0D, 1, 1);
            wr.addVertexWithUV(0.5F, 0.75F, 0.0D, 1, 0);
            wr.addVertexWithUV(-0.5F, 0.75F, 0.0D, 0, 0);

            tessellator.draw();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {

        return this._textureLocation;
    }

    protected final ResourceLocation _textureLocation;

}
