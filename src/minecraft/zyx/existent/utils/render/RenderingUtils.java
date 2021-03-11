package zyx.existent.utils.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import zyx.existent.utils.MCUtil;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderingUtils implements MCUtil {
    private static final Frustum frustrum = new Frustum();
    protected static RenderManager renderManager;
    protected static float zLevel;

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static void drawRainbowRectVertical(int x, int y, int x1, int y1, int segments, float alpha) {
        if (segments < 1)
            segments = 1;
        if (segments > y1 - y)
            segments = y1 - y;
        int segmentLength = (y1 - y) / segments;
        long time = System.nanoTime();
        for (int i = 0; i < segments; i++)
            mc.ingameGUI.drawGradientRect(x, y + segmentLength * i - 1, x1, y + (segmentLength + 1) * i, Colors.rainbow(time, i * 0.1F, alpha).getRGB(), Colors.rainbow(time, (i + 0.1F) * 0.1F, alpha).getRGB());
    }

    public static void drawRainbowRectHorizontal(int x, int y, int x1, int y1, int segments, float alpha) {
        if (segments < 1)
            segments = 1;
        if (segments > x1 - x)
            segments = x1 - x;
        int segmentLength = (x1 - x) / segments;
        long time = System.nanoTime();
        for (int i = 0; i < segments; i++)
            drawRect(x + segmentLength * i, y, x + (segmentLength + 1) * i, y1, Colors.rainbow(time, i, alpha).getRGB());
    }

    public static void drawCustomString(String text, float x, float y, int color, boolean shadow, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        mc.fontRendererObj.drawString(text, x, y, color, shadow);
        GlStateManager.popMatrix();
    }

    public static void drawRectWithEdge(double x, double y, double width, double height, Color color, Color color2) {
        drawRect(x, y, x + width, y + height, color.getRGB());
        int c = color2.getRGB();
        drawRect(x - 1.0D, y, x, y + height, c);
        drawRect(x + width, y, x + width + 1.0D, y + height, c);
        drawRect(x - 1.0D, y - 1.0D, x + width + 1.0D, y, c);
        drawRect(x - 1.0D, y + height, x + width + 1.0D, y + height + 1.0D, c);
    }

    public static void drawLine3D(float x, float y, float z, float x1, float y1, float z1, int color) {
        pre3D();
        GL11.glLoadIdentity();
        mc.entityRenderer.orientCamera(mc.timer.renderPartialTicks);
        float var11 = (color >> 24 & 0xFF) / 255.0F;
        float var6 = (color >> 16 & 0xFF) / 255.0F;
        float var7 = (color >> 8 & 0xFF) / 255.0F;
        float var8 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(var6, var7, var8, var11);
        GL11.glLineWidth(0.5f);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glEnd();
        post3D();
    }

    public static void drawLine(float x, float y, float x2, float y2, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        GL11.glLineWidth(1.0F);
        GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        worldrenderer.begin(1, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x, y, 0.0D).endVertex();
        worldrenderer.pos(x2, y2, 0.0D).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void prepareScissorBox(float x, float y, float x2, float y2) {
        ScaledResolution scale = new ScaledResolution(mc);
        int factor = scale.getScaleFactor();
        GL11.glScissor((int) (x * factor), (int) ((scale.getScaledHeight() - y2) * factor), (int) ((x2 - x) * factor), (int) ((y2 - y) * factor));
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(left, bottom, 0.0D).endVertex();
        vertexbuffer.pos(right, bottom, 0.0D).endVertex();
        vertexbuffer.pos(right, top, 0.0D).endVertex();
        vertexbuffer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        drawRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        drawRect(x + width, y, x1 - width, y + width, borderColor);
        drawRect(x, y, x + width, y1, borderColor);
        drawRect(x1 - width, y, x1, y1, borderColor);
        drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
    }

    public static void drawBorderRect(double x, double y, double x1, double y1, int color, double lwidth) {
        drawHLine(x, y, x1, y, (float) lwidth, color);
        drawHLine(x1, y, x1, y1, (float) lwidth, color);
        drawHLine(x, y1, x1, y1, (float) lwidth, color);
        drawHLine(x, y1, x, y, (float) lwidth, color);
    }

    public static void drawBorderedRect(final double x, final double y, final double x2, final double y2, final double width, final int color1, final int color2) {
        drawRect(x, y, x2, y2, color2);
        drawBorderRect(x, y, x2, y2, color1, width);
//        glEnable(GL_BLEND);
//        glDisable(GL_TEXTURE_2D);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        glEnable(GL_LINE_SMOOTH);
//        glPushMatrix();
//        glColor(color1);
//        glLineWidth((float) width);
//        glBegin(1);
//        glVertex2d(x, y);
//        glVertex2d(x, y2);
//        glVertex2d(x2, y2);
//        glVertex2d(x2, y);
//        glVertex2d(x, y);
//        glVertex2d(x2, y);
//        glVertex2d(x, y2);
//        glVertex2d(x2, y2);
//        glEnd();
//        glPopMatrix();
//        glEnable(GL_TEXTURE_2D);
//        glDisable(GL_BLEND);
//        glDisable(GL_LINE_SMOOTH);
    }

    public static void drawCustomRoundedRect(float x, float y, float x2, float y2, final float round, final int color) {
        GlStateManager.disableBlend();
        x += (float) (round / 2.0f + 0.0);
        y += (float) (round / 2.0f + 0.0);
        x2 -= (float) (round / 2.0f + 0.0);
        y2 -= (float) (round / 2.0f + 0.0);
        drawCircle(x2 - round / 2.0f, y + round / 2.0f, round, 0, 90, color);
        drawCircle(x + round / 2.0f, y + round / 2.0f, round, 90, 180, color);
        drawCircle(x + round / 2.0f, y2 - round / 2.0f, round, 180, 270, color);
        drawCircle(x2 - round / 2.0f, y2 - round / 2.0f, round, 270, 360, color);
        drawRect(x - round / 2.0f, y + round / 2.0f, x2, y2 - round / 2.0f, color);
        drawRect(x2 + round / 2.0f - round / 2.0f, y + round / 2.0f, x2 + round / 2.0f, y2 - round / 2.0f, color);
        drawRect(x + round / 2.0f, y - round / 2.0f, x2 - round / 2.0f, y + round / 2.0f, color);
        drawRect(x + round / 2.0f, y2 - round / 2.0f + 0.0f, x2 - round / 2.0f, y2 + round / 2.0f + 0.0f, color);
        GlStateManager.disableBlend();
    }

    public static void drawRoundedRect(double x, double y, double x1, double y1, int borderC, int insideC) {
        drawRect(x + 0.5F, y, x1 - 0.5F, y + 0.5F, insideC);
        drawRect(x + 0.5F, y1 - 0.5F, x1 - 0.5F, y1, insideC);
        drawRect(x, y + 0.5F, x1, y1 - 0.5F, insideC);
    }

    public static void drawRoundedRect(int xCoord, int yCoord, int xSize, int ySize, int colour) {
        int width = xCoord + xSize;
        int height = yCoord + ySize;
        drawRect(xCoord + 1, yCoord, width - 1, height, colour);
        drawRect(xCoord, yCoord + 1, width, height - 1, colour);
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawGradient(double x, double y, double x2, double y2, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;

        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;

        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);

        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);

        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;

        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);

        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);

        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
        GL11.glColor4d(255, 255, 255, 255);
    }

    public static void drawCircle(float cx, float cy, float r, int num_segments, int c) {
        GL11.glPushMatrix();
        cx *= 2.0F;
        cy *= 2.0F;
        float f = (c >> 24 & 0xFF) / 255.0F;
        float f1 = (c >> 16 & 0xFF) / 255.0F;
        float f2 = (c >> 8 & 0xFF) / 255.0F;
        float f3 = (c & 0xFF) / 255.0F;
        float theta = (float) (6.2831852D / num_segments);
        float p = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        float x = r *= 2.0F;
        float y = 0.0F;
        enableGL2D();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(2);
        int ii = 0;
        while (ii < num_segments) {
            GL11.glVertex2f(x + cx, y + cy);
            float t = x;
            x = p * x - s * y;
            y = s * t + p * y;
            ii++;
        }
        GL11.glEnd();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        disableGL2D();
        GlStateManager.color(1, 1, 1, 1);
        GL11.glPopMatrix();
    }
    public static void drawCircle(final double x, final double y, final float radius, final int startPi, final int endPi, final int c) {
        final float f = (c >> 24 & 0xFF) / 255.0f;
        final float f2 = (c >> 16 & 0xFF) / 255.0f;
        final float f3 = (c >> 8 & 0xFF) / 255.0f;
        final float f4 = (c & 0xFF) / 255.0f;
        GL11.glColor4f(f2, f3, f4, f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.alphaFunc(516, 0.001f);
        final Tessellator tess = Tessellator.getInstance();
        final BufferBuilder render = tess.getBuffer();
        for (double i = startPi; i < endPi; ++i) {
            final double cs = i * 3.141592653589793 / 180.0;
            final double ps = (i - 1.0) * 3.141592653589793 / 180.0;
            final double[] outer = {Math.cos(cs) * radius, -Math.sin(cs) * radius, Math.cos(ps) * radius, -Math.sin(ps) * radius};
            render.begin(6, DefaultVertexFormats.POSITION);
            render.pos(x + outer[2], y + outer[3], 0.0).endVertex();
            render.pos(x + outer[0], y + outer[1], 0.0).endVertex();
            render.pos(x, y, 0.0).endVertex();
            tess.draw();
        }
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.disableAlpha();
        GL11.glEnable(3553);
    }

    public static void drawHLine(double x, double y, double x1, double y1, float width, int color) {
        float var11 = (color >> 24 & 0xFF) / 255.0F;
        float var6 = (color >> 16 & 0xFF) / 255.0F;
        float var7 = (color >> 8 & 0xFF) / 255.0F;
        float var8 = (color & 0xFF) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        glPushMatrix();
        glLineWidth(width);
        glBegin(GL_LINE_STRIP);
        glVertex2d(x, y);
        glVertex2d(x1, y1);
        glEnd();
        glLineWidth(1);
        glPopMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void drawFilledTriangle(float x, float y, float r, int c, int borderC) {
        enableGL2D();
        glColor(c);
        glEnable(GL_POLYGON_SMOOTH);
        glBegin(GL_TRIANGLES);
        glVertex2f(x + r / 2, y + r / 2);
        glVertex2f(x + r / 2, y - r / 2);
        glVertex2f(x - r / 2, y);
        glEnd();
        glLineWidth(1.3f);
        glColor(borderC);
        glBegin(GL_LINE_STRIP);
        glVertex2f(x + r / 2, y + r / 2);
        glVertex2f(x + r / 2, y - r / 2);
        glEnd();
        glBegin(GL_LINE_STRIP);
        glVertex2f(x - r / 2, y);
        glVertex2f(x + r / 2, y - r / 2);
        glEnd();
        glBegin(GL_LINE_STRIP);
        glVertex2f(x + r / 2, y + r / 2);
        glVertex2f(x - r / 2, y);
        glEnd();
        glDisable(GL_POLYGON_SMOOTH);
        disableGL2D();
    }

    public static void drawCornerRect(double left, double top, double right, double bottom, int color, int otherColor, int corner) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha2 = (otherColor >> 24 & 0xFF) / 255.0F;
        float red2 = (otherColor >> 16 & 0xFF) / 255.0F;
        float green2 = (otherColor >> 8 & 0xFF) / 255.0F;
        float blue2 = (otherColor & 0xFF) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        if (corner == 0) {
            worldrenderer.pos(right, top, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(left, top, zLevel).color(red2, green2, blue2, alpha2).endVertex();
            worldrenderer.pos(left, bottom, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(right, bottom, zLevel).color(red, green, blue, alpha).endVertex();
        } else if (corner == 1) {
            worldrenderer.pos(right, top, zLevel).color(red2, green2, blue2, alpha2).endVertex();
            worldrenderer.pos(left, top, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(left, bottom, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(right, bottom, zLevel).color(red, green, blue, alpha).endVertex();
        } else if (corner == 2) {
            worldrenderer.pos(right, top, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(left, top, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(left, bottom, zLevel).color(red2, green2, blue2, alpha2).endVertex();
            worldrenderer.pos(right, bottom, zLevel).color(red, green, blue, alpha).endVertex();
        } else if (corner == 3) {
            worldrenderer.pos(right, top, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(left, top, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(left, bottom, zLevel).color(red, green, blue, alpha).endVertex();
            worldrenderer.pos(right, bottom, zLevel).color(red2, green2, blue2, alpha2).endVertex();
        } else {
            throw new IndexOutOfBoundsException("corner value must be between 0 and 3");
        }
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawImage(final ResourceLocation image, final int x, final int y, final int width, final int height) {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawFilledBox(final AxisAlignedBB axisAlignedBB) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder vertexbufferer = tessellator.getBuffer();
        vertexbufferer.begin(7, DefaultVertexFormats.POSITION);
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
        vertexbufferer.begin(7, DefaultVertexFormats.POSITION);
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
        vertexbufferer.begin(7, DefaultVertexFormats.POSITION);
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        vertexbufferer.begin(7, DefaultVertexFormats.POSITION);
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        vertexbufferer.begin(7, DefaultVertexFormats.POSITION);
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        vertexbufferer.begin(7, DefaultVertexFormats.POSITION);
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        vertexbufferer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawBoundingBox(final AxisAlignedBB axisalignedbb) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder worldrender = Tessellator.getInstance().getBuffer();
        worldrender.begin(7, DefaultVertexFormats.POSITION);
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).endVertex();
        worldrender.pos(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawImg(ResourceLocation loc, double posX, double posY, double width, double height) {
        mc.getTextureManager().bindTexture(loc);
        float f = 1.0F / (float) width;
        float f1 = 1.0F / (float) height;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(posX, posY + height, 0.0D).tex((0.0F * f), ((0.0F + (float) height) * f1)).endVertex();
        worldrenderer.pos(posX + width, posY + height, 0.0D).tex(((0.0F + (float) width) * f), ((0.0F + (float) height) * f1)).endVertex();
        worldrenderer.pos(posX + width, posY, 0.0D).tex(((0.0F + (float) width) * f), (0.0F * f1)).endVertex();
        worldrenderer.pos(posX, posY, 0.0D).tex((0.0F * f), (0.0F * f1)).endVertex();
        tessellator.draw();
    }

    public static void glColor(final int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255F;
        float red = (hex >> 16 & 0xFF) / 255F;
        float green = (hex >> 8 & 0xFF) / 255F;
        float blue = (hex & 0xFF) / 255F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColor(final int hex, final float alpha) {
        float red = (hex >> 16 & 0xFF) / 255F;
        float green = (hex >> 8 & 0xFF) / 255F;
        float blue = (hex & 0xFF) / 255F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        GL11.glDepthMask(false);
        GL11.glHint(3154, 4354);
    }

    public static void post3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return (isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck);
    }

    private static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static double getDiff(double lastI, double i, float ticks, double ownI) { return lastI + (i - lastI) * ticks - ownI; }
}
