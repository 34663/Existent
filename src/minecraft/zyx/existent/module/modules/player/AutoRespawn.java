package zyx.existent.module.modules.player;

import net.minecraft.network.play.client.CPacketClientStatus;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;

public class AutoRespawn extends Module {
    public AutoRespawn(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (this.mc.thePlayer.isDead || this.mc.thePlayer.getHealth() <= 0.0F) {
            this.mc.thePlayer.respawnPlayer();
            mc.getConnection().sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
        }
    }
}
