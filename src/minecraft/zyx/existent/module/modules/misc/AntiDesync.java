package zyx.existent.module.modules.misc;

import net.minecraft.network.play.server.SPacketPlayerPosLook;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacketReceive;
import zyx.existent.module.Category;
import zyx.existent.module.Module;

public class AntiDesync extends Module {
    public AntiDesync(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onReceive(EventPacketReceive event) {
        if (mc.thePlayer != null && event.getPacket() != null && event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            packet.setYaw(mc.thePlayer.rotationYaw);
            packet.setPitch(mc.thePlayer.rotationPitch);
        }
    }
}
