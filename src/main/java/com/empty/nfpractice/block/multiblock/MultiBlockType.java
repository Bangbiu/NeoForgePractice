package com.empty.nfpractice.block.multiblock;

import com.empty.nfpractice.util.LocalBlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

public class MultiBlockType {

    public final String name;
    public final StructShapes shapes;

    private MultiBlockType() {
        this.name = MultiBlockType.DEFAULT_ID;
        this.shapes = new StructShapes();
    }

    private MultiBlockType(String name, StructShapes shape) {
        this.name = name;
        this.shapes = shape;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.name +
                "\n Of Shape Bounded by " + this.shapes.getBlockWiseBound();
    }

    public VoxelShape getFullShape() {
        return this.shapes.getFullShape();
    }

    public VoxelShape getMasterShape() {
        return this.getShapeAt(new LocalBlockPos(0,0,0));
    }

    public VoxelShape getShapeAt(LocalBlockPos localPos) {
        return this.shapes.shapeAt(localPos);
    }

    private static final Map<String, MultiBlockType> TYPES = new HashMap<>();

    public static final String DEFAULT_ID = "default";
    public static MultiBlockType DEFAULT = MultiBlockType.createDefault();

    public static MultiBlockType create(String name, StructShapes shape) {
        return new MultiBlockType(name, shape);
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

    public static MultiBlockType register(String name, VoxelShape fullShape) {
        MultiBlockType toReg = create(name, StructShapes.of(fullShape));
        TYPES.put(name, toReg);
        return toReg;
    }

    public static MultiBlockType request(String name) {
        return TYPES.getOrDefault(name, DEFAULT);
    }

}
