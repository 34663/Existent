package net.minecraft.client.gui;

import java.awt.*;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import zyx.existent.gui.screen.impl.GuiExButton;
import zyx.existent.gui.screen.impl.GuiExOptionButton;
import zyx.existent.gui.screen.impl.GuiExOptionSlider;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

public class GuiControls extends GuiScreen {
    private static final GameSettings.Options[] OPTIONS_ARR = new GameSettings.Options[]{GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN, GameSettings.Options.AUTO_JUMP};
    private final GuiScreen parentScreen;
    protected String screenTitle = "Controls";
    private final GameSettings options;
    public KeyBinding buttonId;
    public long time;
    private GuiKeyBindingList keyBindingList;
    private GuiButton buttonReset;
    private final CFontRenderer font = Fonts.default18;

    public GuiControls(GuiScreen screen, GameSettings settings) {
        this.parentScreen = screen;
        this.options = settings;
    }

    public void initGui() {
        this.keyBindingList = new GuiKeyBindingList(this, this.mc);
        this.buttonList.add(new GuiExButton(200, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("gui.done")));
        this.buttonReset = this.addButton(new GuiExButton(201, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("controls.resetAll")));
        this.screenTitle = I18n.format("controls.title");
        int i = 0;

        for (GameSettings.Options gamesettings$options : OPTIONS_ARR) {
            if (gamesettings$options.getEnumFloat()) {
                this.buttonList.add(new GuiExOptionSlider(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options));
            } else {
                this.buttonList.add(new GuiExOptionButton(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options, this.options.getKeyBinding(gamesettings$options)));
            }

            ++i;
        }
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.keyBindingList.handleMouseInput();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 200) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 201) {
            for (KeyBinding keybinding : this.mc.gameSettings.keyBindings) {
                keybinding.setKeyCode(keybinding.getKeyCodeDefault());
            }

            KeyBinding.resetKeyBindingArrayAndHash();
        } else if (button.id < 100 && button instanceof GuiExOptionButton) {
            this.options.setOptionValue(((GuiExOptionButton) button).returnEnumOptions(), 1);
            button.displayString = this.options.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.buttonId != null) {
            this.options.setOptionKeyBinding(this.buttonId, -100 + mouseButton);
            this.buttonId = null;
            KeyBinding.resetKeyBindingArrayAndHash();
        } else if (mouseButton != 0 || !this.keyBindingList.mouseClicked(mouseX, mouseY, mouseButton)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state != 0 || !this.keyBindingList.mouseReleased(mouseX, mouseY, state)) {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.buttonId != null) {
            if (keyCode == 1) {
                this.options.setOptionKeyBinding(this.buttonId, 0);
            } else if (keyCode != 0) {
                this.options.setOptionKeyBinding(this.buttonId, keyCode);
            } else if (typedChar > 0) {
                this.options.setOptionKeyBinding(this.buttonId, typedChar + 256);
            }

            this.buttonId = null;
            this.time = Minecraft.getSystemTime();
            KeyBinding.resetKeyBindingArrayAndHash();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (mc.theWorld != null) {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            RenderingUtils.drawRect(0, 0, width, height, new Color(50, 45, 45, 255).getRGB());
        }
        this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
        font.drawCenteredString(this.screenTitle, this.width / 2F, 8, 16777215);
        boolean flag = false;

        for (KeyBinding keybinding : this.options.keyBindings) {
            if (keybinding.getKeyCode() != keybinding.getKeyCodeDefault()) {
                flag = true;
                break;
            }
        }

        this.buttonReset.enabled = flag;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
