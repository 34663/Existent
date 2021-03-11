package zyx.existent.module.modules.misc;

import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class BetterSounds extends Module {
    public static String FIGHT = "FIGHT";
    public static String TEST = "TEST";

    public BetterSounds(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(FIGHT, new Setting(FIGHT, true, ""));
        settings.put(TEST, new Setting(TEST, false, ""));
    }
}
