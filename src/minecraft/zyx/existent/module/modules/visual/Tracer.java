package zyx.existent.module.modules.visual;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventRender3D;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.modules.combat.AntiBot;
import zyx.existent.utils.render.RenderingUtils;

import java.awt.*;

public class Tracer extends Module {
    public Tracer(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        boolean bobbing;
        for (Entity entities : mc.theWorld.loadedEntityList) {
            if (entities != mc.thePlayer && entities != null) {
                if (entities instanceof EntityPlayer) {
                    if (Existent.getFriendManager().isFriend(entities.getName())) {
                        float posX = (float) ((float) (entities.lastTickPosX + (entities.posX - entities.lastTickPosX) * event.renderPartialTicks) - RenderManager.renderPosX);
                        float posY = (float) ((float) (entities.lastTickPosY + (entities.posY - entities.lastTickPosY) * event.renderPartialTicks) - RenderManager.renderPosY);
                        float posZ = (float) ((float) (entities.lastTickPosZ + (entities.posZ - entities.lastTickPosZ) * event.renderPartialTicks) - RenderManager.renderPosZ);

                        bobbing = mc.gameSettings.viewBobbing;
                        mc.gameSettings.viewBobbing = false;
                        RenderingUtils.glColor(new Color(50, 120, 50, 255).getRGB());
                        if (AntiBot.getInvalid().contains(entities)) {
                            RenderingUtils.glColor(new Color(100, 100, 100, 255).getRGB());
                        }
                        draw3DLine(posX, posY, posZ, 2.5F);
                        RenderingUtils.glColor(new Color(100, 255, 100, 255).getRGB());
                        if (AntiBot.getInvalid().contains(entities)) {
                            RenderingUtils.glColor(new Color(200, 100, 100, 255).getRGB());
                        }
                        draw3DLine(posX, posY, posZ, 1.0F);
                        mc.gameSettings.viewBobbing = bobbing;
                    }
                }
            }
        }
    }

    public void draw3DLine(float x, float y, float z, float width) {
        RenderingUtils.pre3D();
        GL11.glLoadIdentity();
        mc.entityRenderer.orientCamera(mc.timer.renderPartialTicks);
        GL11.glLineWidth(width);
        GL11.glBegin(3);
        Vec3d eyes = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(mc.thePlayer.rotationPitch)).rotateYaw(-(float) Math.toRadians(mc.thePlayer.rotationYaw));
        GL11.glVertex3d(eyes.getX(), mc.thePlayer.getEyeHeight()+ eyes.getY(), eyes.getZ());
        GL11.glVertex3d((double)x, (double)y, (double)z);
        GL11.glEnd();
        RenderingUtils.post3D();
    }
}
