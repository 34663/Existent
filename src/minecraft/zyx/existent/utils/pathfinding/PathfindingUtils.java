package zyx.existent.utils.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import zyx.existent.utils.MCUtil;

import java.util.ArrayList;

public class PathfindingUtils implements MCUtil {
    public static ArrayList<CustomVec3d> computePath(CustomVec3d topFrom, CustomVec3d to) {
        if (!canPassThrow(new BlockPos(topFrom.mc())))
            topFrom = topFrom.addVector(0.0D, 1.0D, 0.0D);
        AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
        pathfinder.compute();
        int i = 0;
        CustomVec3d lastLoc = null;
        CustomVec3d lastDashLoc = null;
        ArrayList<CustomVec3d> path = new ArrayList<>();
        ArrayList<CustomVec3d> pathFinderPath = pathfinder.getPath();
        label42:
        for (CustomVec3d pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null)
                    path.add(lastLoc.addVector(0.5D, 0.0D, 0.5D));
                path.add(pathElm.addVector(0.5D, 0.0D, 0.5D));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > 25.0D) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    for (int x = (int) smallX; x <= bigX; x++) {
                        for (int y = (int) smallY; y <= bigY; y++) {
                            for (int z = (int) smallZ; z <= bigZ; z++) {
                                if (!AStarCustomPathFinder.checkPositionValidity(new CustomVec3d(x, y, z))) {
                                    canContinue = false;
                                    continue label42;
                                }
                            }
                        }
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5D, 0.0D, 0.5D));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            i++;
        }
        return path;
    }

    private static boolean canPassThrow(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return (block.getMaterial() == Material.AIR || block.getMaterial() == Material.PLANTS || block.getMaterial() == Material.VINE || block == Blocks.LADDER || block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.WALL_SIGN || block == Blocks.STANDING_SIGN);
    }
}
