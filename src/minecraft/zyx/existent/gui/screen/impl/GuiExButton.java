package zyx.existent.gui.screen.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.awt.*;

public class GuiExButton extends GuiButton {
    private int fade = 20;

    public GuiExButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public GuiExButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public void drawButton(Minecraft p_191745_1_, int mouseX, int mouseY, float p_191745_4_) {
        if (this.visible) {
            this.hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);
            Color color = new Color(this.fade, 20, 20, 255);
            Color text = new Color(255, 255, 255, 255);

            if (!this.enabled) {
                color = new Color(10, 10, 10, 255);
                text = new Color(100, 100, 100, 255);
            } else if (this.hovered) {
                if (this.fade < 100)
                    this.fade += 20;
            } else {
                if (this.fade > 20)
                    this.fade -= 20;
            }
            RenderingUtils.drawRectWithEdge(this.xPosition, this.yPosition, this.width, this.height, color, new Color(30, 30, 30, 255));
            CFontRenderer var4 = Fonts.default18;
            var4.drawCenteredString(this.displayString, this.xPosition + this.width / 2F, this.yPosition + (this.height - 8) / 2F, text.getRGB());
        }
    }
}
