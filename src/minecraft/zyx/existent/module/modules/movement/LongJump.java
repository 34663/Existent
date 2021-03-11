package zyx.existent.module.modules.movement;

import net.minecraft.network.play.client.CPacketPlayer;
import zyx.existent.event.EventTarget;
import zyx.existent.event.events.EventMove;
import zyx.existent.event.events.EventUpdate;
import zyx.existent.module.Category;
import zyx.existent.module.Module;
import zyx.existent.module.data.Options;
import zyx.existent.module.data.Setting;
import zyx.existent.utils.MoveUtils;
import zyx.existent.utils.PlayerUtils;

public class LongJump extends Module {
    private final String MODE = "MODE";
    private final String DISABLE = "AUTODISABLE";

    private double lastDif;
    private double moveSpeed;
    private int stage;
    private int groundTicks;
    private float air;

    public LongJump(String name, String desc, int keybind, Category category) {
        super(name, desc, keybind, category);

        settings.put(MODE,new Setting(MODE,new Options("Mode", "NCP", new String[] {"NCP", "AACDev"}),"LongJump method."));
        settings.put(DISABLE, new Setting(DISABLE, true, "AutoDisable"));
    }

    @Override
    public void onEnable() {
        this.lastDif = 0.0D;
        this.moveSpeed = 0.0D;
        this.stage = 0;
        this.groundTicks = 1;
        super.onEnable();
    }
    @Override
    public void onDisable() {
        mc.thePlayer.jumpMovementFactor = 0.0F;
        mc.timer.timerSpeed = 1.0F;
        super.onDisable();
    }

    @EventTarget
    public void onMove(EventMove eventMove) {
        String currentmode = ((Options) settings.get(MODE).getValue()).getSelected();

        switch (currentmode) {
            case "NCP":
                if (mc.thePlayer.isMoving()) {
                    switch (this.stage) {
                        case 0:
                        case 1:
                            this.moveSpeed = 0.0D;
                            break;
                        case 2:
                            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
                                eventMove.setY(mc.thePlayer.motionY = MoveUtils.getJumpBoostModifier(0.3999999463558197D));
                                this.moveSpeed = MoveUtils.getBaseMoveSpeed() * 2.0D;
                            }
                            break;
                        case 3:
                            this.moveSpeed = MoveUtils.getBaseMoveSpeed() * 2.1489999294281006D;
                            break;
                        default:
                            if (mc.thePlayer.motionY < 0.0D)
                                mc.thePlayer.motionY *= 0.5D;
                            this.moveSpeed = this.lastDif - this.lastDif / 159.0D;
                            break;
                    }
                    this.moveSpeed = Math.max(this.moveSpeed, MoveUtils.getBaseMoveSpeed());
                    this.stage++;
                }
                MoveUtils.setMotion(eventMove, this.moveSpeed);
                break;
            case "AACDev":
                if (mc.thePlayer.onGround) {
                    mc.timer.timerSpeed = 1F;
                    mc.thePlayer.jump();
                    mc.thePlayer.motionY = 0.5;
                    mc.thePlayer.speedInAir = 0.02F;
                    mc.thePlayer.jumpMovementFactor = 0.02F;
                } else {
                    mc.thePlayer.jumpMovementFactor = 0.48F;
                    this.move(mc.thePlayer.rotationYaw, (float) 0.2);
                    mc.timer.timerSpeed = 0.6F;
                }
                break;
        }
    }
    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        if (eventUpdate.isPre()) {
            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
                eventUpdate.setY(eventUpdate.getY() + 7.435E-4D);
            }
            double xDif = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDif = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            this.lastDif = Math.sqrt(xDif * xDif + zDif * zDif);
            if (mc.thePlayer.isMoving() && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && this.stage > 2)
                this.groundTicks++;
            if (this.groundTicks > 1)
                if ((Boolean) settings.get(DISABLE).getValue())
                    toggle();
        }
    }

    public void move(float yaw, float multiplyer) {
        double moveX = -Math.sin(Math.toRadians((double) yaw)) * (double) multiplyer;
        double moveZ = Math.cos(Math.toRadians((double) yaw)) * (double) multiplyer;
        this.mc.thePlayer.motionX = moveX;
        this.mc.thePlayer.motionZ = moveZ;
    }
}
