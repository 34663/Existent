package zyx.existent.utils.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import zyx.existent.utils.MCUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AStarCustomPathFinder implements MCUtil {
    private static final CustomVec3d[] flatCardinalDirections = new CustomVec3d[]{new CustomVec3d(1.0D, 0.0D, 0.0D), new CustomVec3d(-1.0D, 0.0D, 0.0D), new CustomVec3d(0.0D, 0.0D, 1.0D), new CustomVec3d(0.0D, 0.0D, -1.0D)};

    private final CustomVec3d startCustomVec3;

    private final CustomVec3d endCustomVec3;

    private final ArrayList<Hub> hubs = new ArrayList<>();

    private final ArrayList<Hub> hubsToWork = new ArrayList<>();

    private ArrayList<CustomVec3d> path = new ArrayList<>();

    public AStarCustomPathFinder(CustomVec3d startCustomVec3, CustomVec3d endCustomVec3) {
        this.startCustomVec3 = startCustomVec3.addVector(0.0D, 0.0D, 0.0D).floor();
        this.endCustomVec3 = endCustomVec3.addVector(0.0D, 0.0D, 0.0D).floor();
    }

    public static boolean checkPositionValidity(CustomVec3d vec3) {
        BlockPos pos = new BlockPos(vec3);
        if (isBlockSolid(pos) || isBlockSolid(pos.add(0, 1, 0)))
            return false;
        return isSafeToWalkOn(pos.add(0, -1, 0));
    }

    private static boolean isBlockSolid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return (block.isSolidFullCube() || block instanceof net.minecraft.block.BlockSlab || block instanceof net.minecraft.block.BlockStairs || block instanceof net.minecraft.block.BlockCactus || block instanceof net.minecraft.block.BlockChest || block instanceof net.minecraft.block.BlockEnderChest || block instanceof net.minecraft.block.BlockSkull || block instanceof net.minecraft.block.BlockPane || block instanceof net.minecraft.block.BlockFence || block instanceof net.minecraft.block.BlockWall || block instanceof net.minecraft.block.BlockGlass || block instanceof net.minecraft.block.BlockPistonBase || block instanceof net.minecraft.block.BlockPistonExtension || block instanceof net.minecraft.block.BlockPistonMoving || block instanceof net.minecraft.block.BlockStainedGlass || block instanceof net.minecraft.block.BlockTrapDoor);
    }

    private static boolean isSafeToWalkOn(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return (!(block instanceof net.minecraft.block.BlockFence) && !(block instanceof net.minecraft.block.BlockWall));
    }

    public ArrayList<CustomVec3d> getPath() {
        return this.path;
    }

    public void compute() {
        compute(1000, 4);
    }

    public void compute(int loops, int depth) {
        this.path.clear();
        this.hubsToWork.clear();
        ArrayList<CustomVec3d> initPath = new ArrayList<>();
        CustomVec3d startCustomVec3 = this.startCustomVec3;
        initPath.add(startCustomVec3);
        CustomVec3d[] flatCardinalDirections = AStarCustomPathFinder.flatCardinalDirections;
        this.hubsToWork.add(new Hub(startCustomVec3, null, initPath, startCustomVec3.squareDistanceTo(this.endCustomVec3), 0.0D, 0.0D));
        int i;
        label38:
        for (i = 0; i < loops; i++) {
            ArrayList<Hub> hubsToWork = this.hubsToWork;
            int hubsToWorkSize = hubsToWork.size();
            hubsToWork.sort(new CompareHub());
            int j = 0;
            if (hubsToWorkSize == 0)
                break;
            for (int i1 = 0; i1 < hubsToWorkSize; i1++) {
                Hub hub = hubsToWork.get(i1);
                j++;
                if (j > depth)
                    break;
                hubsToWork.remove(hub);
                this.hubs.add(hub);
                CustomVec3d hLoc = hub.getLoc();
                for (int i2 = 0, flatCardinalDirectionsLength = flatCardinalDirections.length; i2 < flatCardinalDirectionsLength; i2++) {
                    CustomVec3d loc = hLoc.add(flatCardinalDirections[i2]).floor();
                    if (checkPositionValidity(loc) && addHub(hub, loc, 0.0D))
                        break label38;
                }
                CustomVec3d loc1 = hLoc.addVector(0.0D, 1.0D, 0.0D).floor();
                if (checkPositionValidity(loc1) && addHub(hub, loc1, 0.0D))
                    break label38;
                CustomVec3d loc2 = hLoc.addVector(0.0D, -1.0D, 0.0D).floor();
                if (checkPositionValidity(loc2) && addHub(hub, loc2, 0.0D))
                    break label38;
            }
        }
        this.hubs.sort(new CompareHub());
        this.path = ((Hub) this.hubs.get(0)).getPath();
    }

    public Hub isHubExisting(CustomVec3d loc) {
        List<Hub> hubs = this.hubs;
        for (int i = 0, hubsSize = hubs.size(); i < hubsSize; i++) {
            Hub hub = hubs.get(i);
            CustomVec3d hubLoc = hub.getLoc();
            if (hubLoc.getX() == loc.getX() && hubLoc.getY() == loc.getY() && hubLoc.getZ() == loc.getZ())
                return hub;
        }
        List<Hub> hubsToWork = this.hubsToWork;
        for (int j = 0, hubsToWorkSize = hubsToWork.size(); j < hubsToWorkSize; j++) {
            Hub hub = hubsToWork.get(j);
            CustomVec3d hubLoc = hub.getLoc();
            if (hubLoc.getX() == loc.getX() && hubLoc.getY() == loc.getY() && hubLoc.getZ() == loc.getZ())
                return hub;
        }
        return null;
    }

    public boolean addHub(Hub parent, CustomVec3d loc, double cost) {
        Hub existingHub = isHubExisting(loc);
        double totalCost = cost;
        if (parent != null)
            totalCost += parent.getTotalCost();
        CustomVec3d endCustomVec3 = this.endCustomVec3;
        ArrayList<CustomVec3d> parentPath = parent.getPath();
        if (existingHub == null) {
            if (loc.getX() == endCustomVec3.getX() && loc.getY() == endCustomVec3.getY() && loc.getZ() == endCustomVec3.getZ()) {
                this.path.clear();
                this.path = parentPath;
                this.path.add(loc);
                return true;
            }
            parentPath.add(loc);
            this.hubsToWork.add(new Hub(loc, parent, parentPath, loc.squareDistanceTo(endCustomVec3), cost, totalCost));
        } else if (existingHub.getCost() > cost) {
            parentPath.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(parentPath);
            existingHub.setSquareDistanceToFromTarget(loc.squareDistanceTo(endCustomVec3));
            existingHub.setCost(cost);
            existingHub.setTotalCost(totalCost);
        }
        return false;
    }

    private static class Hub {
        private CustomVec3d loc;

        private Hub parent;

        private ArrayList<CustomVec3d> path;

        private double squareDistanceToFromTarget;

        private double cost;

        private double totalCost;

        public Hub(CustomVec3d loc, Hub parent, ArrayList<CustomVec3d> path, double squareDistanceToFromTarget, double cost, double totalCost) {
            this.loc = loc;
            this.parent = parent;
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }

        public CustomVec3d getLoc() {
            return this.loc;
        }

        public void setLoc(CustomVec3d loc) {
            this.loc = loc;
        }

        public Hub getParent() {
            return this.parent;
        }

        public void setParent(Hub parent) {
            this.parent = parent;
        }

        public ArrayList<CustomVec3d> getPath() {
            return this.path;
        }

        public void setPath(ArrayList<CustomVec3d> path) {
            this.path = path;
        }

        public double getSquareDistanceToFromTarget() {
            return this.squareDistanceToFromTarget;
        }

        public void setSquareDistanceToFromTarget(double squareDistanceToFromTarget) {
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
        }

        public double getCost() {
            return this.cost;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public double getTotalCost() {
            return this.totalCost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }
    }

    public static class CompareHub implements Comparator<Hub> {
        public int compare(AStarCustomPathFinder.Hub o1, AStarCustomPathFinder.Hub o2) {
            return
                    (int) (o1.getSquareDistanceToFromTarget() + o1.getTotalCost() - o2.getSquareDistanceToFromTarget() + o2.getTotalCost());
        }
    }
}
