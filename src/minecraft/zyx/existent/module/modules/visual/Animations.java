package zyx.existent.module.modules.visual;

import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;

public class Animations extends Module {
    public static String MODE = "MODE";
    public static String OLDANIM = "OLDANIMATION";
    public static String ROTATION = "ROTATION";
    public static String SMOOTH = "SMOOTH";
    public static String SPEED = "SPEED";
    public static String MINI = "MINI";

    public Animations(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(SPEED, new Setting(SPEED, 6, "SwingSpeed", 1, 3, 20));
        settings.put(SMOOTH, new Setting(SMOOTH, false, "Smooth Animation"));
        settings.put(OLDANIM, new Setting(OLDANIM, true, "Old Animation"));
        settings.put(ROTATION, new Setting(ROTATION, false, "Rotation"));
        settings.put(MINI, new Setting(MINI, false, "MiniItem"));
        settings.put(MODE, new Setting(MODE, new Options("Mode", "1.7", new String[]{"1.7", "Swaing", "Matt", "Ejaculation", "Hanabi", "Luna", "Exhibobo", "Pump", "Spin", "Slide", "MiniJump", "Mini"}), "Animation method"));
    }
}
