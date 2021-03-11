package zyx.existent.module.modules.visual;

import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;

public class Cosmetics extends Module {
    private final String CAPEMODE = "CAPE MODE";
    private final String WINGMODE = "WING MODE";
    private final String CAPE = "CAPE";
    private final String WING = "WING";
    private final String SCALE = "SCALE";

    public static boolean cape;
    public static boolean wing;
    public static double scale;
    public static String capepng;
    public static String wingpng;

    public Cosmetics(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(CAPEMODE, new Setting(CAPEMODE, new Options("Cape", "Shivas", new String[]{"Shivas", "SadGirl"}), "Cosmetic method"));
        settings.put(WINGMODE, new Setting(WINGMODE, new Options("Wing", "Dragon", new String[]{"Dragon"}), "Cosmetic method"));
        settings.put(WING, new Setting(WING, false, "DragonWind Cosmetic"));
        settings.put(CAPE, new Setting(CAPE, false, "Cape Cosmetic"));
        settings.put(SCALE, new Setting(SCALE, 0.75, "Scale.", 0.01, 0.10, 2.00));
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        cape = (Boolean) settings.get(CAPE).getValue();
        wing = (Boolean) settings.get(WING).getValue();
        scale = ((Number) settings.get(SCALE).getValue()).doubleValue();
        capepng = ((Options) settings.get(CAPEMODE).getValue()).getSelected();
        wingpng = ((Options) settings.get(WINGMODE).getValue()).getSelected();
    }
}
