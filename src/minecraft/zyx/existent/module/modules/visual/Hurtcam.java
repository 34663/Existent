package zyx.existent.module.modules.visual;

import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class Hurtcam extends Module {
    public static String HURTANGLE = "HURTANGLE";

    public Hurtcam(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(HURTANGLE, new Setting(HURTANGLE, 0, "Hurtcam Angle", 0.1, -14.0, 14.0));
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        setSuffix(String.valueOf(((Number) settings.get(HURTANGLE).getValue()).doubleValue()));
    }
}
