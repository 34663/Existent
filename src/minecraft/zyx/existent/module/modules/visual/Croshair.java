package zyx.existent.module.modules.visual;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventCrosshair;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.render.RenderingUtils;

import java.awt.*;

public class Croshair extends Module {
    private String DOT = "DOT";
    private String LINE = "LINE";
    private String GAP = "GAP";
    private String LENGHT = "LENGHT";
    private String THICKNESS = "THICKNESS";
    private String LINETHICKNESS = "LINETHICKNESS";
    private String DYNAMIC = "DYNAMIC";

    public Croshair(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(DOT, new Setting(DOT, true, "Draw dot."));
        settings.put(LINE, new Setting(LINE, true, "Draw Line."));
        settings.put(DYNAMIC, new Setting(DYNAMIC, true, ""));
        settings.put(GAP, new Setting(GAP, 2.0, "Aura Range.", 0.5, 0.5, 4.0));
        settings.put(LENGHT, new Setting(LENGHT, 4.0, "Aura Range.", 0.5, 0.5, 10.0));
        settings.put(THICKNESS, new Setting(THICKNESS, 1.0, "Aura Range.", 0.5, 0.5, 4.0));
        settings.put(LINETHICKNESS, new Setting(LINETHICKNESS, 0.5, "Aura Range.", 0.5, 0.5, 4.0));
    }

    @EventTarget
    public void onRenderCrosshair(EventCrosshair event) {
        GL11.glPushMatrix();
        event.setCancelled(true);
        ScaledResolution sr = event.getScaledRes();
        double thickness = ((Number) settings.get(THICKNESS).getValue()).doubleValue() / 2.0D;
        double linethickness = ((Number) settings.get(LINETHICKNESS).getValue()).doubleValue();
        double gap = ((Number) settings.get(GAP).getValue()).doubleValue();
        double lenght = ((Number) settings.get(LENGHT).getValue()).doubleValue();
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        float middleX = width / 2.0F;
        float middleY = height / 2.0F;
        if ((Boolean) settings.get(DOT).getValue()) {
            RenderingUtils.drawRect(middleX - thickness - linethickness, middleY - thickness - linethickness, middleX + thickness + linethickness, middleY + thickness + linethickness, Color.BLACK.getRGB());
            RenderingUtils.drawRect(middleX - thickness, middleY - thickness, middleX + thickness, middleY + thickness, new Color(255, 50, 50).getRGB());
        }

        // 上
        RenderingUtils.drawRect(middleX - thickness - linethickness, middleY - gap - lenght - linethickness - (isMoving() ? 2 : 0), middleX + thickness + linethickness, middleY - gap + linethickness - (isMoving() ? 2 : 0), Color.BLACK.getRGB());
        RenderingUtils.drawRect(middleX - thickness, middleY - gap - lenght - (isMoving() ? 2 : 0), middleX + thickness, middleY - gap - (isMoving() ? 2 : 0), new Color(255, 50, 50).getRGB());

        // 左
        RenderingUtils.drawRect(middleX - gap - lenght - linethickness - (isMoving() ? 2 : 0), middleY - thickness - linethickness, middleX - gap + linethickness - (isMoving() ? 2 : 0), middleY + thickness + linethickness, Color.BLACK.getRGB());
        RenderingUtils.drawRect(middleX - gap - lenght - (isMoving() ? 2 : 0), middleY - thickness, middleX - gap - (isMoving() ? 2 : 0), middleY + thickness, new Color(255, 50, 50, 255).getRGB());

        // 下
        RenderingUtils.drawRect(middleX - thickness - linethickness, middleY + gap - linethickness + (isMoving() ? 2 : 0), middleX + thickness + linethickness, middleY + gap + lenght + linethickness + (isMoving() ? 2 : 0), Color.BLACK.getRGB());
        RenderingUtils.drawRect(middleX - thickness, middleY + gap + (isMoving() ? 2 : 0), middleX + thickness, middleY + gap + lenght + (isMoving() ? 2 : 0), new Color(255, 50, 50).getRGB());

        // 右
        RenderingUtils.drawRect(middleX + gap - linethickness + (isMoving() ? 2 : 0), middleY - thickness - linethickness, middleX + gap + lenght + linethickness + (isMoving() ? 2 : 0), middleY + thickness + linethickness, Color.BLACK.getRGB());
        RenderingUtils.drawRect(middleX + gap + (isMoving() ? 2 : 0), middleY - thickness, middleX + gap + lenght + (isMoving() ? 2 : 0), middleY + thickness, new Color(255, 50, 50).getRGB());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GL11.glPopMatrix();
    }

    public boolean isMoving() {
        return (Boolean) settings.get(DYNAMIC).getValue() && (!mc.thePlayer.isCollidedHorizontally) && (!mc.thePlayer.isSneaking()) && ((mc.thePlayer.movementInput.moveForward != 0.0F) || (mc.thePlayer.movementInput.moveStrafe != 0.0F));
    }
}
