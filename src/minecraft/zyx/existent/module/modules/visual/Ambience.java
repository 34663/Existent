package zyx.existent.module.modules.visual;

import net.minecraft.network.play.server.SPacketTimeUpdate;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class Ambience extends Module {
    private String TIME = "TIME";

    public Ambience(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(TIME, new Setting(TIME, 11000.0, "World Time.", 0.1, 1.0, 16000.0));
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof SPacketTimeUpdate)
            e.setCancelled(true);
    }
    @EventTarget
    public void onUpdate(EventUpdate event) {
        long time = ((Number) settings.get(TIME).getValue()).longValue();
        mc.theWorld.setWorldTime(time);
    }
}
