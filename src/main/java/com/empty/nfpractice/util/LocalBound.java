package com.empty.nfpractice.util;

import com.google.common.collect.AbstractIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;

public class LocalBound extends BoundingBox implements Iterable<LocalBlockPos>, LocalFrameData<LocalBound> {
    public LocalBound(int x1, int y1, int z1, int x2, int y2, int z2) {
        super(x1, y1, z1, x2, y2, z2);
    }

    public LocalBound(int maxX, int maxY, int maxZ) {
        super(0, 0, 0, maxX, maxY, maxZ);
    }

    /**
     * @return BlockPos.betweenClosed Wrapper Iterator
     */
    @Override
    public @NotNull Iterator<LocalBlockPos> iterator() {
        Iterator<BlockPos> blockPosIterator = BlockPos.betweenClosed(
                this.minX(), this.minY(), this.minZ(),
                this.maxX(), this.maxY(), this.maxZ()
        ).iterator();
        return new AbstractIterator<>() {
            @Override
            protected @Nullable LocalBlockPos computeNext() {
                if (!blockPosIterator.hasNext())
                    return this.endOfData();
                return LocalBlockPos.of(blockPosIterator.next());
            }
        };
    }

    @Override
    public LocalBound faceTo(Direction dir) {
        LocalBlockPos[] corners = {
                new LocalBlockPos(this.minX(), this.minY(), this.minZ()),
                new LocalBlockPos(this.maxX(), this.minY(), this.minZ()),
                new LocalBlockPos(this.minX(), this.maxY(), this.minZ()),
                new LocalBlockPos(this.maxX(), this.maxY(), this.minZ()),
                new LocalBlockPos(this.minX(), this.minY(), this.maxZ()),
                new LocalBlockPos(this.maxX(), this.minY(), this.maxZ()),
                new LocalBlockPos(this.minX(), this.maxY(), this.maxZ()),
                new LocalBlockPos(this.maxX(), this.maxY(), this.maxZ()),
        };

        for (int i = 0; i < corners.length; i++) {
            corners[i] = corners[i].faceTo(dir);
        }

        int minX = Arrays.stream(corners).mapToInt(v -> v.getX()).min().getAsInt();
        int maxX = Arrays.stream(corners).mapToInt(v -> v.getX()).max().getAsInt();
        int minY = Arrays.stream(corners).mapToInt(v -> v.getY()).min().getAsInt();
        int maxY = Arrays.stream(corners).mapToInt(v -> v.getY()).max().getAsInt();
        int minZ = Arrays.stream(corners).mapToInt(v -> v.getZ()).min().getAsInt();
        int maxZ = Arrays.stream(corners).mapToInt(v -> v.getZ()).max().getAsInt();

        return new LocalBound(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Round Value to Half Open Range [min, max)</br>
     * Find block index of value in between
     * <p>
     * Example: 3.0 -> Inside Block Index 2
     */
    public static int blockIndex(double value) {
        // Value on edge need to deduct by 1
        return Mth.floor(value) == Mth.ceil(value) ? Mth.floor(value) - 1 : Mth.floor(value);
    }

    public static LocalBound of(VoxelShape shape) {
        return LocalBound.of(shape.bounds());
    }

    public static LocalBound of(AABB aabb) {
        return new LocalBound(
                blockIndex(aabb.minX),
                blockIndex(aabb.minY),
                blockIndex(aabb.minZ),
                blockIndex(aabb.maxX),
                blockIndex(aabb.maxY),
                blockIndex(aabb.maxZ)
        );
    }
}
