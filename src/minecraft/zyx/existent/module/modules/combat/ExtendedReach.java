package zyx.existent.module.modules.combat;

import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class ExtendedReach extends Module {
    public static String REACH = "REACH";
    private final String GHOST = "GHOST";
    private double reach = 3.9F;

    public ExtendedReach(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(REACH, new Setting(REACH, 4.2, "Reach", 0.1, 1.0, 7.0));
        settings.put(GHOST, new Setting(GHOST, true, "Test"));
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        setSuffix(String.valueOf(getReach()));

        if ((Boolean) settings.get(GHOST).getValue()) {
            if (Math.random() > 0.20000000298023224) {
                this.reach = 4.4;
            } else {
                this.reach = 3.9;
            }
        } else {
            this.reach = ((Number) settings.get(REACH).getValue()).doubleValue();
        }
    }

    public double getReach() {
        return reach;
    }
}
