package net.minecraft.client.gui;

import com.google.common.collect.Lists;

import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import zyx.existent.gui.screen.impl.GuiExButton;
import zyx.existent.utils.render.RenderingUtils;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

public class GuiSnooper extends GuiScreen {
    private final GuiScreen lastScreen;
    private final GameSettings game_settings_2;
    private final java.util.List<String> keys = Lists.<String>newArrayList();
    private final java.util.List<String> values = Lists.<String>newArrayList();
    private String title;
    private String[] desc;
    private GuiSnooper.List list;
    private GuiButton toggleButton;
    private CFontRenderer font = Fonts.default18;

    public GuiSnooper(GuiScreen p_i1061_1_, GameSettings p_i1061_2_) {
        this.lastScreen = p_i1061_1_;
        this.game_settings_2 = p_i1061_2_;
    }

    public void initGui() {
        this.title = I18n.format("options.snooper.title");
        String s = I18n.format("options.snooper.desc");
        java.util.List<String> list = Lists.newArrayList();

        list.addAll(font.listFormattedStringToWidth(s, this.width - 30));

        this.desc = list.toArray(new String[0]);
        this.keys.clear();
        this.values.clear();
        this.toggleButton = this.addButton(new GuiExButton(1, this.width / 2 - 152, this.height - 30, 150, 20, this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED)));
        this.buttonList.add(new GuiExButton(2, this.width / 2 + 2, this.height - 30, 150, 20, I18n.format("gui.done")));
        boolean flag = this.mc.getIntegratedServer() != null && this.mc.getIntegratedServer().getPlayerUsageSnooper() != null;

        for (Map.Entry<String, String> entry : (new TreeMap<String, String>(this.mc.getPlayerUsageSnooper().getCurrentStats())).entrySet()) {
            this.keys.add((flag ? "C " : "") + (String) entry.getKey());
            this.values.add(font.trimStringToWidth(entry.getValue(), this.width - 220));
        }

        if (flag) {
            for (Map.Entry<String, String> entry1 : (new TreeMap<String, String>(this.mc.getIntegratedServer().getPlayerUsageSnooper().getCurrentStats())).entrySet()) {
                this.keys.add("S " + (String) entry1.getKey());
                this.values.add(font.trimStringToWidth(entry1.getValue(), this.width - 220));
            }
        }

        this.list = new GuiSnooper.List();
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 2) {
                this.game_settings_2.saveOptions();
                this.game_settings_2.saveOptions();
                this.mc.displayGuiScreen(this.lastScreen);
            }

            if (button.id == 1) {
                this.game_settings_2.setOptionValue(GameSettings.Options.SNOOPER_ENABLED, 1);
                this.toggleButton.displayString = this.game_settings_2.getKeyBinding(GameSettings.Options.SNOOPER_ENABLED);
            }
        }
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
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        font.drawCenteredString(this.title, this.width / 2F, 8, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class List extends GuiSlot {
        public List() {
            super(GuiSnooper.this.mc, GuiSnooper.this.width, GuiSnooper.this.height, 80, GuiSnooper.this.height - 40, GuiSnooper.this.font.getHeight() + 1);
        }

        protected int getSize() {
            return GuiSnooper.this.keys.size();
        }

        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        }

        protected boolean isSelected(int slotIndex) {
            return false;
        }

        protected void drawBackground() {
        }

        protected void func_192637_a(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_) {
            font.drawString(GuiSnooper.this.keys.get(p_192637_1_), 10, p_192637_3_, 16777215);
            font.drawString(GuiSnooper.this.values.get(p_192637_1_), 230, p_192637_3_, 16777215);
        }

        protected int getScrollBarX() {
            return this.width - 10;
        }
    }
}
