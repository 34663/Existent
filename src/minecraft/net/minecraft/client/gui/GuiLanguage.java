package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;
import zyx.existent.gui.screen.impl.GuiExOptionButton;
import zyx.existent.utils.render.font.CFontRenderer;
import zyx.existent.utils.render.font.Fonts;

public class GuiLanguage extends GuiScreen {
    protected GuiScreen parentScreen;
    private GuiLanguage.List list;
    private final GameSettings game_settings_3;
    private final LanguageManager languageManager;
    private GuiExOptionButton forceUnicodeFontBtn;
    private GuiExOptionButton confirmSettingsBtn;
    private final CFontRenderer font = Fonts.default18;

    public GuiLanguage(GuiScreen screen, GameSettings gameSettingsObj, LanguageManager manager) {
        this.parentScreen = screen;
        this.game_settings_3 = gameSettingsObj;
        this.languageManager = manager;
    }

    public void initGui() {
        this.forceUnicodeFontBtn = this.addButton(new GuiExOptionButton(100, this.width / 2 - 155, this.height - 38, GameSettings.Options.FORCE_UNICODE_FONT, this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT)));
        this.confirmSettingsBtn = this.addButton(new GuiExOptionButton(6, this.width / 2 - 155 + 160, this.height - 38, I18n.format("gui.done")));
        this.list = new GuiLanguage.List(this.mc);
        this.list.registerScrollButtons(7, 8);
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            switch (button.id) {
                case 5:
                    break;

                case 6:
                    this.mc.displayGuiScreen(this.parentScreen);
                    break;

                case 100:
                    if (button instanceof GuiExOptionButton) {
                        this.game_settings_3.setOptionValue(((GuiExOptionButton) button).returnEnumOptions(), 1);
                        button.displayString = this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
                        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                        int i = scaledresolution.getScaledWidth();
                        int j = scaledresolution.getScaledHeight();
                        this.setWorldAndResolution(this.mc, i, j);
                    }

                    break;

                default:
                    this.list.actionPerformed(button);
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        font.drawCenteredString(I18n.format("options.language"), this.width / 2, 16, 16777215);
        font.drawCenteredString("(" + I18n.format("options.languageWarning") + ")", this.width / 2F, this.height - 56, 8421504);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class List extends GuiSlot {
        private final java.util.List<String> langCodeList = Lists.<String>newArrayList();
        private final Map<String, Language> languageMap = Maps.<String, Language>newHashMap();

        public List(Minecraft mcIn) {
            super(mcIn, GuiLanguage.this.width, GuiLanguage.this.height, 32, GuiLanguage.this.height - 65 + 4, 18);

            for (Language language : languageManager.getLanguages()) {
                this.languageMap.put(language.getLanguageCode(), language);
                this.langCodeList.add(language.getLanguageCode());
            }
        }

        protected int getSize() {
            return this.langCodeList.size();
        }

        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            Language language = this.languageMap.get(this.langCodeList.get(slotIndex));
            languageManager.setCurrentLanguage(language);
            game_settings_3.language = language.getLanguageCode();
            this.mc.refreshResources();
            fontRendererObj.setUnicodeFlag(languageManager.isCurrentLocaleUnicode() || game_settings_3.forceUnicodeFont);
            fontRendererObj.setBidiFlag(languageManager.isCurrentLanguageBidirectional());
            confirmSettingsBtn.displayString = I18n.format("gui.done");
            forceUnicodeFontBtn.displayString = game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
            game_settings_3.saveOptions();
        }

        protected boolean isSelected(int slotIndex) {
            return ((String) this.langCodeList.get(slotIndex)).equals(languageManager.getCurrentLanguage().getLanguageCode());
        }

        protected int getContentHeight() {
            return this.getSize() * 18;
        }

        protected void drawBackground() {
            drawDefaultBackground();
        }

        protected void func_192637_a(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_) {
            fontRendererObj.setBidiFlag(true);
            drawCenteredString(fontRendererObj, this.languageMap.get(this.langCodeList.get(p_192637_1_)).toString(), this.width / 2F, p_192637_3_ + 1, 16777215);
            fontRendererObj.setBidiFlag(languageManager.getCurrentLanguage().isBidirectional());
        }
    }
}
