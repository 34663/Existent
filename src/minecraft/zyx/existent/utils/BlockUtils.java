package zyx.existent.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class BlockUtils implements MCUtil {
    public static boolean collideBlock(AxisAlignedBB axisAlignedBB, Collidable collide) {
        for (int x = MathHelper.floor(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                Block block = getBlock(new BlockPos(x, axisAlignedBB.minY, z));

                if (!collide.collideBlock(block))
                    return false;
            }
        }
        return true;
    }

    public static boolean isInLiquid() {
        if (mc.thePlayer.isInWater()) {
            return true;
        }
        boolean inLiquid = false;
        final int y = (int) mc.thePlayer.getEntityBoundingBox().minY;
        for (int x = MathHelper.floor(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && block.getMaterial() != Material.AIR) {
                    if (!(block instanceof BlockLiquid)) return false;
                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }

    public static boolean isOnLiquid(double profondeur) {
        return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - profondeur, mc.thePlayer.posZ)).getBlock().getMaterial().isLiquid();
    }

    public static boolean isTotalOnLiquid(double profondeur) {
        for (double x = mc.thePlayer.boundingBox.minX; x < mc.thePlayer.boundingBox.maxX; x += 0.01f) {

            for (double z = mc.thePlayer.boundingBox.minZ; z < mc.thePlayer.boundingBox.maxZ; z += 0.01f) {
                Block block = mc.theWorld.getBlockState(new BlockPos(x, mc.thePlayer.posY - profondeur, z)).getBlock();
                if (!(block instanceof BlockLiquid) && !(block instanceof BlockAir)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static IBlockState getState(BlockPos pos) {
        return mc.theWorld.getBlockState(pos);
    }

    public interface Collidable {
        boolean collideBlock(Block block);
    }
}
