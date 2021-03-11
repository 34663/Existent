package zyx.existent.module.modules.player;

import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class SpeedMine extends Module {
    public static String HASTE = "HASTE";
    public static String WATER = "WATER";
    public static String GROUND = "GROUND";
    private String DAMAGE = "DAMAGEMP";
    private String DELAY = "DELAY";

    public SpeedMine(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(HASTE, new Setting(HASTE, true, ""));
        settings.put(WATER, new Setting(WATER, true, ""));
        settings.put(GROUND, new Setting(GROUND, true, ""));
        settings.put(DAMAGE, new Setting(DAMAGE, 0.7, "BlockDamageMP", 0.1, 0.1, 1.0));
    }

    @Override
    public void onDisable() {
        mc.playerController.blockHitDelay = 5;
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        if (eventUpdate.isPre()) {
            mc.playerController.blockHitDelay = 0;

            if (mc.playerController.curBlockDamageMP >= ((Number) settings.get(DAMAGE).getValue()).doubleValue()) {
                mc.playerController.curBlockDamageMP = 1.0F;
            }
        }
    }
}
