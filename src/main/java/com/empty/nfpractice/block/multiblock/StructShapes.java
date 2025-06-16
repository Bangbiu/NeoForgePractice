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
    private final BlockWiseShapes blockShapes;

    public StructShapes() {
        this(Shapes.box(0,0,0,2,2, 2));
    }

    public StructShapes(VoxelShape fullShape) {
        this.fullShape = fullShape;
        this.blockShapes = new BlockWiseShapes();
        this.bound = LocalBound.of(fullShape);
        // Fill Block Shapes
        this.splitVoxelShapePerBlock();
    }

    public VoxelShape shapeAt(LocalBlockPos localPos) {
        return this.rotatedShapeAt(localPos, Direction.NORTH);
    }

    public VoxelShape rotatedShapeAt(LocalBlockPos localPos, Direction dir) {
        if (!this.occupied(localPos)) {
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

    public @NotNull boolean occupied(LocalBlockPos blockPos) {
        if (!bound.isInside(blockPos)) {
            return false;
        }
        return blockShapes.get().containsKey(blockPos);
    }

    @Override
    public @NotNull Iterator<LocalBlockPos> iterator() {
        return blockShapes.get().keySet().iterator();
    }

    public @NotNull Iterator<LocalBlockPos> allLocalPosFacing(Direction dir) {
        return blockShapes.get(dir).keySet().iterator();
    }

    /**
     * Split the Full Voxel Shape to Blockwise Subshape</br>
     * Only Calculate the Default Shape (Facing North)</br>
     * Rotate to Fill Shape for other Direction
     */
    public void splitVoxelShapePerBlock() {
        Map<LocalBlockPos, List<LocalAABB>> splitMap = new HashMap<>();
        for (AABB box : this.fullShape.toAabbs()) {
            // Determine the range of blocks this AABB spans
            LocalAABB localBox = LocalAABB.of(box);
            for (Map.Entry<LocalBlockPos, LocalAABB> entry : localBox.splitPerBlock().entrySet()) {
                splitMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
            }
        }
        // Convert clipped boxes to voxel shapes
        for (Map.Entry<LocalBlockPos, List<LocalAABB>> entry : splitMap.entrySet()) {
            VoxelShape combined = Shapes.empty();
            for (AABB box : entry.getValue()) {
                combined = Shapes.or(combined, Shapes.create(box));
            }

            this.blockShapes.register(entry.getKey(), combined);
        }
    }

    public static StructShapes of(VoxelShape fullShape) {
        return new StructShapes(fullShape);
    }

    private static class BlockWiseShapes extends EnumMap<Direction, Map<LocalBlockPos, VoxelShape>> {
        public BlockWiseShapes() {
            super(Direction.class);
            this.put(Direction.NORTH, new HashMap<>());
            this.put(Direction.SOUTH, new HashMap<>());
            this.put(Direction.WEST, new HashMap<>());
            this.put(Direction.EAST, new HashMap<>());
            this.put(Direction.UP, this.get());
            this.put(Direction.DOWN, this.get());
        }

        public Map<LocalBlockPos, VoxelShape> get() {
            return this.get(Direction.NORTH);
        }

        public void register(LocalBlockPos localPos, VoxelShape localShape) {
            this.get().put(localPos, localShape.optimize());
            // Register Rotated Shape
            VoxelShape rotatedShape = Shapes.empty();
            for (Direction dir : new Direction[] {Direction.SOUTH, Direction.WEST, Direction.EAST}) {
                for (AABB box : localShape.toAabbs()) {
                    LocalAABB rotatedBox = LocalAABB.of(box).faceTo(dir);
                    if (dir == Direction.SOUTH) {
                        NFPractice.LOGGER.info("\nboxesFrom: {}\nboxesTo{}", box, rotatedBox);
                    }

                    rotatedShape = Shapes.or(rotatedShape, Shapes.create(rotatedBox));
                }
                this.get(dir).put(localPos, rotatedShape.optimize());
            }
        }
    }
}