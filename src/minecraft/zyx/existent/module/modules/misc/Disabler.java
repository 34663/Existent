package zyx.existent.module.modules.misc;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import zyx.existent.Existent;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventPacket;
import zyx.existent.event.events.EventPacketSend;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.gui.notification.NotificationPublisher;
import zyx.existent.gui.notification.NotificationType;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.module.modules.movement.Flight;
import zyx.existent.module.modules.movement.LongJump;
import zyx.existent.module.modules.movement.Speed;
import zyx.existent.utils.MathUtils;
import zyx.existent.utils.timer.Timer;

import java.util.Random;

public class Disabler extends Module {
    private String MODE = "MODE";
    private Timer timer = new Timer();
    private boolean erisium = false;
    private int i;
    int lastKey;

    public Disabler(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Mode", "Hypixel", new String[]{"Hypixel", "Faithful", "Erisium"}), "Aura method"));
    }

    @Override
    public void onEnable() {
        if (mc.theWorld == null)
            return;
        NotificationPublisher.queue("Disabler", "Please relog for the disabler to take affect.", NotificationType.INFO);
        erisium = false;
        timer.reset();
        this.i = 0;
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        switch (((Options) settings.get(MODE).getValue()).getSelected()) {
            case "Hypixel":
                if (Existent.getModuleManager().isEnabled(Flight.class) || Existent.getModuleManager().isEnabled(Speed.class) || Existent.getModuleManager().isEnabled(LongJump.class) || mc.thePlayer.isSpectator()) {
                    PlayerCapabilities playerCapabilities = new PlayerCapabilities();
                    playerCapabilities.isFlying = true;
                    playerCapabilities.allowFlying = true;
                    playerCapabilities.setFlySpeed((float) MathUtils.randomNumber(0.1D, 9.0D));
                    mc.getConnection().sendPacket(new CPacketPlayerAbilities(playerCapabilities));
                }
                break;
            case "Erisium":
                if (erisium && timer.delay(1500)) {
                    erisium = !erisium;
                    mc.thePlayer.connection.getNetworkManager().sendPacketNoEvent(new CPacketKeepAlive(lastKey));
                }
                break;
        }
    }
    @EventTarget
    public void onPacketSend(EventPacketSend event) {
        switch (((Options) settings.get(MODE).getValue()).getSelected()) {
            case "Hypixel":
                if (event.getPacket() instanceof CPacketConfirmTransaction) {
                    CPacketConfirmTransaction packetConfirmTransaction = (CPacketConfirmTransaction) event.getPacket();
                    mc.getConnection().sendPacket(new CPacketConfirmTransaction(2147483647, packetConfirmTransaction.getUid(), false));
                    event.setCancelled(true);
                }
                if (event.getPacket() instanceof CPacketKeepAlive) {
                    mc.getConnection().sendPacket(new CPacketKeepAlive(Integer.MIN_VALUE + (new Random()).nextInt(100)));
                    event.setCancelled(true);
                }
                break;
            case "Faithful":
                if (mc.thePlayer != null && mc.thePlayer.getDistance(mc.thePlayer.prevPosX, mc.thePlayer.prevPosY, mc.thePlayer.prevPosZ) > 0.6D && event.getPacket() instanceof CPacketPlayer) {
                    if (this.i > 2) {
                        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) / 2.0D, mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) / 2.0D, mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) / 2.0D, true));
                        this.i = 0;
                    } else {
                        event.setCancelled(true);
                        mc.getConnection().sendPacket(new CPacketKeepAlive(-2147483648));
                    }
                    this.i++;
                }
                break;
        }
    }
    @EventTarget
    public void onPacket(EventPacket event) {
        Packet p = event.getPacket();

        if (Existent.getModuleManager().isEnabled(Disabler.class)) {
            if (event.getPacket() instanceof SPacketJoinGame)
                toggle();
            if (event.getPacket() instanceof SPacketDisconnect)
                toggle();
        }
        switch (((Options) settings.get(MODE).getValue()).getSelected()) {
            case "Erisium":
                if (p instanceof SPacketKeepAlive) {
                    SPacketKeepAlive packet = (SPacketKeepAlive) p;
                    int KEY = (int) packet.getId();
                    timer.reset();
                    erisium = true;
                    lastKey = KEY;
                }
                if (p instanceof CPacketKeepAlive) {
                    CPacketKeepAlive packet = (CPacketKeepAlive) p;
                    event.setCancelled(true);
                }
        }
    }
}
