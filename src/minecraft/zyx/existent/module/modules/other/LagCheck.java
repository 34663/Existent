package zyx.existent.module.modules.other;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.gui.notification.NotificationPublisher;
import zyx.existent.gui.notification.NotificationType;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.modules.movement.Flight;
import zyx.existent.module.modules.movement.Glide;
import zyx.existent.module.modules.movement.LongJump;
import zyx.existent.module.modules.movement.Speed;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.timer.Timer;

public class LagCheck extends Module {
    private final Timer lastCheck = new Timer();

    public LagCheck(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        Packet<?> p = event.getPacket();
        if (event.isIncoming()) {
            if (p instanceof SPacketPlayerPosLook && mc.thePlayer != null) {
                mc.thePlayer.onGround = false;
                mc.thePlayer.motionX *= 0;
                mc.thePlayer.motionZ *= 0;
                mc.thePlayer.jumpMovementFactor = 0;
                if (Existent.getModuleManager().isEnabled(Speed.class)) {
                    Existent.getModuleManager().getClazz(Speed.class).toggle();
                    NotificationPublisher.queue("LagBack", "Disabling Speed due to lagback", NotificationType.WARNING);
                }
                if (Existent.getModuleManager().isEnabled(LongJump.class)) {
                    Existent.getModuleManager().getClazz(LongJump.class).toggle();
                    NotificationPublisher.queue("LagBack", "Disabling LongJump due to lagback", NotificationType.WARNING);
                }
                if (Existent.getModuleManager().isEnabled(Flight.class)) {
                    Existent.getModuleManager().getClazz(Flight.class).toggle();
                    NotificationPublisher.queue("LagBack", "Disabling Flight due to lagback", NotificationType.WARNING);
                }
                if (Existent.getModuleManager().isEnabled(Glide.class)) {
                    Existent.getModuleManager().getClazz(Glide.class).toggle();
                    NotificationPublisher.queue("LagBack", "Disabling Glide due to lagback", NotificationType.WARNING);
                }
            }
        }
    }
}
