package com.empty.nfpractice.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class MultiBlockType {

    public final String NAME;
    public final StructShapes SHAPES;
    public final LocalBlockPos MASTER_OFFSET;

    private MultiBlockType() {
        this.NAME = "default";
        this.SHAPES = new StructShapes();
        this.MASTER_OFFSET = new LocalBlockPos(0, 0, 0);
    }

    private MultiBlockType(String name, StructShapes shape, LocalBlockPos masterPos) {
        this.NAME = name;
        this.SHAPES = shape;
        this.MASTER_OFFSET = masterPos;
    }

    public BlockPos getWorldPosFromMaster(BlockPos masterWorldPos, LocalBlockPos localPos, Direction facing) {
        // Rotated Position According to Structure's FACING
        LocalBlockPos rotatedLocalCurPos = localPos.faceTo(facing);
        LocalBlockPos roatatedMasterOffset = this.MASTER_OFFSET.faceTo(facing);
        // Master World Pos - Master Offset = Structure Origin World Pos
        // Structure Origin World Pos + Local Pos(localCurPos) = World Pos
        return masterWorldPos.subtract(roatatedMasterOffset).offset(rotatedLocalCurPos);
    }

    public static MultiBlockType DEFAULT = MultiBlockType.createDefault();

    public static MultiBlockType create(String name, StructShapes shape, LocalBlockPos masterPos) {
        return new MultiBlockType(name, shape, masterPos);
    }

    public static MultiBlockType createDefault() {
        return new MultiBlockType();
    }

}
