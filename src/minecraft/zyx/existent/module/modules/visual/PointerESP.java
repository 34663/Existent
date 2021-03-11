package zyx.existent.module.modules.visual;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventRender2D;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.render.RenderingUtils;

import java.awt.*;

public class PointerESP extends Module {
    private final String PLAYERS = "PLAYERS";
    private final String MONSTERS = "MONSTERS";
    private final String ANIMALS = "ANIMALS";
    private final String INVISIBLE = "INVISIBLE";

    public PointerESP(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(PLAYERS, new Setting(PLAYERS, true, ""));
        settings.put(MONSTERS, new Setting(MONSTERS, false, ""));
        settings.put(ANIMALS, new Setting(ANIMALS, false, ""));
        settings.put(INVISIBLE, new Setting(INVISIBLE, false, ""));
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        ScaledResolution sr = new ScaledResolution(mc);
        GlStateManager.pushMatrix();
        int size = 100;
        double xOffset = sr.getScaledWidth() / 2F - (size / 2.04);
        double yOffset = sr.getScaledHeight() / 2F - (size / 1.983);
        double playerOffsetX = mc.thePlayer.posX;
        double playerOffSetZ = mc.thePlayer.posZ;

        for (int i = 0; i < mc.theWorld.loadedEntityList.size(); i++) {
            Entity entity = mc.theWorld.loadedEntityList.get(i);

            if (!mc.thePlayer.canEntityBeSeen(entity)) {
                if ((Boolean) settings.get(MONSTERS).getValue() && (entity instanceof EntityMob || entity instanceof EntitySlime) && ((Boolean) settings.get(INVISIBLE).getValue() || !entity.isInvisible())) {
                    double loaddist = 0.2f;
                    double pTicks = mc.timer.renderPartialTicks;
                    double pos1 = (((entity.posX + (entity.posX - entity.lastTickPosX) * pTicks) - playerOffsetX) * loaddist);
                    double pos2 = (((entity.posZ + (entity.posZ - entity.lastTickPosZ) * pTicks) - playerOffSetZ) * loaddist);
                    double cos = Math.cos(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
                    double sin = Math.sin(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
                    double rotY = -(pos2 * cos - pos1 * sin);
                    double rotX = -(pos1 * cos + pos2 * sin);
                    double var7 = 0 - rotX;
                    double var9 = 0 - rotY;
                    if (MathHelper.sqrt(var7 * var7 + var9 * var9) < size / 2F - 4) {
                        double angle = (Math.atan2(rotY - 0, rotX - 0) * 180 / Math.PI);
                        double x = ((size / 2F) * Math.cos(Math.toRadians(angle))) + xOffset + size / 2F;
                        double y = ((size / 2F) * Math.sin(Math.toRadians(angle))) + yOffset + size / 2F;
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(x, y, 0);
                        GlStateManager.rotate((float) angle, 0, 0, 1);
                        GlStateManager.scale(1.5, 1.0, 1.0);

                        drawESPCircle(0, 0, 2.2F, 3, new Color(194, 88, 234, 255).getRGB());
                        drawESPCircle(0, 0, 1.5F, 3, new Color(194, 88, 234, 255).getRGB());
                        drawESPCircle(0, 0, 1.0F, 3, new Color(194, 88, 234, 255).getRGB());
                        drawESPCircle(0, 0, 0.5F, 3, new Color(194, 88, 234, 255).getRGB());

                        GlStateManager.popMatrix();
                    }
                }

                if ((Boolean) settings.get(ANIMALS).getValue() && (entity instanceof EntityAnimal || entity instanceof EntitySquid) && ((Boolean) settings.get(INVISIBLE).getValue() || !entity.isInvisible())) {
                    double loaddist = 0.2;
                    double pTicks = mc.timer.renderPartialTicks;
                    double pos1 = (((entity.posX + (entity.posX - entity.lastTickPosX) * pTicks) - playerOffsetX) * loaddist);
                    double pos2 = (((entity.posZ + (entity.posZ - entity.lastTickPosZ) * pTicks) - playerOffSetZ) * loaddist);
                    double cos = Math.cos(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
                    double sin = Math.sin(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
                    double rotY = -(pos2 * cos - pos1 * sin);
                    double rotX = -(pos1 * cos + pos2 * sin);
                    double var7 = 0 - rotX;
                    double var9 = 0 - rotY;
                    if (MathHelper.sqrt(var7 * var7 + var9 * var9) < size / 2F - 4) {
                        double angle = (Math.atan2(rotY - 0, rotX - 0) * 180 / Math.PI);
                        double x = ((size / 2F) * Math.cos(Math.toRadians(angle))) + xOffset + size / 2F;
                        double y = ((size / 2F) * Math.sin(Math.toRadians(angle))) + yOffset + size / 2F;
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(x, y, 0);
                        GlStateManager.rotate((float) angle, 0, 0, 1);
                        GlStateManager.scale(1.5, 1.0, 1.0);

                        drawESPCircle(0, 0, 2.2F, 3, new Color(194, 88, 234, 255).getRGB());
                        drawESPCircle(0, 0, 1.5F, 3, new Color(194, 88, 234, 255).getRGB());
                        drawESPCircle(0, 0, 1.0F, 3, new Color(194, 88, 234, 255).getRGB());
                        drawESPCircle(0, 0, 0.5F, 3, new Color(194, 88, 234, 255).getRGB());

                        GlStateManager.popMatrix();
                    }
                }

                if ((Boolean) settings.get(PLAYERS).getValue() && (entity instanceof EntityPlayer && entity != mc.thePlayer) && ((Boolean) settings.get(INVISIBLE).getValue() || !entity.isInvisible())) {
                    double loaddist = 0.2;
                    double pTicks = mc.timer.renderPartialTicks;
                    double pos1 = (((entity.posX + (entity.posX - entity.lastTickPosX) * pTicks) - playerOffsetX) * loaddist);
                    double pos2 = (((entity.posZ + (entity.posZ - entity.lastTickPosZ) * pTicks) - playerOffSetZ) * loaddist);
                    double cos = Math.cos(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
                    double sin = Math.sin(mc.thePlayer.rotationYaw * (Math.PI * 2 / 360));
                    double rotY = -(pos2 * cos - pos1 * sin);
                    double rotX = -(pos1 * cos + pos2 * sin);
                    double var7 = 0 - rotX;
                    double var9 = 0 - rotY;
                    if (MathHelper.sqrt(var7 * var7 + var9 * var9) < size / 2F - 4) {
                        double angle = (Math.atan2(rotY - 0, rotX - 0) * 180 / Math.PI);
                        double x = ((size / 2F) * Math.cos(Math.toRadians(angle))) + xOffset + size / 2F;
                        double y = ((size / 2F) * Math.sin(Math.toRadians(angle))) + yOffset + size / 2F;
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(x, y, 0);
                        GlStateManager.rotate((float) angle, 0, 0, 1);
                        GlStateManager.scale(1.5, 1.0, 1.0);

                        drawESPCircle(0, 0, 2.2F, 3, new Color(194, 88, 234, 255).getRGB());
                        drawESPCircle(0, 0, 1.5F, 3, new Color(194, 88, 234, 255).getRGB());
                        drawESPCircle(0, 0, 1.0F, 3, new Color(194, 88, 234, 255).getRGB());
                        drawESPCircle(0, 0, 0.5F, 3, new Color(194, 88, 234, 255).getRGB());

                        GlStateManager.popMatrix();
                    }
                }
            }
        }
        GlStateManager.popMatrix();
    }

    private void drawESPCircle(float cx, float cy, float r, float n, int color) {
        cx *= 2.0;
        cy *= 2.0;
        float b = 6.2831852f / n;
        float p = (float) Math.cos(b);
        float s = (float) Math.sin(b);
        float x = r *= 2.0f;
        float y = 0.0f;
        GL11.glPushMatrix();
        RenderingUtils.enableGL2D();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        RenderingUtils.glColor(color);
        GL11.glBegin(2);
        int ii = 0;
        while (ii < n) {
            GL11.glVertex2f(x + cx, y + cy);
            float t = x;
            x = p * x - s * y;
            y = s * t + p * y;
            ii++;
        }
        GL11.glEnd();
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        RenderingUtils.disableGL2D();
        GL11.glPopMatrix();
    }
}
