package zyx.existent.module.modules.movement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventMove;
import zyx.existent.event.events.EventRender3D;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.module.modules.combat.KillAura;
import zyx.existent.utils.MoveUtils;
import zyx.existent.utils.RotationUtils;
import zyx.existent.utils.entity.EntityValidator;
import zyx.existent.utils.entity.impl.VoidCheck;
import zyx.existent.utils.entity.impl.WallCheck;
import zyx.existent.utils.render.GLUtils;

import java.awt.*;

public class TargetStrafe extends Module {
    public int direction = -1;

    public static String RADIUS = "RADIUS";
    private final String RENDER = "RENDER";

    public TargetStrafe(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(RENDER, new Setting(RENDER, true, "Render Circle."));
        settings.put(RADIUS, new Setting(RADIUS, 1.8, "Strafe Range.", 0.1, 1.0, 7.0));
        EntityValidator targetValidator = new EntityValidator();
        targetValidator.add(new VoidCheck());
        targetValidator.add(new WallCheck());
    }

    @EventTarget
    public final void onUpdate(EventUpdate event) {
        if (event.isPre()) {
            if (mc.thePlayer.isCollidedHorizontally)
                switchDirection();
            if (mc.gameSettings.keyBindLeft.isPressed())
                this.direction = 1;
            if (mc.gameSettings.keyBindRight.isPressed())
                this.direction = -1;
        }
    }
    @EventTarget
    public void onRender3D(EventRender3D render) {
        if (canStrafe() && (Boolean) settings.get(RENDER).getValue())
            drawCircle(KillAura.target, render.getRenderPartialTicks(), ((Number) settings.get(RADIUS).getValue()).doubleValue());
    }

    public void strafe(EventMove event, double moveSpeed) {
        EntityLivingBase target = KillAura.target;
        float[] rotations = RotationUtils.getRotationsEntity(target);
        if (mc.thePlayer.getDistanceToEntity(target) < ((Number) settings.get(RADIUS).getValue()).doubleValue()) {
            MoveUtils.setSpeed(event, moveSpeed, rotations[0], this.direction, 0.0D);
        } else {
            MoveUtils.setSpeed(event, moveSpeed, rotations[0], this.direction, 1.0D);
        }
    }
    private void switchDirection() {
        if (this.direction == 1) {
            this.direction = -1;
        } else {
            this.direction = 1;
        }
    }
    private void drawCircle(Entity entity, float partialTicks, double rad) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GLUtils.startSmooth();
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(1.0F);
        GL11.glBegin(3);
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;
        float r = 0.003921569F * Color.WHITE.getRed();
        float g = 0.003921569F * Color.WHITE.getGreen();
        float b = 0.003921569F * Color.WHITE.getBlue();
        double pix2 = 6.283185307179586D;
        int[] counter = {1};
        for (int i = 0; i <= 100; i++) {
            GL11.glColor4f(255, 255, 255, 255);
            GL11.glVertex3d(x + rad * Math.cos(i * 6.283185307179586D / 45.0D), y, z + rad * Math.sin(i * 6.283185307179586D / 45.0D));
            counter[0] -= 1;
        }
        GL11.glEnd();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GLUtils.endSmooth();
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }
    public boolean canStrafe() {
        return (Existent.getModuleManager().isEnabled(KillAura.class) && KillAura.target != null && isEnabled());
    }
}
