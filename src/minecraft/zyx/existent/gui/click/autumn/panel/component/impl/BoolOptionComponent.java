package zyx.existent.gui.click.autumn.panel.component.impl;

import zyx.existent.gui.click.autumn.panel.Panel;
import zyx.existent.gui.click.autumn.panel.component.Component;
import zyx.existent.module.data.Setting;

import java.awt.*;

public class BoolOptionComponent extends Component {
    private final Setting option;

    public BoolOptionComponent(Setting option, Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
        this.option = option;
    }

    public void onDraw(int mouseX, int mouseY) {
        Panel parent = getPanel();
        int x = parent.getX() + getX();
        int y = parent.getY() + getY();
        boolean hovered = isMouseOver(mouseX, mouseY);
        int color = (Boolean) this.option.getValue() ? new Color(hovered ? 220 : 255, hovered ? 30 : 50, hovered ? 30 : 50).getRGB() : new Color(hovered ? 130 : 170, hovered ? 130 : 170, hovered ? 130 : 170).getRGB();
        FONT_RENDERER2.drawStringWithShadow(this.option.getName().substring(0, 1).toUpperCase()+this.option.getName().substring(1).toLowerCase(), x + 2.0F, y + getHeight() / 2.0F - 2.0F, color);
    }

    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (isMouseOver(mouseX, mouseY)) {
            boolean xd = ((Boolean) option.getValue());
            this.option.setValue(!xd);
        }
    }
}
