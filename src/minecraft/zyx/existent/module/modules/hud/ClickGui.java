package zyx.existent.module.modules.hud;

import zyx.existent.gui.click.autumn.ExClickGui;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class ClickGui extends Module {
    public static String BLUH ="BLUH";

    public ClickGui(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(BLUH, new Setting(BLUH, true, ""));
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(ExClickGui.getInstance());
        toggle();
        super.onEnable();
    }
}
