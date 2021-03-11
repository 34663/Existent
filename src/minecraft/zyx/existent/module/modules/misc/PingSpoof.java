package zyx.existent.module.modules.misc;

import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.server.SPacketKeepAlive;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacketReceive;
import zyx.existent.module.Category;
import zyx.existent.module.Module;

public class PingSpoof extends Module {
    public PingSpoof(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onPacketReceive(EventPacketReceive event) {
        if (event.getPacket() instanceof SPacketKeepAlive) {
            event.setCancelled(true);
            mc.getConnection().sendPacket(new CPacketKeepAlive());
        }
    }
}
