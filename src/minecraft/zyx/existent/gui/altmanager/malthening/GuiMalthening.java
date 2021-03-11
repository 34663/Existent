package zyx.existent.gui.altmanager.malthening;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import zyx.existent.gui.altmanager.GuiAltManager;
import zyx.existent.gui.altmanager.malthening.api.Maltening;
import zyx.existent.gui.screen.impl.GuiExButton;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.awt.*;
import java.io.IOException;

public class GuiMalthening extends GuiScreen {
    private final GuiAltManager manager;
    private GuiExButton generate;
    private GuiTextField tokenField;
    public String status = "";

    public GuiMalthening(GuiScreen manager) {
        this.manager = (GuiAltManager) manager;
    }

    public void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled)
            return;

        switch (button.id) {
            case 0:
                mc.displayGuiScreen(manager);
                break;
            case 1:
                this.status = Maltening.genMalt(tokenField.getText());
                break;
        }
        super.actionPerformed(button);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderingUtils.drawRect(0, 0, width, height, new Color(50, 45, 45, 255).getRGB());
        CFontRenderer fonts = Fonts.default18;
        fonts.drawCenteredString("TheMaltening", width / 2F, 6, 0xffffff);
        fonts.drawCenteredString(status, width / 2F, 18, 0xffffff);

        tokenField.drawTextBox();
        if (this.tokenField.getText().isEmpty() && !this.tokenField.isFocused()) {
            this.drawString(this.mc.fontRendererObj, "Token", this.width / 2 - 96, 156, -7829368);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        FontRenderer font = mc.fontRendererObj;
        Keyboard.enableRepeatEvents(true);

        generate = new GuiExButton(1, width / 2 - 100, 175, "Generate");
        buttonList.add(generate);
        buttonList.add(new GuiExButton(0, width / 2 - 100, height - 60, "Back"));
        tokenField = new GuiTextField(666, font, width / 2 - 100, 150, 200, 20);
        tokenField.maxStringLength = Integer.MAX_VALUE;
        super.initGui();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(manager);
            return;
        }

        if (tokenField.isFocused)
            tokenField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        tokenField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        tokenField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }
}
