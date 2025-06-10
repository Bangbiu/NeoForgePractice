package com.empty.nfpractice.block.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class MultiBlockType {
    public final StructShapes SHAPES;
    public final BlockPos MASTER_OFFSET;

    private MultiBlockType() {
        this.SHAPES = new StructShapes();
        this.MASTER_OFFSET = new BlockPos(0, 0, 0);
    }

    public static MultiBlockType createDefault() {
        return new MultiBlockType();
    }

    public static MultiBlockType create() {
        return new MultiBlockType();
    }

    @NotNull
    public boolean eachOccupiedFromMaster(Function<BlockPos, Boolean> func) {
        return this.eachOccupied(blockPos -> func.apply(blockPos.subtract(MASTER_OFFSET)));
    }

    @NotNull
    public boolean eachOccupied(Function<BlockPos, Boolean> func) {
        for (int x = 0; x < this.SHAPES.BOUND.maxX(); x++) {
            for (int y = 0; y < this.SHAPES.BOUND.maxY(); y++) {
                for (int z = 0; z < this.SHAPES.BOUND.maxZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (this.SHAPES.occupied(pos)) {
                        if (!func.apply(pos)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
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

    public static class StructShapes implements Function<BlockPos, VoxelShape> {
        public final LocalBound BOUND = new LocalBound(2, 2, 2);;

        @Override
        public VoxelShape apply(BlockPos blockPos) {
            if (!this.occupied(blockPos)) {
                return null;
            }
            return Shapes.block();
        }

        @NotNull
        public boolean occupied(BlockPos blockPos) {
            if (!BOUND.isInside(blockPos)) {
                return false;
            }
            // Rules
            return true;
        }
    }
}
