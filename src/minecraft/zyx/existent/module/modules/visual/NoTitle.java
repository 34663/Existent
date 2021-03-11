package zyx.existent.module.modules.visual;

import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class NoTitle extends Module {
    public static String MAIN = "MAINTITLE";
    public static String SUB = "SUBTITLE";

    public NoTitle(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MAIN, new Setting(MAIN, true, "Main Title"));
        settings.put(SUB, new Setting(SUB, true, "Sub Title"));
    }
}
