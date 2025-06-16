package com.empty.nfpractice.block.multiblock;

import com.empty.nfpractice.NFPractice;
import com.empty.nfpractice.util.LocalAABB;
import com.empty.nfpractice.util.LocalBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StructShapes implements Iterable<LocalBlockPos> {
    private final LocalBound blockWiseBound;
    private final VoxelShape fullShape;
    private final BlockWiseShapes blockShapes;

    public StructShapes() {
        this(Shapes.box(0,0,0,2,2, 2));
    }

    public StructShapes(VoxelShape fullShape) {
        this.fullShape = fullShape;
        this.blockShapes = new BlockWiseShapes();
        this.blockWiseBound = LocalBound.of(fullShape);
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

    public LocalBound getBlockWiseBound() {
        return this.blockWiseBound;
    }

    public @NotNull boolean occupied(LocalBlockPos blockPos) {
        if (!blockWiseBound.isInside(blockPos)) {
            return false;
        }
        return blockShapes.get().containsKey(blockPos);
    }

    @Override
    public @NotNull Iterator<LocalBlockPos> iterator() {
        return blockShapes.get().keySet().iterator();
    }

    /*
     * Split the Full Voxel Shape to Blockwise Subshape
     * Only Calculate the Default Shape (Facing North)
     * Rotate to Fill Shape for other Direction
     */
    public void splitVoxelShapePerBlock() {
        Map<LocalBlockPos, List<AABB>> splitMap = new HashMap<>();
        for (AABB box : this.fullShape.toAabbs()) {
            // Determine the range of blocks this AABB spans
            int minX = (int)Math.floor(box.minX);
            int maxX = (int)Math.floor(box.maxX - 1e-6); // avoid ceiling rounding
            int minY = (int)Math.floor(box.minY);
            int maxY = (int)Math.floor(box.maxY - 1e-6);
            int minZ = (int)Math.floor(box.minZ);
            int maxZ = (int)Math.floor(box.maxZ - 1e-6);

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        LocalBlockPos blockPos = new LocalBlockPos(x, y, z);

                        // Clip the AABB to the block-local space [0,1)
                        double clippedMinX = Math.max(0, box.minX - x);
                        double clippedMaxX = Math.min(1, box.maxX - x);
                        double clippedMinY = Math.max(0, box.minY - y);
                        double clippedMaxY = Math.min(1, box.maxY - y);
                        double clippedMinZ = Math.max(0, box.minZ - z);
                        double clippedMaxZ = Math.min(1, box.maxZ - z);

                        if (clippedMinX < clippedMaxX && clippedMinY < clippedMaxY && clippedMinZ < clippedMaxZ) {
                            AABB localBox = new AABB(clippedMinX, clippedMinY, clippedMinZ,
                                    clippedMaxX, clippedMaxY, clippedMaxZ);
                            splitMap.computeIfAbsent(blockPos, k -> new ArrayList<>()).add(localBox);
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

            this.blockShapes.register(entry.getKey(), combined);
        }
        NFPractice.LOGGER.info("\nboxes: {}", this.blockShapes.get());
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
            // Move shape from Block Frame to Structure Local Frame
            VoxelShape inStructShape = localShape.move(localPos.getX(), localPos.getY(), localPos.getZ());
            // Step 2: Rotate each AABB in the shape around Y-axis (origin = 0,0,0)
            VoxelShape rotatedShape = Shapes.empty();
            for (Direction dir : new Direction[] {Direction.SOUTH, Direction.WEST, Direction.EAST}) {
                for (AABB box : inStructShape.toAabbs()) {
                    AABB rotatedBox = LocalAABB.of(box).faceTo(dir);
                    rotatedShape = Shapes.or(rotatedShape, Shapes.create(rotatedBox));
                }
                this.get(dir).put(localPos, rotatedShape.optimize());
            }
        }
    }

    private static class LocalBound extends BoundingBox {
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
                    (int) shapeBound.maxX,
                    (int) shapeBound.maxY,
                    (int) shapeBound.maxZ
            );
        }
    }
}