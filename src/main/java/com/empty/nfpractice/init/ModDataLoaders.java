package com.empty.nfpractice.init;

import com.empty.nfpractice.NFPractice;
import com.empty.nfpractice.block.multiblock.LocalBlockPos;
import com.empty.nfpractice.block.multiblock.MultiBlockType;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

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
            MultiBlockType.TYPES.clear();
            // Put Default
            MultiBlockType.TYPES.put(MultiBlockType.DEFAULT_ID, MultiBlockType.DEFAULT);
            // Parsing JSON
            for (Map.Entry<ResourceLocation, JsonElement> entry : objectMap.entrySet()) {
                ResourceLocation id = entry.getKey();
                NFPractice.LOGGER.info("\n Loading {}\n", id.getPath());
                JsonObject mbInfo = entry.getValue().getAsJsonObject();
                // Fields
                String name = mbInfo.get("name").getAsString();
                LocalBlockPos masterPos = parsePos(mbInfo.getAsJsonArray("master"));


//                if (true) continue;
//
//
//                JsonArray array = entry.getValue().getAsJsonObject().getAsJsonArray("shape");
//
//                VoxelShape shape = Shapes.empty();
//                for (JsonElement boxElement : array) {
//                    JsonObject box = boxElement.getAsJsonObject();
//                    Vec3 from = parseVec3(box.getAsJsonArray("from"));
//                    Vec3 to   = parseVec3(box.getAsJsonArray("to"));
//
//                    AABB aabb = new AABB(from.x / 16, from.y / 16, from.z / 16,
//                            to.x / 16,   to.y / 16,   to.z / 16);
//                    shape = Shapes.or(shape, Shapes.create(aabb));
//                }
            }
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
