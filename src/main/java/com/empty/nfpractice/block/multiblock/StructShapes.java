package com.empty.nfpractice.block.multiblock;

import com.empty.nfpractice.NFPractice;
import com.empty.nfpractice.util.LocalAABB;
import com.empty.nfpractice.util.LocalBlockPos;
import com.empty.nfpractice.util.LocalBound;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StructShapes implements Iterable<LocalBlockPos> {
    private final LocalBound bound;
    private final VoxelShape fullShape;
    private final DirectionalBlockWiseShapes blockShapes;
    private final boolean yawOnly;

    public StructShapes() {
        this(Shapes.box(0,0,0,2,2, 2));
    }

    public StructShapes(VoxelShape fullShape) {
        this(fullShape, true);
    }

    public StructShapes(VoxelShape fullShape, boolean yawOnly) {
        this.fullShape = fullShape;
        this.blockShapes = new DirectionalBlockWiseShapes();
        this.bound = LocalBound.of(fullShape);
        this.yawOnly = yawOnly;
        // Fill Block Shapes
        this.computeBlockWiseShapes();
    }

    public VoxelShape rotatedShapeAt(LocalBlockPos localPos, Direction dir) {
        // Check if Block Pos Has Shape
        if (!blockShapes.get(dir).containsKey(localPos)) {
            return Shapes.empty();
        }
        return this.blockShapes.get(dir).get(localPos);
    }

    public VoxelShape getFullShape() {
        return this.fullShape;
    }

    public LocalBound getBound() {
        return this.bound;
    }

    @Override
    public @NotNull Iterator<LocalBlockPos> iterator() {
        return blockShapes.get().keySet().iterator();
    }

    public @NotNull Iterable<LocalBlockPos> allLocalPosFacing(Direction dir) {
        return blockShapes.get(dir).keySet();
    }

    /**
     * Split the Full Voxel Shape to Blockwise Subshape</br>
     * Only Calculate the Default Shape (Facing North)</br>
     * Rotate to Fill Shape for other Direction
     */
    public void computeBlockWiseShapes() {
        this.blockShapes.clear();
        Direction[] directions = new Direction[]{ Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
        if (!this.yawOnly) directions = Direction.values();
        for (Direction dir : directions) {
            Map<LocalBlockPos, List<LocalAABB>> splitMap = new HashMap<>();
            for (AABB box : this.fullShape.toAabbs()) {
                // Determine the range of blocks this AABB spans
                LocalAABB localBox = LocalAABB.of(box).faceTo(dir);
                for (Map.Entry<LocalBlockPos, LocalAABB> entry : localBox.splitPerBlock().entrySet()) {
                    splitMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
                }
                NFPractice.LOGGER.info("\n Dir: {}\n{}", dir, localBox.getBound());
            }
            // Convert clipped boxes to voxel shapes
            for (Map.Entry<LocalBlockPos, List<LocalAABB>> entry : splitMap.entrySet()) {
                VoxelShape combined = Shapes.empty();
                for (AABB box : entry.getValue()) {
                    combined = Shapes.or(combined, Shapes.create(box));
                }
                this.blockShapes.register(dir, entry.getKey(), combined);
            }
        }
        if (yawOnly) {
            this.blockShapes.put(Direction.DOWN, this.blockShapes.get());
            this.blockShapes.put(Direction.UP, this.blockShapes.get());
        }

    }

    public static StructShapes of(VoxelShape fullShape) {
        return new StructShapes(fullShape);
    }

    private class DirectionalBlockWiseShapes extends EnumMap<Direction, Map<LocalBlockPos, VoxelShape>> {
        public DirectionalBlockWiseShapes() {
            super(Direction.class);
        }

        public Map<LocalBlockPos, VoxelShape> get() {
            return this.get(Direction.NORTH);
        }

        public void register(Direction dir, LocalBlockPos localPos, VoxelShape localShape) {
            // Register Rotated Shape
            this.computeIfAbsent(dir, k -> new HashMap<>()).put(localPos, localShape);
        }
    }
}