package zyx.existent.gui.screen.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.MathHelper;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

import java.awt.*;

public class GuiExOptionSlider extends GuiButton {
    private int fade = 20;
    private float sliderValue;
    public boolean dragging;
    private final GameSettings.Options options;
    private final float minValue;
    private final float maxValue;

    public GuiExOptionSlider(int buttonId, int x, int y, GameSettings.Options optionIn) {
        this(buttonId, x, y, optionIn, 0.0F, 1.0F);
    }

    public GuiExOptionSlider(int buttonId, int x, int y, GameSettings.Options optionIn, float minValueIn, float maxValue) {
        super(buttonId, x, y, 150, 20, "");
        this.sliderValue = 1.0F;
        this.options = optionIn;
        this.minValue = minValueIn;
        this.maxValue = maxValue;
        Minecraft minecraft = Minecraft.getMinecraft();
        this.sliderValue = optionIn.normalizeValue(minecraft.gameSettings.getOptionFloatValue(optionIn));
        this.displayString = minecraft.gameSettings.getKeyBinding(optionIn);
    }

    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);

            if (this.dragging) {
                this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
                this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
                float f = this.options.denormalizeValue(this.sliderValue);
                mc.gameSettings.setOptionFloatValue(this.options, f);
                this.sliderValue = this.options.normalizeValue(f);
                this.displayString = mc.gameSettings.getKeyBinding(this.options);
            }

            Color color = new Color(this.fade, 20, 20, 255);
            if (!this.enabled) {
                color = new Color(10, 10, 10, 255);
            } else if (this.hovered) {
                if (this.fade < 100)
                    this.fade += 20;
            } else {
                if (this.fade > 20)
                    this.fade -= 20;
            }

            RenderingUtils.drawRectWithEdge(this.xPosition + this.sliderValue * (float) (this.width - 8), this.yPosition, 8, 20, color, new Color(30, 30, 30, 255));
        }
    }

    @Override
    public void drawButton(Minecraft p_191745_1_, int mouseX, int mouseY, float p_191745_4_) {
        if (this.visible) {
            Color text = new Color(255, 255, 255, 255);

            if (!this.enabled)
                text = new Color(100, 100, 100, 255);
            RenderingUtils.drawRectWithEdge(this.xPosition, this.yPosition, this.width, this.height, new Color(20, 20, 20, 255), new Color(30, 30, 30, 255));
            CFontRenderer var4 = Fonts.default18;
            this.mouseDragged(p_191745_1_, mouseX, mouseY);
            var4.drawCenteredString(this.displayString, this.xPosition + this.width / 2F, this.yPosition + (this.height - 8) / 2F, text.getRGB());
        }
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
            this.sliderValue = MathHelper.clamp(this.sliderValue, 0.0F, 1.0F);
            mc.gameSettings.setOptionFloatValue(this.options, this.options.denormalizeValue(this.sliderValue));
            this.displayString = mc.gameSettings.getKeyBinding(this.options);
            this.dragging = true;
            return true;
        } else {
            return false;
        }
    }

    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }
}
