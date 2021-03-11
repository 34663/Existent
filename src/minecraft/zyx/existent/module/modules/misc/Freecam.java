package zyx.existent.module.modules.misc;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.*;
import zyx.existent.module.Category;
import zyx.existent.module.Module;

public class Freecam extends Module {
    private double x;
    private double y;
    private double minY;
    private double z;
    private float yaw;
    private float pitch;

    public Freecam(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer != null) {
            this.x = mc.thePlayer.posX;
            this.y = mc.thePlayer.posY;
            this.minY = mc.thePlayer.boundingBox.minY;
            this.z = mc.thePlayer.posZ;
            this.yaw = mc.thePlayer.rotationYaw;
            this.pitch = mc.thePlayer.rotationPitch;
            EntityOtherPlayerMP ent = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            ent.inventory = mc.thePlayer.inventory;
            ent.inventoryContainer = mc.thePlayer.inventoryContainer;
            ent.setPositionAndRotation(this.x, this.minY, this.z, this.yaw, this.pitch);
            ent.rotationYawHead = mc.thePlayer.rotationYawHead;
            mc.theWorld.addEntityToWorld(-1, ent);
            mc.thePlayer.noClip = true;
        }
        super.onEnable();
    }
    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.noClip = false;
            mc.theWorld.removeEntityFromWorld(-1);
            mc.thePlayer.setPositionAndRotation(this.x, this.y, this.z, this.yaw, this.pitch);
        }
        super.onDisable();
    }

    @EventTarget
    public void onMove(EventMove event) {
        double player = 4.0D;
        if (Keyboard.isKeyDown(29))
            player /= 2.0D;
        event.setY(0.0D);
        mc.thePlayer.motionY = 0.0D;
        if (!mc.inGameHasFocus)
            return;
        if (MovementInput.moveForward != 0.0F || MovementInput.moveStrafe != 0.0F) {
            event.setX(event.getX() * player);
            event.setZ(event.getZ() * player);
        }
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
            event.setY(player / 8.0D);
        } else if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
            event.setY(-(player / 8.0D));
        }
    }
    @EventTarget
    public void onUpdate(EventUpdate event) {
        mc.thePlayer.renderArmPitch += 400.0F;
    }
    @EventTarget
    public void onEntityUpdate(EventEntityUpdate event) {
        mc.thePlayer.noClip = true;
    }
    @EventTarget
    public void InsideBlock(EventInsideBlock event) {
        event.setCancelled(true);
    }
    @EventTarget
    public void PushOutBlock(EventPushOutBlock event) {
        event.setCancelled(true);
    }
    @EventTarget
    public void onPacketSent(EventPacketSend send) {
        if (send.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer player1 = (CPacketPlayer) send.getPacket();
            player1.x = this.x;
            player1.y = this.y;
            player1.z = this.z;
            player1.yaw = this.yaw;
            player1.pitch = this.pitch;
        }
    }
}
