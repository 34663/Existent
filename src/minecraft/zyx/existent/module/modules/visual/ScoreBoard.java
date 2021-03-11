package zyx.existent.module.modules.visual;

import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class ScoreBoard extends Module {
    public static String REMOVE = "REMOVE";
    public static String POINTS = "REMOVE POINTS";
    public static String POSITION = "POSITION";

    public ScoreBoard(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(REMOVE, new Setting(REMOVE, false, "Remove Scoreboard"));
        settings.put(POINTS, new Setting(POINTS, false, "Remove ScorePoints"));
        settings.put(POSITION, new Setting(POSITION, 0, "ScoreBoard Position", 1, -150, 200));
    }
}
