package zyx.existent.utils;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtils implements MCUtil {
    public static float getAngleDistance(Entity entity) {
        return Math.abs(normalizeAngle(getYaw(entity)));
    }

    /**
     * @return index 0 = yaw | index 1 = pitch
     */
    public static float[] getFacePosEntity(Entity en) {
        if (en == null) {
            return new float[]{mc.thePlayer.rotationYawHead, mc.thePlayer.rotationPitch};
        }
        return getFacePos(new Vec3d(en.posX - 0.5, en.posY + (en.getEyeHeight() - en.height / 1.5), en.posZ - 0.5));
    }

    public static float[] getFacePos(Vec3d vec) {
        double diffX = vec.xCoord + 0.5 - mc.thePlayer.posX;
        double diffY = vec.yCoord + 0.5 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double diffZ = vec.zCoord + 0.5 - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
        return new float[] {mc.thePlayer.rotationYaw + MathHelper.wrapDegrees(yaw - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + MathHelper.wrapDegrees(pitch - mc.thePlayer.rotationPitch) };
    }

    public final Vec3d getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((f1 * f2), f3, (f * f2));
    }

    public static float[] getIntaveRots(BlockPos bp, EnumFacing enumface) {
        double x = bp.getX() + 0.5D, y = bp.getY() + 0.5D, z = bp.getZ() + 0.5D;
        if (enumface != null)
            if (EnumFacing.UP != null) {
                y += 0.5D;
            } else if (EnumFacing.DOWN != null) {
                y -= 0.5D;
            } else if (EnumFacing.WEST != null) {
                x += 0.5D;
            } else if (EnumFacing.EAST != null) {
                x -= 0.5D;
            } else if (EnumFacing.NORTH != null) {
                z += 0.5D;
            } else if (EnumFacing.SOUTH != null) {
                z -= 0.5D;
            }
        double dX = x - mc.thePlayer.posX, dY = y - mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        double dZ = z - mc.thePlayer.posZ;
        float yaw = (float) (Math.atan2(dZ, dX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(dY, Math.sqrt(dX * dX + dZ * dZ)) * 180.0D / Math.PI);
        yaw = MathHelper.wrapDegrees(yaw);
        pitch = MathHelper.wrapDegrees(pitch);
        return new float[]{yaw, pitch};
    }

    public static float[] getBlockRotations(final double x, final double y, final double z) {
        final double var4 = x - mc.thePlayer.posX + 0.5;
        final double var5 = z - mc.thePlayer.posZ + 0.5;
        final double var6 = y - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - 1.0);
        final double var7 = MathHelper.sqrt(var4 * var4 + var5 * var5);
        final float var8 = (float)(Math.atan2(var5, var4) * 180.0 / 3.141592653589793) - 90.0f;
        return new float[] { var8, (float)(-(Math.atan2(var6, var7) * 180.0 / 3.141592653589793)) };
    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        EntityPlayerSP player = mc.thePlayer;
        double x = posX - player.posX;
        double y = posY - player.posY + player.getEyeHeight();
        double z = posZ - player.posZ;
        double dist = MathHelper.sqrt(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float)-(Math.atan2(y, dist) * 180.0D / Math.PI);
        return new float[] { yaw, pitch };
    }

    public static float[] getRotationsEntity(EntityLivingBase entity) {
        return getRotations(entity.posX + MathUtils.randomNumber(0.03D, -0.03D), entity.posY + entity.getEyeHeight() - 0.4D + MathUtils.randomNumber(0.07D, -0.07D), entity.posZ + MathUtils.randomNumber(0.03D, -0.03D));
    }

    public static float[] getRotations(Entity entity, double maxRange) {
        if (entity == null) {
            return null;
        }
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        Location BestPos = new Location(entity.posX, entity.posY, entity.posZ);
        Location myEyePos = new Location(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        for (double i = entity.boundingBox.minY + 0.7; i < entity.boundingBox.maxY - 0.1; i += 0.1) {
            if (myEyePos.distanceTo(new Location(entity.posX, i, entity.posZ)) < myEyePos.distanceTo(BestPos)) {
                BestPos = new Location(entity.posX, i, entity.posZ);
            }
        }

        if (myEyePos.distanceTo(BestPos) > maxRange) {
            return null;
        }
        double diffY = BestPos.getY() - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / 3.141592653589793));
        return new float[]{yaw, pitch};
    }

    public static float[] smoothRotation(float[] currentRotations, float[] neededRotations, float rotationSpeed) {
        final float yawDiff = getDifference(neededRotations[0], currentRotations[0]);
        final float pitchDiff = getDifference(neededRotations[1], currentRotations[1]);

        float rotationSpeedYaw = rotationSpeed;

        if (yawDiff > rotationSpeed) {
            rotationSpeedYaw = rotationSpeed;
        } else {
            rotationSpeedYaw = Math.max(yawDiff, -rotationSpeed);
        }

        float rotationSpeedPitch = rotationSpeed;

        if (pitchDiff > rotationSpeed) {
            rotationSpeedPitch = rotationSpeed;
        } else {
            rotationSpeedPitch = Math.max(pitchDiff, -rotationSpeed);
        }

        final float newYaw = currentRotations[0] + rotationSpeedYaw;
        final float newPitch = currentRotations[1] + rotationSpeedPitch;

        return new float[] { newYaw, newPitch };
    }

    public static float[] getRotations(Entity entity, boolean predict, double predictionFactor) {
        final Vec3d playerPos = new Vec3d(mc.thePlayer.posX + (predict ? mc.thePlayer.motionX * predictionFactor : 0), mc.thePlayer.posY + (entity instanceof EntityLivingBase ? mc.thePlayer.getEyeHeight() : 0) + (predict ? mc.thePlayer.motionY * predictionFactor : 0), mc.thePlayer.posZ + (predict ? mc.thePlayer.motionZ * predictionFactor : 0));
        final Vec3d entityPos = new Vec3d(entity.posX + (predict ? (entity.posX - entity.prevPosX) * predictionFactor : 0), entity.posY + (predict ? (entity.posY - entity.prevPosY) * predictionFactor : 0), entity.posZ + (predict ? (entity.posZ - entity.prevPosZ) * predictionFactor : 0));

        final double diffX = entityPos.xCoord - playerPos.xCoord;
        final double diffY = (entity instanceof EntityLivingBase ? entityPos.yCoord + ((EntityLivingBase) entity).getEyeHeight() - playerPos.yCoord : entityPos.yCoord - playerPos.yCoord);
        final double diffZ = entityPos.zCoord - playerPos.zCoord;

        final double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        final double yaw = Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0;
        final double pitch = -Math.toDegrees(Math.atan2(diffY, dist));

        return new float[] {(float) yaw, (float) pitch};
    }

    public static float[] getRotations(EntityLivingBase ent) {
        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.posY + ent.getEyeHeight() / 2.0F;
        return getRotationFromPosition(x, z, y);
    }

    public static float[] getAngles(Entity e) {
        return new float[]{getYawChangeToEntity(e) + mc.thePlayer.rotationYaw, getPitchChangeToEntity(e) + mc.thePlayer.rotationPitch};
    }

    public static float getYaw(Entity var0) {
        double var5, var1 = var0.posX - mc.thePlayer.posX;
        double var3 = var0.posZ - mc.thePlayer.posZ;
        if (var3 < 0.0D && var1 < 0.0D) {
            var5 = 90.0D + Math.toDegrees(Math.atan(var3 / var1));
        } else if (var3 < 0.0D && var1 > 0.0D) {
            var5 = -90.0D + Math.toDegrees(Math.atan(var3 / var1));
        } else {
            var5 = Math.toDegrees(-Math.atan(var1 / var3));
        }
        return MathHelper.wrapDegrees(-(mc.thePlayer.rotationYaw - (float) var5));
    }

    public static float getPitchChangeToEntity(Entity entity) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double deltaY = entity.posY - 1.6D + entity.getEyeHeight() - 0.4D - mc.thePlayer.posY;
        double distanceXZ = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapDegrees(mc.thePlayer.rotationPitch - (float) pitchToEntity);
    }

    public static float getYawChangeToEntity(Entity entity) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double yawToEntity;
        if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
            yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
                yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
            } else
                yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }
        return MathHelper.wrapDegrees(-(mc.thePlayer.rotationYaw - (float) yawToEntity));
    }

    public static float normalizeAngle(float angle) {
        return MathHelper.wrapDegrees((angle + 180.0F) % 360.0F - 180.0F);
    }

    public static boolean canEntityBeSeen(Entity e) {
        Vec3d vec1 = new Vec3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        AxisAlignedBB box = e.getEntityBoundingBox();
        Vec3d vec2 = new Vec3d(e.posX, e.posY + (e.getEyeHeight() / 1.32F), e.posZ);
        double minx = e.posX - 0.25;
        double maxx = e.posX + 0.25;
        double miny = e.posY;
        double maxy = e.posY + Math.abs(e.posY - box.maxY);
        double minz = e.posZ - 0.25;
        double maxz = e.posZ + 0.25;
        boolean see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3d(maxx, miny, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3d(minx, miny, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;

        if (see)
            return true;
        vec2 = new Vec3d(minx, miny, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3d(maxx, miny, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;

        vec2 = new Vec3d(maxx, maxy, minz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;

        if (see)
            return true;
        vec2 = new Vec3d(minx, maxy, minz);

        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3d(minx, maxy, maxz - 0.1);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3d(maxx, maxy, maxz);
        see = mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        return false;
    }

    public static float[] getNeededFacing(final Vec3d target, final Vec3d from) {
        final double diffX = target.xCoord - from.xCoord;
        final double diffY = target.yCoord - from.yCoord;
        final double diffZ = target.zCoord - from.zCoord;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[] { MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch) };
    }

    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - mc.thePlayer.posX;
        double zDiff = z - mc.thePlayer.posZ;
        double yDiff = y - mc.thePlayer.posY - 1.2;

        double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    public static float getDifference(float a, float b) {
        float r = (float) ((a - b) % 360.0);

        if (r < -180.0) {
            r += 360.0;
        }

        if (r >= 180.0) {
            r -= 360.0;
        }

        return r;
    }
}
