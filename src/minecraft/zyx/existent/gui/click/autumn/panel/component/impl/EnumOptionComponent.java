package zyx.existent.gui.click.autumn.panel.component.impl;

import net.minecraft.client.gui.ScaledResolution;
import zyx.existent.gui.click.autumn.panel.Panel;
import zyx.existent.gui.click.autumn.panel.component.Component;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EnumOptionComponent extends Component {
    private final Setting option;
    private final Options options;

    public EnumOptionComponent(Setting option, Options options, Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
        this.option = option;
        this.options = options;
    }

    public void onDraw(int mouseX, int mouseY) {
        ScaledResolution sr = new ScaledResolution(mc);
        Panel parent = getPanel();
        int x = parent.getX() + getX();
        int y = parent.getY() + getY();
        boolean hovered = isMouseOver(mouseX, mouseY);
        int color = new Color(hovered ? 160 : 210, hovered ? 160 : 210, hovered ? 160 : 210).getRGB();
        FONT_RENDERER2.drawStringWithShadow(String.format("%s: %s", this.options.getName(), ((Options) option.getValue()).getSelected()), x + 2.0F, y + getHeight() / 2.0F - 2.0F, color);
    }

    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (isMouseOver(mouseX, mouseY)) {
            List<String> options = new CopyOnWriteArrayList<>();
            Collections.addAll(options, ((Options) option.getValue()).getOptions());

            for (int i = options.size() - 1; i >= 0; i--) {
                if (options.get(i).equalsIgnoreCase(((Options) option.getValue()).getSelected())) {
                    if (i - 1 < 0) {
                        ((Options) option.getValue()).setSelected(options.get(options.size() - 1));
                    } else {
                        ((Options) option.getValue()).setSelected(options.get(i - 1));
                    }
                    break;
                }
            }
        }
//            this.options.setSelected(this.options.getOptions()[(((Options) this.option.getValue()).getOptions().length + 1) % (this.options.getOptions()).length]);
    }
}
