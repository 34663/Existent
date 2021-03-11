package zyx.existent.gui.click.autumn;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import zyx.existent.Existent;
import zyx.existent.gui.click.autumn.panel.Panel;
import zyx.existent.module.Category;
import zyx.existent.module.modules.hud.ClickGui;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ExClickGui extends GuiScreen {
    private static ExClickGui INSTANCE;
    private final ArrayList<Object> panels = Lists.newArrayList();

    public void initGui() {
        if (this.mc.theWorld != null && !this.mc.gameSettings.ofFastRender && (Boolean) Existent.getModuleManager().getClazz(ClickGui.class).getSetting(ClickGui.BLUH).getValue())
            this.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
    }

    private ExClickGui() {
        Category[] categories = Category.values();
        for (int i = categories.length - 1; i >= 0; i--)
            this.panels.add(new Panel(categories[i], 5 + 120 * i, 5));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (int i = 0, panelsSize = this.panels.size(); i < panelsSize; i++) {
            ((Panel) this.panels.get(i)).onDraw(mouseX, mouseY);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (int i = 0, panelsSize = this.panels.size(); i < panelsSize; i++)
            ((Panel)this.panels.get(i)).onMouseClick(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (int i = 0, panelsSize = this.panels.size(); i < panelsSize; i++)
            ((Panel)this.panels.get(i)).onMouseRelease(mouseX, mouseY, state);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (int i = 0, panelsSize = this.panels.size(); i < panelsSize; i++)
            ((Panel)this.panels.get(i)).onKeyPress(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    public void onGuiClosed() {
        this.mc.entityRenderer.stopUseShader();
        super.onGuiClosed();
    }

    public static ExClickGui getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ExClickGui();
        return INSTANCE;
    }

    public static Color getColor() {
        return new Color(230, 50, 50);
    }
}
