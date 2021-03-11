package zyx.existent.module.modules.other;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.server.SPacketRespawn;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.event.events.EventPacketReceive;
import zyx.existent.event.events.EventTick;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.utils.ChatUtils;

public class TEST extends Module {
    public TEST(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
//        for (EntityPlayer entity : mc.theWorld.playerEntities) {
//            if (entity != null && !(entity == mc.thePlayer)) {
//                ChatUtils.printChat(entity.getName() + " : " + entity.lastTickPosY);
//            }
//        }
    }
    @EventTarget
    public void onPacket(EventPacket event) {
//        if (event.getPacket() instanceof SPacketRespawn) {
//            ChatUtils.printChat("respawn: dead");
//        }
//        if (event.getPacket() instanceof CPacketClientStatus) {
//            ChatUtils.printChat("status: dead");
//        }
    }
}
