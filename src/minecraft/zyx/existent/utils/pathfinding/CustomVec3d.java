package zyx.existent.utils.pathfinding;

import net.minecraft.util.math.Vec3d;

public class CustomVec3d {
    private double x;
    private double y;
    private double z;

    public CustomVec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public CustomVec3d addVector(double x, double y, double z) {
        return new CustomVec3d(this.x + x, this.y + y, this.z + z);
    }

    public CustomVec3d floor() {
        return new CustomVec3d(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
    }

    public double squareDistanceTo(CustomVec3d v) {
        return Math.pow(v.x - this.x, 2.0D) + Math.pow(v.y - this.y, 2.0D) + Math.pow(v.z - this.z, 2.0D);
    }

    public CustomVec3d add(CustomVec3d v) {
        return addVector(v.getX(), v.getY(), v.getZ());
    }

    public Vec3d mc() {
        return new Vec3d(this.x, this.y, this.z);
    }

    public String toString() {
        return "[" + this.x + ";" + this.y + ";" + this.z + "]";
    }
}
