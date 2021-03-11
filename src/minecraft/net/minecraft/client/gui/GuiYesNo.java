package net.minecraft.client.gui;

import com.google.common.collect.Lists;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.resources.I18n;
import zyx.existent.gui.screen.impl.GuiExOptionButton;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

public class GuiYesNo extends GuiScreen {
    protected GuiYesNoCallback parentScreen;
    protected String messageLine1;
    private final String messageLine2;
    private final List<String> listLines = Lists.<String>newArrayList();
    protected String confirmButtonText;
    protected String cancelButtonText;
    protected int parentButtonClickedId;
    private int ticksUntilEnable;
    private final CFontRenderer font = Fonts.default18;

    public GuiYesNo(GuiYesNoCallback p_i1082_1_, String p_i1082_2_, String p_i1082_3_, int p_i1082_4_) {
        this.parentScreen = p_i1082_1_;
        this.messageLine1 = p_i1082_2_;
        this.messageLine2 = p_i1082_3_;
        this.parentButtonClickedId = p_i1082_4_;
        this.confirmButtonText = I18n.format("gui.yes");
        this.cancelButtonText = I18n.format("gui.no");
    }

    public GuiYesNo(GuiYesNoCallback p_i1083_1_, String p_i1083_2_, String p_i1083_3_, String p_i1083_4_, String p_i1083_5_, int p_i1083_6_) {
        this.parentScreen = p_i1083_1_;
        this.messageLine1 = p_i1083_2_;
        this.messageLine2 = p_i1083_3_;
        this.confirmButtonText = p_i1083_4_;
        this.cancelButtonText = p_i1083_5_;
        this.parentButtonClickedId = p_i1083_6_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        this.buttonList.add(new GuiExOptionButton(0, this.width / 2 - 155, this.height / 6 + 96, this.confirmButtonText));
        this.buttonList.add(new GuiExOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.cancelButtonText));
        this.listLines.clear();
        this.listLines.addAll(this.fontRendererObj.listFormattedStringToWidth(this.messageLine2, this.width - 50));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        this.parentScreen.confirmClicked(button.id == 0, this.parentButtonClickedId);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (mc.theWorld != null) {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            RenderingUtils.drawRect(0, 0, width, height, new Color(50, 45, 45, 255).getRGB());
        }
        font.drawCenteredString(this.messageLine1, this.width / 2F, 70, 16777215);
        int i = 90;

        for (String s : this.listLines) {
            font.drawCenteredString(s, this.width / 2F, i, 16777215);
            i += this.fontRendererObj.FONT_HEIGHT;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Sets the number of ticks to wait before enabling the buttons.
     */
    public void setButtonDelay(int p_146350_1_) {
        this.ticksUntilEnable = p_146350_1_;

        for (GuiButton guibutton : this.buttonList) {
            guibutton.enabled = false;
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        super.updateScreen();

        if (--this.ticksUntilEnable == 0) {
            for (GuiButton guibutton : this.buttonList) {
                guibutton.enabled = true;
            }
        }
    }
}
