package zyx.existent.gui.click.autumn.panel.component.impl;

import zyx.existent.gui.click.autumn.panel.AnimationState;
import zyx.existent.gui.click.autumn.panel.Panel;
import zyx.existent.gui.click.autumn.panel.component.Component;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.animate.AnimationUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ModuleComponent extends Component {
    private static final Color BACKGROUND_COLOR = new Color(23, 23, 23);
    public final List<Component> components = new ArrayList<>();
    private final List<Component> children = new ArrayList<>();
    private final Module module;
    private int childrenHeight;
    private double scissorBoxHeight;
    private AnimationState state = AnimationState.STATIC;

    public ModuleComponent(Module module, Panel parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.module = module;
        int y2 = height;
        if (getSettings(module) != null) {
            for (Setting setting : getSettings(module)) {
                if (setting.getValue() instanceof Options) {
                    this.children.add(new EnumOptionComponent(setting, (Options) setting.getValue(), getPanel(), x, y + y2, width, height));
                } else if (setting.getValue() instanceof Boolean) {
                    this.children.add(new BoolOptionComponent(setting, getPanel(), x, y + y2, width, height));
                } else if (setting.getValue() instanceof Number) {
                    this.children.add(new NumberOptionComponent(setting, getPanel(), x, y + y2, width, 16));
                }
                y2 += height;
            }
        }
        calculateChildrenHeight();
    }

    public double getOffset() {
        return this.scissorBoxHeight;
    }

    private void drawChildren(int mouseX, int mouseY) {
        int childY = 15;
        List<Component> children = this.children;
        for (int i = 0, componentListSize = children.size(); i < componentListSize; i++) {
            Component child = children.get(i);
            if (!child.isHidden()) {
                child.setY(getY() + childY);
                child.onDraw(mouseX, mouseY);
                childY += 15;
            }
        }
    }

    private int calculateChildrenHeight() {
        int height = 0;
        List<Component> children = this.children;
        for (int i = 0, childrenSize = children.size(); i < childrenSize; i++) {
            Component component = children.get(i);
            if (!component.isHidden())
                height = (int) (height + component.getHeight() + component.getOffset());
        }
        return height;
    }

    public void onDraw(int mouseX, int mouseY) {
        Panel parent = getPanel();
        int x = parent.getX() + getX();
        int y = parent.getY() + getY();
        int height = getHeight();
        int width = getWidth();
        boolean hovered = isMouseOver(mouseX, mouseY);
        handleScissorBox();
        this.childrenHeight = calculateChildrenHeight();
        int color = this.module.isEnabled() ? new Color(hovered ? 210 : 255, hovered ? 20 : 50, hovered ? 20 : 50).getRGB() : new Color(hovered ? 170 : 230, hovered ? 170 : 230, hovered ? 170 : 230).getRGB();
        RenderingUtils.drawRect(x, y, x + width, y + height, new Color(23, 23, 23, 25).getRGB());
        FONT_RENDERER.drawCenteredStringWithShadow(this.module.getName(), x + width / 2F, y + height / 2.0F - 2.0F, color);
        if (this.scissorBoxHeight > 0.0D) {
            if (getSettings(this.module) != null)
                FONT_ARROWICON.drawStringWithShadow("b", x + width - 10.0, y + height / 2.0F - 1.0F, (new Color(255, 255, 255)).getRGB());
            if (parent.state != AnimationState.RETRACTING)
                RenderingUtils.prepareScissorBox(x, y, (x + width), (float) (y + Math.min(this.scissorBoxHeight, parent.scissorBoxHeight) + height));
            drawChildren(mouseX, mouseY);
        } else if (this.scissorBoxHeight < this.childrenHeight) {
            if (getSettings(this.module) != null)
                FONT_ARROWICON.drawStringWithShadow("a", x + width - 10.0, y + height / 2.0F - 1.0F, (new Color(255, 255, 255)).getRGB());
        }
    }

    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (this.scissorBoxHeight > 0.0D) {
            for (Component component : this.children) {
                (component).onMouseClick(mouseX, mouseY, mouseButton);
            }
        }
        if (isMouseOver(mouseX, mouseY))
            if (mouseButton == 0) {
                this.module.toggle();
            } else if (mouseButton == 1 && !this.children.isEmpty()) {
                if (this.scissorBoxHeight > 0.0D && (this.state == AnimationState.EXPANDING || this.state == AnimationState.STATIC)) {
                    this.state = AnimationState.RETRACTING;
                } else if (this.scissorBoxHeight < this.childrenHeight && (this.state == AnimationState.EXPANDING || this.state == AnimationState.STATIC)) {
                    this.state = AnimationState.EXPANDING;
                }
            }
    }

    public void onMouseRelease(int mouseX, int mouseY, int mouseButton) {
        if (this.scissorBoxHeight > 0.0D) {
            List<Component> componentList = this.children;
            for (int i = 0, componentListSize = componentList.size(); i < componentListSize; i++)
                ((Component) componentList.get(i)).onMouseRelease(mouseX, mouseY, mouseButton);
        }
    }

    public void onKeyPress(int typedChar, int keyCode) {
        if (this.scissorBoxHeight > 0.0D) {
            List<Component> componentList = this.children;
            for (int i = 0, componentListSize = componentList.size(); i < componentListSize; i++)
                ((Component) componentList.get(i)).onKeyPress(typedChar, keyCode);
        }
    }

    private void handleScissorBox() {
        int childrenHeight = this.childrenHeight;
        switch (this.state) {
            case EXPANDING:
                if (this.scissorBoxHeight < childrenHeight) {
                    this.scissorBoxHeight = AnimationUtil.animate(childrenHeight, this.scissorBoxHeight, 0.15D);
                } else if (this.scissorBoxHeight >= childrenHeight) {
                    this.state = AnimationState.STATIC;
                }
                this.scissorBoxHeight = clamp(this.scissorBoxHeight, childrenHeight);
                break;
            case RETRACTING:
                if (this.scissorBoxHeight > 0.0D) {
                    this.scissorBoxHeight = AnimationUtil.animate(0.0D, this.scissorBoxHeight, 0.15D);
                } else if (this.scissorBoxHeight <= 0.0D) {
                    this.state = AnimationState.STATIC;
                }
                this.scissorBoxHeight = clamp(this.scissorBoxHeight, childrenHeight);
                break;
            case STATIC:
                if (this.scissorBoxHeight > 0.0D && this.scissorBoxHeight != childrenHeight)
                    this.scissorBoxHeight = AnimationUtil.animate(childrenHeight, this.scissorBoxHeight, 0.15D);
                this.scissorBoxHeight = clamp(this.scissorBoxHeight, childrenHeight);
                break;
        }
    }

    private List<Setting> getSettings(Module mod) {
        List<Setting> settings = new ArrayList();
        for (Setting set : mod.getSettings().values()) {
            settings.add(set);
        }
        if (settings.isEmpty()) {
            return null;
        }
        return settings;
    }

    private double clamp(double a, double max) {
        if (a < 0.0D)
            return 0.0D;
        if (a > max)
            return max;
        return a;
    }
}
