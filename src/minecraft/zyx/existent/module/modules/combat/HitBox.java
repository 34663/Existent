package zyx.existent.module.modules.combat;

import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class HitBox extends Module {
    public static String SIZE = "SIZE";

    public HitBox(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(SIZE, new Setting(SIZE, 0.4, "Box Size.", 0.1, 0.0, 1.0));
    }
}
