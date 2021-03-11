package zyx.existent.gui.screen.impl;

import net.minecraft.client.settings.GameSettings;
import optifine.IOptionControl;

public class GuiExOptionSliderOF extends GuiExOptionSlider implements IOptionControl {
    private GameSettings.Options option = null;

    public GuiExOptionSliderOF(int p_i50_1_, int p_i50_2_, int p_i50_3_, GameSettings.Options p_i50_4_) {
        super(p_i50_1_, p_i50_2_, p_i50_3_, p_i50_4_);
        this.option = p_i50_4_;
    }

    public GameSettings.Options getOption() {
        return this.option;
    }
}
