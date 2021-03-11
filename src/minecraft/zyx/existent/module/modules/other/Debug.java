package zyx.existent.module.modules.other;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventAttack;
import zyx.existent.event.events.EventPacket;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.utils.ChatUtils;

public class Debug extends Module {
    public Debug(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            ChatUtils.printChat("\2479PacketDetect\2477: \247fVelocity");
            if (event.isCancelled()) {
                ChatUtils.printChat("\2479Canceled Packet");
            }
        } else if (event.getPacket() instanceof SPacketExplosion) {
            ChatUtils.printChat("\2479PacketDetect\2477: \247fExplosion");
            if (event.isCancelled()) {
                ChatUtils.printChat("\2479Canceled Packet");
            }
        }
    }
    @EventTarget
    public void onAttack(EventAttack event) {
        if (mc.thePlayer.getCooledAttackStrength(0) >= 1 && !mc.thePlayer.onGround) {
            ChatUtils.printChat("\2479CriticalAttack\2477: " + event.getEntity().getName());
        } else {
            ChatUtils.printChat("\2479Attack\2477: " + event.getEntity().getName());
        }
    }
}
