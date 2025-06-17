package com.empty.nfpractice.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.*;

public class LocalAABB extends AABB implements LocalFrameData<LocalAABB> {
    private final Lazy<LocalBound> bound;
    private final Lazy<LocalVector3[]> corners;
    public LocalAABB(double x1, double y1, double z1, double x2, double y2, double z2) {
        super(x1, y1, z1, x2, y2, z2);
        this.bound = new Lazy<>(() -> LocalBound.of(this));
        this.corners = new Lazy<>(() -> LocalAABB.cornersOf(this));
    }

    public LocalAABB move(LocalBlockPos pos) {
        return LocalAABB.of(super.move(pos));
    }

    public LocalBound getBound() {
        return this.bound.get();
    }

    public LocalVector3[] getCorners() {
        return this.corners.get();
    }

    /**
     * Clip the AABB to the block-local space [0,1) At localPos
     * @param localPos
     * @return LocalAABB within the Local Block Pos
     */
    public @Nullable LocalAABB clipAt(LocalBlockPos localPos) {
        LocalAABB result = null;
        double clippedMinX = Math.max(0, this.minX - localPos.getX());
        double clippedMaxX = Math.min(1, this.maxX - localPos.getX());
        double clippedMinY = Math.max(0, this.minY - localPos.getY());
        double clippedMaxY = Math.min(1, this.maxY - localPos.getY());
        double clippedMinZ = Math.max(0, this.minZ - localPos.getZ());
        double clippedMaxZ = Math.min(1, this.maxZ - localPos.getZ());
        if (clippedMinX < clippedMaxX && clippedMinY < clippedMaxY && clippedMinZ < clippedMaxZ) {
            result = new LocalAABB(clippedMinX, clippedMinY, clippedMinZ,
                    clippedMaxX, clippedMaxY, clippedMaxZ);
        }
        return result;
    }

    public Map<LocalBlockPos, LocalAABB> splitPerBlock() {
        Map<LocalBlockPos, LocalAABB> splitMap = new HashMap<>();
        for (LocalBlockPos localPos : this.getBound()) {
            LocalAABB localBox = this.clipAt(localPos);
            if (localBox != null)
                splitMap.put(localPos, localBox);
        }
        return splitMap;
    }

    @Override
    public LocalAABB faceTo(Direction dir) {
        if (Direction.NORTH == dir) return this;
        LocalVector3[] corners = this.getCorners();

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

    public static LocalAABB of(AABB aabb) {
        return new LocalAABB(aabb.minX, aabb.minY, aabb.minZ,
                aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public static LocalVector3[] cornersOf(AABB aabb) {
        return new LocalVector3[] {
                new LocalVector3(aabb.minX, aabb.minY, aabb.minZ),
                new LocalVector3(aabb.maxX, aabb.minY, aabb.minZ),
                new LocalVector3(aabb.minX, aabb.maxY, aabb.minZ),
                new LocalVector3(aabb.maxX, aabb.maxY, aabb.minZ),
                new LocalVector3(aabb.minX, aabb.minY, aabb.maxZ),
                new LocalVector3(aabb.maxX, aabb.minY, aabb.maxZ),
                new LocalVector3(aabb.minX, aabb.maxY, aabb.maxZ),
                new LocalVector3(aabb.maxX, aabb.maxY, aabb.maxZ),
        };
    }
}
