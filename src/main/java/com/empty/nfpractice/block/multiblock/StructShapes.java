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

    /**
     * Split the Full Voxel Shape to Blockwise Subshape</br>
     * Only Calculate the Default Shape (Facing North)</br>
     * Rotate to Fill Shape for other Direction
     */
    public void splitVoxelShapePerBlock() {
        Map<LocalBlockPos, List<AABB>> splitMap = new HashMap<>();
        for (AABB box : this.fullShape.toAabbs()) {
            // Determine the range of blocks this AABB spans
            LocalBound bound = LocalBound.of(box);
            for (LocalBlockPos localPos : bound) {
                // Clip the AABB to the block-local space [0,1)
                double clippedMinX = Math.max(0, box.minX - localPos.getX());
                double clippedMaxX = Math.min(1, box.maxX - localPos.getX());
                double clippedMinY = Math.max(0, box.minY - localPos.getY());
                double clippedMaxY = Math.min(1, box.maxY - localPos.getY());
                double clippedMinZ = Math.max(0, box.minZ - localPos.getZ());
                double clippedMaxZ = Math.min(1, box.maxZ - localPos.getZ());

                if (clippedMinX < clippedMaxX && clippedMinY < clippedMaxY && clippedMinZ < clippedMaxZ) {
                    AABB localBox = new AABB(clippedMinX, clippedMinY, clippedMinZ,
                            clippedMaxX, clippedMaxY, clippedMaxZ);
                    splitMap.computeIfAbsent(localPos, k -> new ArrayList<>()).add(localBox);
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
}