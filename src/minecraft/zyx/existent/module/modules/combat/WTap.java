package zyx.existent.module.modules.combat;

import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacketReceive;
import zyx.existent.module.Category;
import zyx.existent.module.Module;

public class WTap extends Module {
    public WTap(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onPacketReceive(EventPacketReceive packetReceive) {
        if (mc.theWorld != null && mc.thePlayer != null && packetReceive.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) packetReceive.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld(mc.theWorld) != mc.thePlayer && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
                mc.thePlayer.setSprinting(false);
                mc.getConnection().sendPacket(new CPacketEntityAction(mc.thePlayer, CPacketEntityAction.Action.STOP_SPRINTING));
                mc.thePlayer.setSprinting(true);
                mc.getConnection().sendPacket(new CPacketEntityAction(mc.thePlayer, CPacketEntityAction.Action.START_SPRINTING));
            }
        }
    }
}
