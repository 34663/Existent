package zyx.existent.gui.notification;

import java.awt.*;

public enum  NotificationType {
    SUCCESS(new Color(100, 255, 100).getRGB(), "a"),
    INFO(new Color(100, 100, 255).getRGB(), "b"),
    ERROR(new Color(255, 100, 100).getRGB(), "c"),
    WARNING(new Color(255, 211, 53).getRGB(), "c");

    private final int color;
    private final String colorstr;

    NotificationType(int color, String str) {
        this.color = color;
        this.colorstr = str;
    }

    public final int getColor() {
        return this.color;
    }

    public final String getColorstr() {
        return colorstr;
    }
}
