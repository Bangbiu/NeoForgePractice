package com.empty.nfpractice.init;

import com.empty.nfpractice.NFPractice;
import com.empty.nfpractice.block.multiblock.LocalBlockPos;
import com.empty.nfpractice.block.multiblock.MultiBlockType;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.concurrent.Executor;

public class ModDataLoaders {

    public static class MultiBlockTypeLoader extends SimpleJsonResourceReloadListener {
        public static final Gson GSON = new GsonBuilder().create();

        public MultiBlockTypeLoader() {
            super(GSON, "multiblock"); // loads from assets/<modid>/multiblock/*.json
        }

        public MultiBlockTypeLoader(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller1, Executor executor, Executor executor1) {
            this();
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> objectMap, ResourceManager resourceManager, ProfilerFiller profiler) {
            MultiBlockType.clear();
            MultiBlockType.init();
            // Loading JSON
            for (Map.Entry<ResourceLocation, JsonElement> entry : objectMap.entrySet()) {
                ResourceLocation id = entry.getKey();
                JsonObject mbInfo = entry.getValue().getAsJsonObject();
                // Fields
                String name = id.getPath();
                if (mbInfo.has("name"))
                    name = mbInfo.get("name").getAsString();
                LocalBlockPos masterPos = parsePos(mbInfo.getAsJsonArray("master"));
                VoxelShape fullShape = Shapes.empty();
                // Parse Voxel Shape
                JsonArray array = entry.getValue().getAsJsonObject().getAsJsonArray("shape");
                for (JsonElement boxElement : array) {
                    JsonObject box = boxElement.getAsJsonObject();
                    Vec3 from = parseVec3(box.getAsJsonArray("from"));
                    Vec3 to   = parseVec3(box.getAsJsonArray("to"));

                    VoxelShape boxShape = Block.box(
                            from.x, from.y, from.z,
                            to.x, to.y, to.z
                    );
                    fullShape = Shapes.or(fullShape, boxShape);
                }

                MultiBlockType type = MultiBlockType.register(name, fullShape, masterPos);
                NFPractice.LOGGER.info("\n {}", type);
            }
        }

        private static Vec3 parseVec3(JsonArray arr) {
            return new Vec3(
                    arr.get(0).getAsInt(),
                    arr.get(1).getAsInt(),
                    arr.get(2).getAsInt()
            );
        }

        private static LocalBlockPos parsePos(JsonArray arr) {
            return new LocalBlockPos(
                    arr.get(0).getAsInt(),
                    arr.get(1).getAsInt(),
                    arr.get(2).getAsInt()
            );
        }
    }
}
