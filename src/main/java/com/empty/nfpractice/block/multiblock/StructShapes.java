package com.empty.nfpractice.block.multiblock;

import com.empty.nfpractice.NFPractice;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StructShapes implements Iterable<LocalBlockPos> {
    public final LocalBound blockWiseBound;
    private final VoxelShape fullShape;
    private final Map<LocalBlockPos, VoxelShape> blockShapes;

    public StructShapes() {
        this(Shapes.box(0,0,0,2,2, 2));
    }

    public StructShapes(VoxelShape fullShape) {
        this.fullShape = fullShape;
        this.blockWiseBound = LocalBound.of(fullShape);
        this.blockShapes = new HashMap<>();
        this.splitVoxelShapePerBlock();
    }

    public VoxelShape shapeAt(LocalBlockPos blockPos) {
        if (!this.occupied(blockPos)) {
            return Shapes.empty();
        }
        // PlaceHolder
        return this.blockShapes.get(blockPos);
    }

    public @NotNull boolean occupied(LocalBlockPos blockPos) {
        if (!blockWiseBound.isInside(blockPos)) {
            return false;
        }
        return blockShapes.containsKey(blockPos);
    }

    public @NotNull boolean occupied(int x, int y, int z) {
        return this.occupied(new LocalBlockPos(x, y, z));
    }

    @Override
    public @NotNull Iterator<LocalBlockPos> iterator() {
        return blockShapes.keySet().iterator();
    }

    public void splitVoxelShapePerBlock() {
        this.blockShapes.clear();

        Map<LocalBlockPos, List<AABB>> splitMap = new HashMap<>();
        for (AABB box : this.fullShape.toAabbs()) {
            // Determine all blocks this box overlaps
            int minX = (int)Math.floor(box.minX);
            int minY = (int)Math.floor(box.minY);
            int minZ = (int)Math.floor(box.minZ);
            int maxX = (int)Math.ceil(box.maxX) - 1;
            int maxY = (int)Math.ceil(box.maxY) - 1;
            int maxZ = (int)Math.ceil(box.maxZ) - 1;

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

                            LocalBlockPos pos = new LocalBlockPos(x, y, z);
                            splitMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(clipped);
                        }
                    }
                }
            }
        }

        // Convert clipped boxes to voxel shapes
        for (Map.Entry<LocalBlockPos, List<AABB>> entry : splitMap.entrySet()) {
            VoxelShape combined = Shapes.empty();
            for (AABB box : entry.getValue()) {
                combined = Shapes.or(combined, Shapes.create(box));
            }
            //NFPractice.LOGGER.info("\nbox\n:{} = {}", entry.getKey(), combined);
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