package zyx.existent.module.modules.movement;

import net.minecraft.network.play.client.CPacketEntityAction;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.module.Category;
import zyx.existent.module.Module;

public class KeepSprint extends Module {
    public KeepSprint(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        try {
            if (e.isIncoming() && e.getPacket() instanceof CPacketEntityAction) {
                CPacketEntityAction packet = (CPacketEntityAction) e.getPacket();
                if (packet.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
                    e.setCancelled(true);
                }
            }
        } catch (ClassCastException ignored) {
            ;
        }
    }
}
