package zyx.existent.module.modules.movement;

import net.minecraft.network.play.client.CPacketEntityAction;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;

public class Sneak extends Module {
    public Sneak(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            mc.getConnection().sendPacket(new CPacketEntityAction(mc.thePlayer, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!mc.thePlayer.isSneaking() || !mc.gameSettings.keyBindSneak.pressed) {
            if (event.isPre()) {
                mc.getConnection().sendPacket(new CPacketEntityAction(mc.thePlayer, CPacketEntityAction.Action.STOP_SNEAKING));
            } else if (event.isPost()) {
                mc.getConnection().sendPacket(new CPacketEntityAction(mc.thePlayer, CPacketEntityAction.Action.START_SNEAKING));
            }
        }
    }
}
