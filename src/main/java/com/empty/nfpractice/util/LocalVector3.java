package com.empty.nfpractice.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class LocalVector3 extends Vec3 implements LocalFrameData<LocalVector3> {

    public LocalVector3(double x, double y, double z) {
        super(x, y, z);
    }

    @Override
    public LocalVector3 faceTo(Direction dir) {
        double _x = this.x;
        double _y = this.y;
        double _z = this.z;
        switch (dir) {
            case EAST:
                return new LocalVector3(-_z, _y, _x); // 90 degrees CW
            case SOUTH:
                return new LocalVector3(-_x, _y, -_z); // 180 degrees
            case WEST:
                return new LocalVector3(_z, _y, -_x); // 270 degrees CW
            default:
                return new LocalVector3(_x, _y, _z); // no rotation
        }
    }
}
