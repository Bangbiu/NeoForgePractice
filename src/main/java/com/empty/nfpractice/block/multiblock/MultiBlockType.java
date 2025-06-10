package com.empty.nfpractice.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class MultiBlockType {
    public final StructShapes SHAPES;
    public final LocalBlockPos MASTER_OFFSET;

    private MultiBlockType() {
        this.SHAPES = new StructShapes();
        this.MASTER_OFFSET = new LocalBlockPos(1, 0, 1);
    }

    public static MultiBlockType createDefault() {
        return new MultiBlockType();
    }

    public static MultiBlockType create() {
        return new MultiBlockType();
    }

    public BlockPos getWorldPosFromMaster(BlockPos masterWorldPos, LocalBlockPos localPos, Direction facing) {
        // Rotated Position According to Structure's FACING
        LocalBlockPos rotatedLocalCurPos = localPos.faceTo(facing);
        LocalBlockPos roatatedMasterOffset = this.MASTER_OFFSET.faceTo(facing);
        // Master World Pos - Master Offset = Structure Origin World Pos
        // Structure Origin World Pos + Local Pos(localCurPos) = World Pos
        return masterWorldPos.subtract(roatatedMasterOffset).offset(rotatedLocalCurPos);
    }

}
