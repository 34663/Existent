package zyx.existent.module.modules.movement;

import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventMove;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.utils.MoveUtils;

public class WaterSpeed extends Module {
    public static double waterSpeed;

    public WaterSpeed(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onMove(EventMove event) {
        if (mc.thePlayer.isInWater()) {
            event.setY(mc.thePlayer.motionY = 0.42F);
            waterSpeed = 0.5D;
            MoveUtils.setMotion(event, waterSpeed);
        } else {
            if (waterSpeed > 0.3) {
                MoveUtils.setMotion(event, waterSpeed *= 0.99);
            }
        }
    }
}
