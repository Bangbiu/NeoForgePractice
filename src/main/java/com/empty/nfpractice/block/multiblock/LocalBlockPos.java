package com.empty.nfpractice.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class LocalBlockPos extends BlockPos {
    public LocalBlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    public LocalBlockPos faceTo(Direction dir) {
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

    public static LocalBlockPos of(BlockPos pos) {
        return new LocalBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }
}