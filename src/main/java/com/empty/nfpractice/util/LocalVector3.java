package com.empty.nfpractice.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class LocalVector3 extends Vec3 implements LocalFrameData<LocalVector3> {

    public LocalVector3(double x, double y, double z) {
        super(x, y, z);
    }

    /**
     * Turning This Vector Around [0.5, 0.5, 0.5] </br>
     * Making Vector at block [0, 0, 0] stay in the same block
     * @param dir
     * @return
     */
    @Override
    public LocalVector3 faceTo(Direction dir) {
        if (Direction.NORTH == dir) return this;
        return this.mutable()
                .subtract(CENTER)
                .factTo(dir)
                .add(CENTER)
                .immutable();
    }

    public MutableLocalVec3 mutable() {
        return new MutableLocalVec3(this.x, this.y, this.z);
    }

    public static final LocalVector3 CENTER = new LocalVector3(0.5, 0.5, 0.5);

    public static class MutableLocalVec3 {
        public double x, y, z;
        public MutableLocalVec3(double x, double y, double z) {
            this.set(x, y, z);
        }

        public MutableLocalVec3 set(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public MutableLocalVec3 subtract(Vec3 vec) {
            this.x -= vec.x;
            this.y -= vec.y;
            this.z -= vec.z;
            return this;
        }

        public MutableLocalVec3 add(Vec3 vec) {
            this.x += vec.x;
            this.y += vec.y;
            this.z += vec.z;
            return this;
        }

        public LocalVector3 immutable() {
            return new LocalVector3(this.x, this.y, this.z);
        }

        public MutableLocalVec3 factTo(Direction dir) {
            switch (dir) {
                case NORTH:
                    return this.set(x, y, z);
                case EAST:
                    return this.set(-z, y, x); // 90 degrees CW
                case SOUTH:
                    return this.set(-x, y, -z); // 180 degrees
                case WEST:
                    return this.set(z, y, -x); // 270 degrees CW
                case DOWN:
                    return this.set(x, z, -y);
                case UP:
                    return this.set(x, -z, y);
                default:
                    return this.set(x, y, z); // no rotation
            }
        }
    }

}
