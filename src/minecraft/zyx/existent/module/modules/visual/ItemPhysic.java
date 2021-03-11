package zyx.existent.module.modules.visual;

import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class ItemPhysic extends Module {
    public static String SPEED = "SPEED";

    public ItemPhysic(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(SPEED, new Setting(SPEED, 1.0, "Hurtcam Angle", 0.1, 0.1, 10.0));
    }
}
