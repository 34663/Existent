package net.minecraft.client.gui;

import java.awt.*;
import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EnumPlayerModelParts;
import zyx.existent.gui.screen.impl.GuiExButton;
import zyx.existent.gui.screen.impl.GuiExOptionButton;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

public class GuiCustomizeSkin extends GuiScreen {
    private final CFontRenderer font = Fonts.default18;
    private final GuiScreen parentScreen;
    private String title;

    public GuiCustomizeSkin(GuiScreen parentScreenIn) {
        this.parentScreen = parentScreenIn;
    }

    public void initGui() {
        int i = 0;
        this.title = I18n.format("options.skinCustomisation.title");

        for (EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values()) {
            this.buttonList.add(new GuiCustomizeSkin.ButtonPart(enumplayermodelparts.getPartId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, enumplayermodelparts));
            ++i;
        }

        this.buttonList.add(new GuiExOptionButton(199, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), GameSettings.Options.MAIN_HAND, this.mc.gameSettings.getKeyBinding(GameSettings.Options.MAIN_HAND)));
        ++i;

        if (i % 2 == 1) {
            ++i;
        }

        this.buttonList.add(new GuiExButton(200, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), I18n.format("gui.done")));
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.gameSettings.saveOptions();
        }

        super.keyTyped(typedChar, keyCode);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentScreen);
            } else if (button.id == 199) {
                this.mc.gameSettings.setOptionValue(GameSettings.Options.MAIN_HAND, 1);
                button.displayString = this.mc.gameSettings.getKeyBinding(GameSettings.Options.MAIN_HAND);
                this.mc.gameSettings.sendSettingsToServer();
            } else if (button instanceof GuiCustomizeSkin.ButtonPart) {
                EnumPlayerModelParts enumplayermodelparts = ((GuiCustomizeSkin.ButtonPart) button).playerModelParts;
                this.mc.gameSettings.switchModelPartEnabled(enumplayermodelparts);
                button.displayString = this.getMessage(enumplayermodelparts);
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (mc.theWorld != null) {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            RenderingUtils.drawRect(0, 0, width, height, new Color(50, 45, 45, 255).getRGB());
        }
        font.drawCenteredString(this.title, this.width / 2F, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String getMessage(EnumPlayerModelParts playerModelParts) {
        String s;

        if (this.mc.gameSettings.getModelParts().contains(playerModelParts)) {
            s = I18n.format("options.on");
        } else {
            s = I18n.format("options.off");
        }

        return playerModelParts.getName().getFormattedText() + ": " + s;
    }

    class ButtonPart extends GuiExButton {
        private final EnumPlayerModelParts playerModelParts;

        private ButtonPart(int p_i45514_2_, int p_i45514_3_, int p_i45514_4_, int p_i45514_5_, int p_i45514_6_, EnumPlayerModelParts playerModelParts) {
            super(p_i45514_2_, p_i45514_3_, p_i45514_4_, p_i45514_5_, p_i45514_6_, getMessage(playerModelParts));
            this.playerModelParts = playerModelParts;
        }
    }
}
