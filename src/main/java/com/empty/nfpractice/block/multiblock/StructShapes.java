package com.empty.nfpractice.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class StructShapes implements Iterable<LocalBlockPos> {
    public final LocalBound BOUNDARY;

    public StructShapes() {
        this.BOUNDARY = new LocalBound(2, 1, 2);
    }

    public VoxelShape shapeAt(LocalBlockPos blockPos) {
        if (!this.occupied(blockPos)) {
            return Shapes.empty();
        }
        // PlaceHolder
        return Shapes.block();
    }

    public @NotNull boolean occupied(LocalBlockPos blockPos) {
        if (!BOUNDARY.isInside(blockPos)) {
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
        int nextX = 0, nextY = 0, nextZ = -1;

        public ShapeBlockIterator() {
            super();
            toNextOccupied();
        }

        @Override
        public boolean hasNext() {
            return nextX < BOUNDARY.maxX();
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
                if (nextZ >= BOUNDARY.maxZ()) {
                    nextZ = 0;
                    nextY++;
                }
                if (nextY >= BOUNDARY.maxY()) {
                    nextY = 0;
                    nextX++;
                }
            } while (nextX < BOUNDARY.maxX() && !occupied(nextX, nextY, nextZ));
        }
    }

    public static class LocalBound extends BoundingBox {
        public LocalBound() {
            this(1, 1, 1);
        }

        public LocalBound(BlockPos pos) {
            this(pos.getX(), pos.getY(), pos.getZ());
        }

        public LocalBound(int maxX, int maxY, int maxZ) {
            super(0, 0, 0, maxX, maxY, maxZ);
        }

        @Override
        public int minX() {
            return 0;
        }

        @Override
        public int minY() {
            return 0;
        }

        @Override
        public int minZ() {
            return 0;
        }
    }
}