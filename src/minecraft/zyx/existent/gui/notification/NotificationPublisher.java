package zyx.existent.gui.notification;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.MCUtil;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.animate.AnimationUtil;
import zyx.existent.utils.render.animate.Translate;
import zyx.existent.utils.render.animate.Translate2;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class NotificationPublisher implements MCUtil {
    private static final List<Notification> NOTIFICATIONS = new CopyOnWriteArrayList<>();

    public static void publish() {
        if (NOTIFICATIONS.isEmpty())
            return;
        ScaledResolution sr = new ScaledResolution(mc);
        int srScaledHeight = sr.getScaledHeight();
        int scaledWidth = sr.getScaledWidth();
        int y = srScaledHeight - 30;
        CFontRenderer title = Fonts.Tahoma20;
        CFontRenderer content = Fonts.Tahoma18;
        CFontRenderer icon = Fonts.NOTIFICATION;
        for (Notification notification : NOTIFICATIONS) {
            Translate2 translate = notification.getTranslate();
            int width = notification.getWidth();
            if (!notification.getTimer().elapsed(notification.getTime())) {
                notification.scissorBoxWidth = AnimationUtil.animate(width, notification.scissorBoxWidth, 0.25D);
                translate.interpolate((scaledWidth - width), y, 0.15D);
            } else {
                notification.scissorBoxWidth = AnimationUtil.animate(0.0, notification.scissorBoxWidth, 0.25D);
                if (notification.getWidth() > scaledWidth) {
                    System.out.println("Remove");
                    NOTIFICATIONS.remove(notification);
                }
                y += 35;
            }
            float translateX = (float) translate.getX();
            float translateY = (float) translate.getY();
            GL11.glPushMatrix();
            GL11.glEnable(3089);
            RenderingUtils.prepareScissorBox((float) (scaledWidth - notification.scissorBoxWidth), translateY, scaledWidth, translateY + 30.0F);
            RenderingUtils.drawRect(translateX, translateY, scaledWidth, (translateY + 28.0F), new Color(10, 10, 10, 180).getRGB());
            RenderingUtils.drawRect(translateX, (translateY + 28.0F - 2.0F), scaledWidth, (translateY + 28.0F), new Color(10, 10, 10, 180).getRGB());
            RenderingUtils.drawRect(translateX, (translateY + 28.0F - 2.0F), translateX + width * (notification.getTime() - notification.getTimer().getElapsedTime()) / notification.getTime(), (translateY + 28.0F), notification.getType().getColor());

            icon.drawString(notification.getType().getColorstr(), translateX + 2.0F, translateY + 4.0F, notification.getType().getColor());
            title.drawStringWithShadow(notification.getTitle(), translateX + 28.0F, translateY + 4.0F, -1);
            content.drawStringWithShadow(notification.getContent(), translateX + 28.0F, translateY + 16.0F, -1);
            GL11.glDisable(3089);
            GL11.glPopMatrix();
            y -= 35;
        }
    }

    public static void queue(String title, String content, NotificationType type) {
        CFontRenderer fr = Fonts.Tahoma16;
        NOTIFICATIONS.add(new Notification(title, content, type, fr));
    }
}
