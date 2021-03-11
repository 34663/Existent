package zyx.existent.module.modules.combat;

import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventTick;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.misc.MiscUtils;
import zyx.existent.utils.timer.Timer;

public class AutoClicker extends Module {
    public Timer timer = new Timer();
    public int cps;
    public int random;

    public final String MINAPS = "MINAPS";
    public final String MAXAPS = "MAXAPS";

    public AutoClicker(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MAXAPS, new Setting(MAXAPS, 11, "MaxAps.", 1, 1, 70));
        settings.put(MINAPS, new Setting(MINAPS, 7, "MinAps.", 1, 1, 70));
    }

    @EventTarget
    public void onTick(EventTick event) {
        if (this.cps < ((Number) settings.get(MINAPS).getValue()).intValue()) {
            this.cps = MiscUtils.random(((Number) settings.get(MINAPS).getValue()).intValue(), ((Number) settings.get(MAXAPS).getValue()).intValue());
            this.random = MiscUtils.random(-25, 25);
        }
        if (mc.gameSettings.keyBindAttack.pressed && this.timer.delay((1000 / this.cps + this.random))) {
            this.timer.reset();
            this.cps = MiscUtils.random(((Number) settings.get(MINAPS).getValue()).intValue(), ((Number) settings.get(MAXAPS).getValue()).intValue());
            this.random = MiscUtils.random(-25, 25);
            mc.clickMouse();
        }
    }
}
