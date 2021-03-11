package zyx.existent.gui.click.autumn.panel.component.impl;

import zyx.existent.gui.click.autumn.panel.Panel;
import zyx.existent.gui.click.autumn.panel.component.Component;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberOptionComponent extends Component {
    private final Setting option;
    private boolean dragging = false;
    private int opacity = 120;

    public NumberOptionComponent(Setting option, Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
        this.option = option;
    }

    public void onDraw(int mouseX, int mouseY) {
        Panel parent = getPanel();
        int x = parent.getX() + getX();
        int y = parent.getY() + getY();
        boolean hovered = isMouseOver(mouseX, mouseY);
        int height = getHeight();
        int width = getWidth();
        Setting option = this.option;
        double min = ((Number) option.getMin()).doubleValue();
        double max = ((Number) option.getMax()).doubleValue();
        double inc = ((Number) option.getInc()).doubleValue();
        if (this.dragging) {
            option.setValue(Double.valueOf(round((mouseX - x) * (max - min) / width + min, inc)));
            if (((Double) option.getValue()).doubleValue() > max) {
                option.setValue(Double.valueOf(max));
            } else if (((Double) option.getValue()).doubleValue() < min) {
                option.setValue(Double.valueOf(min));
            }
        }
        double optionValue = round(((Number) option.getValue()).doubleValue(), inc);
        String optionValueStr = String.valueOf(optionValue);
        int color = Color.WHITE.getRGB();
        double renderPerc = (width - 3) / (max - min);
        double barWidth = renderPerc * optionValue - renderPerc * min;
        if (hovered) {
            if (this.opacity < 200)
                this.opacity += 5;
        } else if (this.opacity > 120) {
            this.opacity -= 5;
        }
        RenderingUtils.drawRect((x + 3), (y + height - 4), (x + width - 3), (y + height - 3), -1436524448);
        RenderingUtils.drawRect((x + 3), (y + height - 4), x + Math.max(barWidth, 4.0D), (y + height - 3), color);
        FONT_RENDERER_SMALL.drawStringWithShadow(option.getName(), x + 2.0F, y + height / 2.0F - 4.0F, color);
        FONT_RENDERER_SMALL.drawStringWithShadow(optionValueStr, (x + width - FONT_RENDERER_SMALL.getStringWidth(optionValueStr) - 3), y + height / 2.0F - 4.0F, color);
    }

    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (isMouseOver(mouseX, mouseY))
            this.dragging = true;
    }

    public void onMouseRelease(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
    }

//    public boolean isHidden() {
//        return !this.option.check();
//    }

    private double round(double num, double increment) {
        double v = Math.round(num / increment) * increment;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
