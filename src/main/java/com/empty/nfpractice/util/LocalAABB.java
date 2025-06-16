package com.empty.nfpractice.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;

public class LocalAABB extends AABB implements LocalFrameData<LocalAABB> {

    public LocalAABB(double x1, double y1, double z1, double x2, double y2, double z2) {
        super(x1, y1, z1, x2, y2, z2);
    }

    public static LocalAABB of(AABB aabb) {
        return new LocalAABB(aabb.minX, aabb.minY, aabb.minZ,
                aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public LocalAABB move(LocalBlockPos pos) {
        return LocalAABB.of(super.move(pos));
    }

    @Override
    public LocalAABB faceTo(Direction dir) {
        LocalVector3[] corners = {
                new LocalVector3(this.minX, this.minY, this.minZ),
                new LocalVector3(this.maxX, this.minY, this.minZ),
                new LocalVector3(this.minX, this.maxY, this.minZ),
                new LocalVector3(this.maxX, this.maxY, this.minZ),
                new LocalVector3(this.minX, this.minY, this.maxZ),
                new LocalVector3(this.maxX, this.minY, this.maxZ),
                new LocalVector3(this.minX, this.maxY, this.maxZ),
                new LocalVector3(this.maxX, this.maxY, this.maxZ),
        };

        for (int i = 0; i < corners.length; i++) {
            corners[i] = corners[i].faceTo(dir);
        }

        double minX = Arrays.stream(corners).mapToDouble(v -> v.x).min().getAsDouble();
        double maxX = Arrays.stream(corners).mapToDouble(v -> v.x).max().getAsDouble();
        double minY = Arrays.stream(corners).mapToDouble(v -> v.y).min().getAsDouble();
        double maxY = Arrays.stream(corners).mapToDouble(v -> v.y).max().getAsDouble();
        double minZ = Arrays.stream(corners).mapToDouble(v -> v.z).min().getAsDouble();
        double maxZ = Arrays.stream(corners).mapToDouble(v -> v.z).max().getAsDouble();

        return new LocalAABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
