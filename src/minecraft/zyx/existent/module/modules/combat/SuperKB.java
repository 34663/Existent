package zyx.existent.module.modules.combat;

import net.minecraft.network.play.client.CPacketPlayer;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventAttack;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Setting;

public class SuperKB extends Module {
    private final String PACKET = "PACKET";

    public SuperKB(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(PACKET, new Setting(PACKET, 5.0, "Packet Size", 0.1, 2.0, 10.0));
    }

    @EventTarget
    public void onAttack(EventAttack event) {
        if (mc.thePlayer.getDistanceToEntity(event.getEntity()) <= 1.0F || mc.thePlayer.getEntityBoundingBox().intersectsWith(event.getEntity().getEntityBoundingBox())) {
            int i = 0;
            while ((double) i < ((Number) settings.get(PACKET).getValue()).doubleValue() * 10.0D) {
                if (mc.thePlayer.onGround) {
                    mc.getConnection().sendPacket(new CPacketPlayer());
                }
                ++i;
            }
        }
    }
}
