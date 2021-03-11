package zyx.existent.utils;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class PlayerUtils implements MCUtil {

    public static EntityLivingBase getClosestEntityToEntity(float range, Entity ent) {
        EntityLivingBase closestEntity = null;
        float mindistance = range;
        for (Object o :mc.theWorld.loadedEntityList) {
            if (isNotItem(o) && !ent.isEntityEqual((EntityLivingBase) o)) {
                EntityLivingBase en = (EntityLivingBase) o;
                if (ent.getDistanceToEntity(en) < mindistance) {
                    mindistance = ent.getDistanceToEntity(en);
                    closestEntity = en;
                }
            }
        }
        return closestEntity;
    }

    public static float[] getFacePosEntityRemote(EntityLivingBase facing, Entity en) {
        if (en == null) {
            return new float[] { facing.rotationYawHead, facing.rotationPitch };
        }
        return getFacePosRemote(new Vec3d(facing.posX, facing.posY + en.getEyeHeight(), facing.posZ), new Vec3d(en.posX, en.posY + en.getEyeHeight(), en.posZ));
    }

    public static float[] getFacePosRemote(Vec3d src, Vec3d dest) {
        double diffX = dest.xCoord - src.xCoord;
        double diffY = dest.yCoord - (src.yCoord);
        double diffZ = dest.zCoord - src.zCoord;
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
        return new float[]{MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch)};
    }

    public static boolean isNotItem(Object o) {
        return o instanceof EntityLivingBase;
    }

    public static void damageHypixel() {
        if (mc.thePlayer.onGround) {
            final double offset = 0.4122222218322211111111F;
            final NetHandlerPlayClient netHandler = mc.getConnection();
            final EntityPlayerSP player = mc.thePlayer;
            final double x = player.posX;
            final double y = player.posY;
            final double z = player.posZ;
            for (int i = 0; i < 9; i++) {
                netHandler.sendPacketSilent(new CPacketPlayer.Position(x, y + offset, z, false));
                netHandler.sendPacketSilent(new CPacketPlayer.Position(x, y + 0.000002737272, z, false));
                netHandler.sendPacketSilent(new CPacketPlayer(false));
            }
            netHandler.sendPacketSilent(new CPacketPlayer(true));
        }
    }

    public static void damage() {
        NetHandlerPlayClient netHandler = mc.getConnection();
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;
        for (int i = 0; i < getMaxFallDist() / 0.05510000046342611D + 1.0D; i++) {
            netHandler.sendPacketSilent(new CPacketPlayer.Position(x, y + 0.060100000351667404D, z, false));
            netHandler.sendPacketSilent(new CPacketPlayer.Position(x, y + 5.000000237487257E-4D, z, false));
            netHandler.sendPacketSilent(new CPacketPlayer.Position(x, y + 0.004999999888241291D + 6.01000003516674E-8D, z, false));
        }
        netHandler.sendPacketSilent(new CPacketPlayer(true));
    }

    public static void damage2() {
        for (int i = 0; i < 48; i++) {
            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625D, mc.thePlayer.posZ, false));
            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            if (i % 3 == 0)
                mc.thePlayer.connection.sendPacket(new CPacketKeepAlive(System.currentTimeMillis()));
        }
        mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-6D, mc.thePlayer.posZ, false));
        mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
    }

    public static void damage3() {
        for (int i = 0; (double) i < 29.2D; ++i) {
            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY + 0.0525D, mc.thePlayer.posZ, false));
            mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY - 0.0525D, mc.thePlayer.posZ, false));
        }
        mc.thePlayer.connection.sendPacket(new CPacketPlayer.Position(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
    }

    public static float getMaxFallDist() {
        PotionEffect potioneffect = mc.thePlayer.getActivePotionEffect(Potion.getPotionById(8));
        int f = (potioneffect != null) ? (potioneffect.getAmplifier() + 1) : 0;
        return (mc.thePlayer.getMaxFallHeight() + f);
    }
}
