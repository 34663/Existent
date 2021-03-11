package zyx.existent.gui.click.autumn.panel.component;

import zyx.existent.gui.click.autumn.panel.Panel;
import zyx.existent.utils.MCUtil;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.awt.*;

public class Component implements MCUtil {
    protected static final Color BACKGROUND = new Color(30, 30, 30, 240);
    protected static final CFontRenderer FONT_RENDERER = Fonts.elliot17;
    protected static final CFontRenderer FONT_RENDERER2 = Fonts.elliot15;
    protected static final CFontRenderer FONT_RENDERER_SMALL = Fonts.elliot12;
    protected static final CFontRenderer FONT_RENDERER_TINY = Fonts.default12;
    protected static final CFontRenderer FONT_ARROWICON = Fonts.ARROW_ICON;
    private final Panel panel;
    private final int x;
    private final int width;
    private int y;
    protected final int height;

    public Component(Panel panel, int x, int y, int width, int height) {
        this.panel = panel;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Panel getPanel() {
        return this.panel;
    }

    public void onDraw(int mouseX, int mouseY) {
    }

    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
    }

    public void onMouseRelease(int mouseX, int mouseY, int mouseButton) {
    }

    public void onKeyPress(int typedChar, int keyCode) {
    }

    public final boolean isMouseOver(int mouseX, int mouseY) {
        int x = this.panel.getX() + this.x;
        int y = this.panel.getY() + this.y;
        return (mouseX > x && mouseX < x + this.width && mouseY > y && mouseY < y + this.height);
    }

    public double getOffset() {
        return 0.0D;
    }

    public int getX() {
        return this.x;
    }

    public int getWidth() {
        return this.width;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return this.y;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isHidden() {
        return false;
    }
}
