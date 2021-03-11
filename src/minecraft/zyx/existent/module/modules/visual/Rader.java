package zyx.existent.module.modules.visual;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventRender2D;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.timer.Timer;

import java.awt.*;

public class Rader extends Module {
    private final String SIZE = "SIZE";
    private final Timer timer = new Timer();
    private boolean dragging;
    public int scale;
    float hue;

    public Rader(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(SIZE, new Setting(SIZE, 80.0, "Rader Size", 0.1, 30.0, 100.0));
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        final ScaledResolution sr = new ScaledResolution(mc);
        this.scale = 2;
        final int size = ((Number) settings.get(SIZE).getValue()).intValue();
        final float xOffset = sr.getScaledWidth() - size - 160;
        final float yOffset = 2;
        final float playerOffsetX = (float) mc.thePlayer.posX;
        final float playerOffsetZ = (float) mc.thePlayer.posZ;
        RenderingUtils.rectangleBordered(xOffset, yOffset, xOffset + size, yOffset + size, 0.5, Colors.getColor(90), Colors.getColor(0));
        RenderingUtils.rectangleBordered(xOffset + 1.0f, yOffset + 1.0f, xOffset + size - 1.0f, yOffset + size - 1.0f, 1.0, Colors.getColor(90), Colors.getColor(61));
        RenderingUtils.rectangleBordered((double) xOffset + 2.5, (double) yOffset + 2.5, (double) (xOffset + size) - 2.5, (double) (yOffset + size) - 2.5, 0.5, Colors.getColor(61), Colors.getColor(0));
        RenderingUtils.rectangleBordered(xOffset + 3.0f, yOffset + 3.0f, xOffset + size - 3.0f, yOffset + size - 3.0f, 0.5, Colors.getColor(27), Colors.getColor(61));
        RenderingUtils.drawRect((double) xOffset + (size / 2F - 0.5), (double) yOffset + 3.5, (double) xOffset + (size / 2F + 0.5), (double) (yOffset + size) - 3.5, Colors.getColor(255, 80));
        RenderingUtils.drawRect((double) xOffset + 3.5, (double) yOffset + (size / 2F - 0.5), (double) (xOffset + size) - 3.5, (double) yOffset + (size / 2F + 0.5), Colors.getColor(255, 80));
        for (final Object obj : mc.theWorld.getLoadedEntityList()) {
            if (obj instanceof EntityPlayer) {
                final EntityPlayer ent = (EntityPlayer) obj;
                if (ent == mc.thePlayer || ent.isInvisible()) {
                    continue;
                }
                final float pTicks = this.mc.timer.renderPartialTicks;
                final float posX = (float) ((ent.posX + (ent.posX - ent.lastTickPosX) * pTicks - playerOffsetX) * this.scale);
                final float posZ = (float) ((ent.posZ + (ent.posZ - ent.lastTickPosZ) * pTicks - playerOffsetZ) * this.scale);
                int color;
                if (!MiscUtils.isTeams(ent)) {
                    color = new Color(0, 255, 0).getRGB();
                } else {
                    color = (mc.thePlayer.canEntityBeSeen(ent) ? new Color(255, 0, 0).getRGB() : new Color(255, 255, 0).getRGB());
                }
                final float cos = (float) Math.cos((double) mc.thePlayer.rotationYaw * 0.017453292519943295);
                final float sin = (float) Math.sin((double) mc.thePlayer.rotationYaw * 0.017453292519943295);
                float rotY = -(posZ * cos - posX * sin);
                float rotX = -(posX * cos + posZ * sin);
                if (rotY > size / 2F - 5) {
                    rotY = size / 2F - 5.0f;
                } else if (rotY < -(size / 2F - 5)) {
                    rotY = -(size / 2F - 5);
                }
                if (rotX > size / 2F - 5.0f) {
                    rotX = size / 2F - 5;
                } else if (rotX < -(size / 2F - 5)) {
                    rotX = -(size / 2F - 5.0f);
                }
                RenderingUtils.rectangleBordered((double) (xOffset + size / 2 + rotX) - 1.5, (double) (yOffset + size / 2 + rotY) - 1.5, (double) (xOffset + size / 2 + rotX) + 1.5, (double) (yOffset + size / 2 + rotY) + 1.5, 0.5, color, Colors.getColor(46));
            }
        }
    }
}
