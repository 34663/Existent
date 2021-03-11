package net.minecraft.client.gui;

import java.awt.*;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import zyx.existent.gui.screen.impl.GuiExButton;
import zyx.existent.gui.screen.impl.GuiExOptionButton;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

public class GuiScreenOptionsSounds extends GuiScreen {
    private final GuiScreen parent;
    private final GameSettings game_settings_4;
    protected String title = "Options";
    private String offDisplayString;
    private final CFontRenderer font = Fonts.default18;

    public GuiScreenOptionsSounds(GuiScreen parentIn, GameSettings settingsIn) {
        this.parent = parentIn;
        this.game_settings_4 = settingsIn;
    }

    public void initGui() {
        this.title = I18n.format("options.sounds.title");
        this.offDisplayString = I18n.format("options.off");
        int i = 0;
        this.buttonList.add(new GuiScreenOptionsSounds.Button(SoundCategory.MASTER.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, true));
        i = i + 2;

        for (SoundCategory soundcategory : SoundCategory.values()) {
            if (soundcategory != SoundCategory.MASTER) {
                this.buttonList.add(new GuiScreenOptionsSounds.Button(soundcategory.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundcategory, false));
                ++i;
            }
        }

        int j = this.width / 2 - 75;
        int k = this.height / 6 - 12;
        ++i;
        this.buttonList.add(new GuiExOptionButton(201, j, k + 24 * (i >> 1), GameSettings.Options.SHOW_SUBTITLES, this.game_settings_4.getKeyBinding(GameSettings.Options.SHOW_SUBTITLES)));
        this.buttonList.add(new GuiExButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
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
                this.mc.displayGuiScreen(this.parent);
            } else if (button.id == 201) {
                this.mc.gameSettings.setOptionValue(GameSettings.Options.SHOW_SUBTITLES, 1);
                button.displayString = this.mc.gameSettings.getKeyBinding(GameSettings.Options.SHOW_SUBTITLES);
                this.mc.gameSettings.saveOptions();
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (mc.theWorld != null) {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            RenderingUtils.drawRect(0, 0, width, height, new Color(50, 45, 45, 255).getRGB());
        }
        font.drawCenteredString(this.title, this.width / 2F, 15, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected String getDisplayString(SoundCategory category) {
        float f = this.game_settings_4.getSoundLevel(category);
        return f == 0.0F ? this.offDisplayString : (int) (f * 100.0F) + "%";
    }

    class Button extends GuiExButton {
        private int fade = 20;
        private final SoundCategory category;
        private final String categoryName;
        public float volume = 1.0F;
        public boolean pressed;

        public Button(int buttonid, int x, int y, SoundCategory categoryIn, boolean master) {
            super(buttonid, x, y, master ? 310 : 150, 20, "");
            this.category = categoryIn;
            this.categoryName = I18n.format("soundCategory." + categoryIn.getName());
            this.displayString = this.categoryName + ": " + getDisplayString(categoryIn);
            this.volume = game_settings_4.getSoundLevel(categoryIn);
        }

        protected int getHoverState(boolean mouseOver) {
            return 0;
        }

        protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                this.hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);

                if (this.pressed) {
                    this.volume = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
                    this.volume = MathHelper.clamp(this.volume, 0.0F, 1.0F);
                    mc.gameSettings.setSoundLevel(this.category, this.volume);
                    mc.gameSettings.saveOptions();
                    this.displayString = this.categoryName + ": " + getDisplayString(this.category);
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

                RenderingUtils.drawRectWithEdge(this.xPosition + this.volume * (float) (this.width - 8), this.yPosition, 8, 20, color, new Color(30, 30, 30, 255));
            }
        }

        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                this.volume = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
                this.volume = MathHelper.clamp(this.volume, 0.0F, 1.0F);
                mc.gameSettings.setSoundLevel(this.category, this.volume);
                mc.gameSettings.saveOptions();
                this.displayString = this.categoryName + ": " + getDisplayString(this.category);
                this.pressed = true;
                return true;
            } else {
                return false;
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

        public void playPressSound(SoundHandler soundHandlerIn) {
        }

        public void mouseReleased(int mouseX, int mouseY) {
            if (this.pressed) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

            this.pressed = false;
        }
    }
}
