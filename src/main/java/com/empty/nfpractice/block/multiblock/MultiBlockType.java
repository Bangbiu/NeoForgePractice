package com.empty.nfpractice.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

public class MultiBlockType {

    public final String name;
    public final StructShapes shapes;
    public final LocalBlockPos masterOffset;

    private MultiBlockType() {
        this.name = MultiBlockType.DEFAULT_ID;
        this.shapes = new StructShapes();
        this.masterOffset = new LocalBlockPos(0, 0, 0);
    }

    private MultiBlockType(String name, StructShapes shape, LocalBlockPos masterPos) {
        this.name = name;
        this.shapes = shape;
        this.masterOffset = masterPos;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.name +
                "\n Centered At " +  this.masterOffset.toString() +
                "\n Of Shape Bounded by " + this.shapes.getBlockWiseBound().toString();
    }

    public VoxelShape getFullShape() {
        return this.shapes.getFullShape();
    }

    public VoxelShape getMasterShape() {
        return this.getShapeAt(this.masterOffset);
    }

    public VoxelShape getShapeAt(LocalBlockPos localPos) {
        return this.shapes.shapeAt(localPos);
    }

    public BlockPos getWorldPosFromMaster(BlockPos masterWorldPos, LocalBlockPos localPos, Direction facing) {
        // Rotated Position According to Structure's FACING
        LocalBlockPos rotatedLocalCurPos = localPos.faceTo(facing);
        LocalBlockPos roatatedMasterOffset = this.masterOffset.faceTo(facing);
        // Master World Pos - Master Offset = Structure Origin World Pos
        // Structure Origin World Pos + Local Pos(localCurPos) = World Pos
        return masterWorldPos.subtract(roatatedMasterOffset).offset(rotatedLocalCurPos);
    }

    private static final Map<String, MultiBlockType> TYPES = new HashMap<>();

    public static final String DEFAULT_ID = "default";
    public static MultiBlockType DEFAULT = MultiBlockType.createDefault();

    public static MultiBlockType create(String name, StructShapes shape, LocalBlockPos masterPos) {
        return new MultiBlockType(name, shape, masterPos);
    }

    public static MultiBlockType createDefault() {
        return new MultiBlockType();
    }

    public static void clear() {
        TYPES.clear();
    }

    public static void init() {
        TYPES.put(DEFAULT_ID, DEFAULT);
    }

    public static MultiBlockType register(String name, VoxelShape fullShape, LocalBlockPos masterPos) {
        MultiBlockType toReg = create(name, StructShapes.of(fullShape), masterPos);
        TYPES.put(name, toReg);
        return toReg;
    }

    public static MultiBlockType request(String name) {
        return TYPES.getOrDefault(name, DEFAULT);
    }

}
