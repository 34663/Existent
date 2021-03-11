package zyx.existent.gui.click.autumn.panel;

import org.lwjgl.opengl.GL11;
import zyx.existent.Existent;
import zyx.existent.gui.click.autumn.panel.component.Component;
import zyx.existent.gui.click.autumn.panel.component.impl.ModuleComponent;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.utils.render.Colors;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.animate.AnimationUtil;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Panel {
    public static final int HEADER_SIZE = 20;
    public static final int HEADER_OFFSET = 2;
    private final CFontRenderer fr = Fonts.comfortaa18;
    private final Category category;
    private Module module;
    private final List<Component> components = new ArrayList<>();
    private final int width;
    public double scissorBoxHeight;
    private int x;
    private int lastX;
    private int y;
    private int lastY;
    private int height;
    public AnimationState state = AnimationState.STATIC;
    private boolean dragging;

    public Panel(Category category, int x, int y) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = 100;
        int componentY = 20;
        int componentHeight = 15;
        List<Module> modulesForCategory = Existent.getModuleManager().getModuleByCategory(category);

        modulesForCategory.sort(new PanelSort());
        for (int i = 0, modulesForCategorySize = modulesForCategory.size(); i < modulesForCategorySize; i++) {
            Module module = modulesForCategory.get(i);
            this.module = module;
            ModuleComponent moduleComponent = new ModuleComponent(module, this, 0, componentY, this.width, 15);
            this.components.add(moduleComponent);
            componentY += 15;
        }
        this.height = componentY - 20;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private void updateComponentHeight() {
        int componentY = 20;
        List<Component> componentList = this.components;
        for (int i = 0, componentListSize = componentList.size(); i < componentListSize; i++) {
            Component component = componentList.get(i);
            component.setY(componentY);
            componentY = (int) (componentY + component.getHeight() + component.getOffset());
        }
        this.height = componentY - 20;
    }

    public final void onDraw(int mouseX, int mouseY) {
        int x = this.x;
        int y = this.y;
        int width = this.width;
        updateComponentHeight();
        handleScissorBox();
        handleDragging(mouseX, mouseY);
        double scissorBoxHeight = this.scissorBoxHeight;
        int backgroundColor = new Color(30, 30, 30, 240).getRGB();
        RenderingUtils.drawGradientRect(x - 2, y, x + width + 2, y + 20, new Color(140, 10, 10, 255).getRGB(), new Color(255, 50, 50, 255).getRGB());
        this.fr.drawStringWithShadow(this.category.toString(), x + 3, y + 10.0F - 3.0F, Colors.getColor(240));
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        RenderingUtils.prepareScissorBox((x - 2), (y + 20 - 2), (x + width + 2), (float) ((y + 20) + scissorBoxHeight));
        RenderingUtils.drawRect((x - 2), y, (x + width + 2), (y + 20) + scissorBoxHeight, backgroundColor);
        List<Component> components = this.components;

        for (int i = 0, componentsSize = components.size(); i < componentsSize; i++) {
            (components.get(i)).onDraw(mouseX, mouseY);
            if (i != componentsSize - 1)
                RenderingUtils.prepareScissorBox((x - 2), (y + 20), (x + width + 2), (float) ((y + 20) + scissorBoxHeight));
        }
        GL11.glDisable(3089);
        GL11.glPopMatrix();
    }

    public final void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        int x = this.x;
        int y = this.y;
        int width = this.width;
        double scissorBoxHeight = this.scissorBoxHeight;
        if (mouseX > x - 2 && mouseX < x + width + 2 && mouseY > y && mouseY < y + 20)
            if (mouseButton == 1) {
                if (scissorBoxHeight > 0.0D && (this.state == AnimationState.EXPANDING || this.state == AnimationState.STATIC)) {
                    this.state = AnimationState.RETRACTING;
                } else if (scissorBoxHeight < (this.height + 2) && (this.state == AnimationState.EXPANDING || this.state == AnimationState.STATIC)) {
                    this.state = AnimationState.EXPANDING;
                }
            } else if (mouseButton == 0 && !this.dragging) {
                this.lastX = x - mouseX;
                this.lastY = y - mouseY;
                this.dragging = true;
            }
        List<Component> components = this.components;
        for (int i = 0, componentsSize = components.size(); i < componentsSize; i++) {
            Component component = components.get(i);
            int componentY = component.getY();
            if (componentY < scissorBoxHeight + 20.0D)
                component.onMouseClick(mouseX, mouseY, mouseButton);
        }
    }

    public final void onMouseRelease(int mouseX, int mouseY, int mouseButton) {
        if (this.dragging)
            this.dragging = false;
        if (this.scissorBoxHeight > 0.0D) {
            List<Component> components = this.components;
            for (int i = 0, componentsSize = components.size(); i < componentsSize; i++)
                ((Component) components.get(i)).onMouseRelease(mouseX, mouseY, mouseButton);
        }
    }

    public final void onKeyPress(char typedChar, int keyCode) {
        if (this.scissorBoxHeight > 0.0D) {
            List<Component> components = this.components;
            for (int i = 0, componentsSize = components.size(); i < componentsSize; i++)
                ((Component) components.get(i)).onKeyPress(typedChar, keyCode);
        }
    }

    private void handleDragging(int mouseX, int mouseY) {
        if (this.dragging) {
            this.x = mouseX + this.lastX;
            this.y = mouseY + this.lastY;
        }
    }

    private void handleScissorBox() {
        int height = this.height;
        switch (this.state) {
            case EXPANDING:
                if (this.scissorBoxHeight < (height + 2)) {
                    this.scissorBoxHeight = AnimationUtil.animate((height + 2), this.scissorBoxHeight, 0.2D);
                    break;
                }
                if (this.scissorBoxHeight >= (height + 2))
                    this.state = AnimationState.STATIC;
                break;
            case RETRACTING:
                if (this.scissorBoxHeight > 0.0D) {
                    this.scissorBoxHeight = AnimationUtil.animate(0.0D, this.scissorBoxHeight, 0.2D);
                    break;
                }
                if (this.scissorBoxHeight <= 0.0D)
                    this.state = AnimationState.STATIC;
                break;
            case STATIC:
                if (this.scissorBoxHeight > 0.0D && this.scissorBoxHeight != (height + 2))
                    this.scissorBoxHeight = AnimationUtil.animate((height + 2), this.scissorBoxHeight, 0.2D);
                this.scissorBoxHeight = clamp(this.scissorBoxHeight, (height + 2));
                break;
        }
    }

    private double clamp(double a, double max) {
        if (a < 0.0D)
            return 0.0D;
        if (a > max)
            return max;
        return a;
    }
}
