package zyx.existent.module.modules.movement;

import net.minecraft.potion.Potion;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventStep;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class Sprint extends Module {
    private String OMNIDIR = "OMNIDIR";

    public Sprint(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(OMNIDIR, new Setting(OMNIDIR, true, "VoidCheck."));
    }

    @EventTarget
    public void onUpdate(EventUpdate em) {
        if (em.isPre() && canSprint() && !mc.thePlayer.isCollidedHorizontally && !(mc.thePlayer.isPotionActive(Potion.getPotionById(2)) && mc.thePlayer.getActivePotionEffect(Potion.getPotionById(2)).getDuration() < 10000)) {
            mc.thePlayer.setSprinting(true);
        }
    }

    private boolean canSprint() {
        if (!(Boolean) settings.get(OMNIDIR).getValue() && !mc.gameSettings.keyBindForward.isKeyDown())
            return false;
        return mc.thePlayer.isMoving() && mc.thePlayer.getFoodStats().getFoodLevel() > 6;
    }
}
