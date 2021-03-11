package zyx.existent.module.modules.visual;

import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class Outline extends Module {
    public static String WIDTH = "WIDTH";

    public Outline(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(WIDTH, new Setting(WIDTH, 1.0, "Outline Width.", 0.1, 0.1, 5.0));
    }
}
