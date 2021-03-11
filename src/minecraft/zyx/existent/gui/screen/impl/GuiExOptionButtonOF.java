package zyx.existent.gui.screen.impl;

import net.minecraft.client.settings.GameSettings;
import optifine.IOptionControl;

public class GuiExOptionButtonOF extends GuiExOptionButton implements IOptionControl {
    private GameSettings.Options option = null;

    public GuiExOptionButtonOF(int p_i49_1_, int p_i49_2_, int p_i49_3_, GameSettings.Options p_i49_4_, String p_i49_5_) {
        super(p_i49_1_, p_i49_2_, p_i49_3_, p_i49_4_, p_i49_5_);
        this.option = p_i49_4_;
    }

    public GameSettings.Options getOption() {
        return this.option;
    }
}
