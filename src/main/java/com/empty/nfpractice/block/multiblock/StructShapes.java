package com.empty.nfpractice.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StructShapes implements Iterable<LocalBlockPos> {
    public final LocalBound blockWiseBound;
    private final VoxelShape fullShape;
    private final Map<BlockPos, VoxelShape> blockShapes;

    public StructShapes() {
        this(Shapes.box(0,0,0,2,2, 2));
    }

    public StructShapes(VoxelShape fullShape) {
        this.fullShape = fullShape;
        this.blockWiseBound = LocalBound.of(fullShape);
        this.blockShapes = new HashMap<>();
    }

    public VoxelShape shapeAt(LocalBlockPos blockPos) {
        if (!this.occupied(blockPos)) {
            return Shapes.empty();
        }
        // PlaceHolder
        this.fullShape.optimize();
        return Shapes.block();
    }

    public @NotNull boolean occupied(LocalBlockPos blockPos) {
        if (!blockWiseBound.isInside(blockPos)) {
            return false;
        }
        // Rules
        return true;
    }

    public @NotNull boolean occupied(int x, int y, int z) {
        return this.occupied(new LocalBlockPos(x, y, z));
    }

    @Override
    public @NotNull Iterator<LocalBlockPos> iterator() {
        return new ShapeBlockIterator();
    }

    public class ShapeBlockIterator implements Iterator<LocalBlockPos> {
        int nextX, nextY, nextZ;

        public ShapeBlockIterator() {
            // To Minimal Pos
            this.nextX = blockWiseBound.minX();
            this.nextY = blockWiseBound.minY();
            this.nextZ = blockWiseBound.minZ() - 1;
            toNextOccupied();
        }

        @Override
        public boolean hasNext() {
            return nextX < blockWiseBound.maxX();
        }

        @Override
        public LocalBlockPos next() {
            LocalBlockPos toRet = new LocalBlockPos(nextX, nextY, nextZ);
            this.toNextOccupied();
            return toRet;
        }

        public void toNextOccupied() {
            do {
                nextZ++;
                if (nextZ >= blockWiseBound.maxZ()) {
                    nextZ = 0;
                    nextY++;
                }
                if (nextY >= blockWiseBound.maxY()) {
                    nextY = 0;
                    nextX++;
                }
            } while (nextX < blockWiseBound.maxX() && !occupied(nextX, nextY, nextZ));
        }
    }

    public void splitVoxelShapePerBlock() {
        Map<BlockPos, List<AABB>> splitMap = new HashMap<>();
        for (AABB box : this.fullShape.toAabbs()) {
            // Determine all blocks this box overlaps
            int minX = (int)Math.floor(box.minX);
            int minY = (int)Math.floor(box.minY);
            int minZ = (int)Math.floor(box.minZ);
            int maxX = (int)Math.floor(box.maxX);
            int maxY = (int)Math.floor(box.maxY);
            int maxZ = (int)Math.floor(box.maxZ);

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        double clipMinX = Math.max(box.minX, x);
                        double clipMinY = Math.max(box.minY, y);
                        double clipMinZ = Math.max(box.minZ, z);
                        double clipMaxX = Math.min(box.maxX, x + 1);
                        double clipMaxY = Math.min(box.maxY, y + 1);
                        double clipMaxZ = Math.min(box.maxZ, z + 1);

                        // Skip if the box doesn't overlap this block
                        if (clipMinX < clipMaxX && clipMinY < clipMaxY && clipMinZ < clipMaxZ) {
                            AABB clipped = new AABB(
                                    clipMinX - x, clipMinY - y, clipMinZ - z,
                                    clipMaxX - x, clipMaxY - y, clipMaxZ - z
                            );

                            BlockPos pos = new BlockPos(x, y, z);
                            splitMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(clipped);
                        }
                    }
                }
            }
        }

        // Convert clipped boxes to voxel shapes
        for (Map.Entry<BlockPos, List<AABB>> entry : splitMap.entrySet()) {
            VoxelShape combined = Shapes.empty();
            for (AABB box : entry.getValue()) {
                combined = Shapes.or(combined, Shapes.create(box));
            }
            this.blockShapes.put(entry.getKey(), combined);
        }
    }

    public static class LocalBound extends BoundingBox {
        public LocalBound(int x1, int y1, int z1, int x2, int y2, int z2) {
            super(x1, y1, z1, x2, y2, z2);
        }

        public LocalBound(int maxX, int maxY, int maxZ) {
            super(0, 0, 0, maxX, maxY, maxZ);
        }

        public static LocalBound of(VoxelShape shape) {
            AABB shapeBound = shape.bounds();
            return new LocalBound(
                    (int) shapeBound.minX,
                    (int) shapeBound.minY,
                    (int) shapeBound.minZ,
                    (int) shapeBound.maxX + 1,
                    (int) shapeBound.maxY + 1,
                    (int) shapeBound.maxZ + 1
            );
        }
    }
}