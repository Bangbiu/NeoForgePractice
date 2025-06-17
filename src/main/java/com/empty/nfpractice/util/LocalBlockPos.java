package com.empty.nfpractice.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class LocalBlockPos extends BlockPos implements LocalFrameData<LocalBlockPos> {
    public LocalBlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public LocalBlockPos faceTo(Direction dir) {
        if (Direction.NORTH == dir) return this;
        int _x = this.getX();
        int _y = this.getY();
        int _z = this.getZ();
        switch (dir) {
            case EAST:
                return new LocalBlockPos(-_z, _y, _x); // 90 degrees CW
            case SOUTH:
                return new LocalBlockPos(-_x, _y, -_z); // 180 degrees
            case WEST:
                return new LocalBlockPos(_z, _y, -_x); // 270 degrees CW
            default:
                return new LocalBlockPos(_x, _y, _z); // no rotation
        }
    }

    public LocalBlockPos negate() {
        return LocalBlockPos.of(this.multiply(-1));
    }

    public static LocalBlockPos of(BlockPos pos) {
        return new LocalBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }
}