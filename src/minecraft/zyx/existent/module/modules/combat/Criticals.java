package zyx.existent.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import zyx.existent.Existent;
import zyx.existent.event.Event;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventAttack;
import zyx.existent.event.events.EventPacket;
import zyx.existent.event.events.EventStep;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.module.modules.movement.Flight;
import zyx.existent.module.modules.movement.LongJump;
import zyx.existent.utils.ChatUtils;
import zyx.existent.utils.MoveUtils;
import zyx.existent.utils.timer.Timer;

public class Criticals extends Module {
    public final double[] hypixelOffsets = new double[] { 0.05000000074505806D, 0.0015999999595806003D, 0.029999999329447746D, 0.0015999999595806003D };
    Timer lastStep = new Timer();
    Timer timer = new Timer();
    int groundTicks, stage, count;

    public static String MODE = "MODE";
    public static String HURTTIME = "HURTTIME";

    public Criticals(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE, new Setting(MODE, new Options("Mode", "Packet", new String[]{"Hypixel", "Visual", "AAC4", "Spartan", "Horizon", "NCP", "Packet2", "Packet"}), "Critical method"));
        settings.put(HURTTIME, new Setting(HURTTIME, 15, "The hurtTime tick to crit at.", 1, 0, 20));
    }

    @Override
    public void onEnable() {
        stage = 0;
        count = 0;
        super.onEnable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        String currentMode = ((Options) settings.get(MODE).getValue()).getSelected();
        double hurttime = ((Number) settings.get(HURTTIME).getValue()).doubleValue();
        setSuffix(currentMode + " " + hurttime);

        if (MoveUtils.isOnGround(0.001)) {
            groundTicks++;
        } else if (!mc.thePlayer.onGround) {
            groundTicks = 0;
        }
    }
    @EventTarget
    public void onPacket(EventPacket event) {
        String currentMode = ((Options) settings.get(MODE).getValue()).getSelected();
        Packet<?> packet = event.getPacket();

        if (currentMode.equalsIgnoreCase("Packet2")) {
            if (packet instanceof CPacketUseEntity && ((CPacketUseEntity) packet).getAction() == CPacketUseEntity.Action.ATTACK) {
                Entity entity = ((CPacketUseEntity) packet).getEntityFromWorld(mc.theWorld);
                if (!mc.thePlayer.onGround) {
                    return;
                }
                if(entity instanceof EntityLivingBase) {
                    if (((EntityLivingBase) entity).hurtTime != 0) {
                        return;
                    }
                }
                double x = mc.thePlayer.posX;
                double y = mc.thePlayer.posY;
                double z = mc.thePlayer.posZ;
                mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(x, y + 0.0625D, z, true));
                mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(x, y, z, false));
                mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(x, y + 1.1E-5D, z, false));
                mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(x, y, z, false));
                mc.thePlayer.onCriticalHit(entity);
            }
        }

        if (packet instanceof SPacketPlayerPosLook) {
            stage = 0;
        }
        if (packet instanceof CPacketConfirmTransaction) {
            CPacketConfirmTransaction confirmTransaction = (CPacketConfirmTransaction) packet;
            boolean accepted = confirmTransaction.isAccepted();
            int uid = confirmTransaction.getUid();
            if (accepted && uid == 0) {
                count++;
            }
        }
    }
    @EventTarget
    public void onAttack(EventAttack attack) {
        String currentMode = ((Options) settings.get(MODE).getValue()).getSelected();
        if (attack.getEntity() instanceof EntityLivingBase) {
            Entity entity = attack.getEntity();

            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;

            if (entity.hurtResistantTime <= ((Number) settings.get(HURTTIME).getValue()).doubleValue()) {
                switch (currentMode) {
                    case "Packet":
                        if (mc.thePlayer.onGround) {
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y + 0.0625, z, false));
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(x, y, z, false));
                            mc.thePlayer.onCriticalHit(entity);
                        }
                        break;
                    case "Hypixel":
                        if (mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround && !(Existent.getModuleManager().isEnabled(LongJump.class) || Existent.getModuleManager().isEnabled(Flight.class)) && timer.delay(200L)) {
                            for (double offset : hypixelOffsets) {
                                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                                mc.thePlayer.onCriticalHit(entity);
                                timer.reset();
                            }
                        }
                        break;
                    case "NCP":
                        if (timer.delay(500) && mc.thePlayer.onGround) {
                            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + 0.11, mc.thePlayer.posZ, true));
                            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                            mc.thePlayer.onCriticalHit(entity);
                            timer.reset();
                        }
                        break;
                    case "Horizon":
                        if (mc.thePlayer.motionX == 0.0 && mc.thePlayer.motionZ == 0.0) {
                            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + 0.0001, mc.thePlayer.posZ, true));
                            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                            mc.thePlayer.onCriticalHit(entity);
                        }
                        break;
                    case "Spartan":
                        if (timer.delay(600)) {
                            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + 0.2, mc.thePlayer.posZ, true));
                            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                            mc.thePlayer.onCriticalHit(entity);
                        }
                        break;
                    case "AAC4":
                        mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + 0.0031311231111, mc.thePlayer.posZ, false));
                        mc.thePlayer.onCriticalHit(entity);
                        break;
                    case "Visual":
                        mc.thePlayer.onCriticalHit(entity);
                        break;
                }
            }
        }
    }
    @EventTarget
    public void onStep(EventStep event) {
        if (!event.isPre()) {
            lastStep.reset();
            if (!mc.thePlayer.isCollidedHorizontally) {
                stage = 0;
            }
        }
    }
}
