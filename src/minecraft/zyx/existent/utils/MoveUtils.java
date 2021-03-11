package zyx.existent.utils;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import zyx.existent.Existent;
import zyx.existent.event.events.EventMove;
import zyx.existent.module.Module;
import zyx.existent.module.modules.combat.KillAura;
import zyx.existent.module.modules.movement.TargetStrafe;

public class MoveUtils implements MCUtil {
    public static boolean status;
    private static int cunt;

    public static float getSpeed() {
        return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static int getSpeedEffect() {
        if (mc.thePlayer.isPotionActive(Potion.getPotionById(1)))
            return mc.thePlayer.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier() + 1;
        else
            return 0;
    }

    public static double getJumpBoostModifier(double baseJumpHeight) {
        if (mc.thePlayer.isPotionActive(Potion.getPotionById(8))) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.getPotionById(8)).getAmplifier();
            baseJumpHeight += ((amplifier + 1) * 0.1F);
        }
        return baseJumpHeight;
    }

    public static int getJumpEffect() {
        if (mc.thePlayer.isPotionActive(Potion.getPotionById(8)))
            return mc.thePlayer.getActivePotionEffect(Potion.getPotionById(8)).getAmplifier() + 1;
        else
            return 0;
    }

    public static boolean isOnGround2(final double height) {
        return !mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
    }

    public static boolean isOnGround(double height) {
        return mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, height, 0.0D)).isEmpty();
    }

    public static boolean isOnGround(final double motionX, final double motionZ) {
        return mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(motionX, 0.001, motionZ)).isEmpty();
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.getPotionById(1))) {
            final int amplifier = mc.thePlayer.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public static void forward(double length, double y) {
        final double yaw = getYaw();
        mc.thePlayer.setPosition(mc.thePlayer.posX + (-Math.sin(yaw) * length), mc.thePlayer.posY + y, mc.thePlayer.posZ + (Math.cos(yaw) * length));
    }

    public static void setSpeed(EventMove moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;
        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45 : -45);
            }
            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }
        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        moveEvent.setX(forward * moveSpeed * mx + strafe * moveSpeed * mz);
        moveEvent.setZ(forward * moveSpeed * mz - strafe * moveSpeed * mx);
    }

    public static void setMotion(EventMove event, double speed) {
        double forward = MovementInput.moveForward;
        double strafe = MovementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if (forward == 0.0D && strafe == 0.0D) {
            event.setX(0.0D);
            event.setZ(0.0D);
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            event.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
            event.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
        }
    }

    public static void setMotion(double speed) {
        double forward = MovementInput.moveForward;
        double strafe = MovementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
            mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }

    public static double getYaw() {
        float rotationYaw = mc.thePlayer.rotationYaw;
        float forward = 1F;

        final double moveForward = MovementInput.moveForward;
        final double moveStrafing = MovementInput.moveStrafe;
        final float yaw = mc.thePlayer.rotationYaw;

        if (moveForward < 0) {
            rotationYaw += 180F;
        }

        if (moveForward < 0) {
            forward = -0.5F;
        } else if (moveForward > 0) {
            forward = 0.5F;
        }

        if (moveStrafing > 0) {
            rotationYaw -= 90F * forward;
        } else if (moveStrafing < 0) {
            rotationYaw += 90F * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    public static boolean isCollidedH(double dist) {
        AxisAlignedBB bb = new AxisAlignedBB(mc.thePlayer.posX - 0.3, mc.thePlayer.posY + 2, mc.thePlayer.posZ + 0.3, mc.thePlayer.posX + 0.3, mc.thePlayer.posY + 3, mc.thePlayer.posZ - 0.3);
        if (!mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, bb.offset(0.3 + dist, 0, 0)).isEmpty()) {
            return true;
        } else if (!mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, bb.offset(-0.3 - dist, 0, 0)).isEmpty()) {
            return true;
        } else if (!mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, bb.offset(0, 0, 0.3 + dist)).isEmpty()) {
            return true;
        } else if (!mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, bb.offset(0, 0, -0.3 - dist)).isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean c() {
        for (int i = (int) Math.ceil(mc.thePlayer.posY); i >= 0; --i) {
            if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() != Blocks.AIR) {
                return false;
            }
        }
        return true;
    }

    public static float a(final double n, final double n2) {
        return (float) (Math.atan2(n2 - mc.thePlayer.posZ, n - mc.thePlayer.posX) * 180.0 / 3.141592653589793) - 90.0f;
    }

    public static Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
        return mc.theWorld.getBlockState(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ)).getBlock();
    }
}
