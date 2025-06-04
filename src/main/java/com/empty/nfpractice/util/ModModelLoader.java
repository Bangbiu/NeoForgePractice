package com.empty.nfpractice.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ModModelLoader implements IGeometryLoader<CustomModelGeometry> {
    @Override
    public CustomModelGeometry read(JsonObject json, JsonDeserializationContext context) {
        String modelPath = json.has("model") ? json.get("model").getAsString() : "missing";
        return new CustomModelGeometry(ResourceLocation.fromNamespaceAndPath("nfpractice", "models/block/" + modelPath));
    }
}


class CustomModelGeometry implements IUnbakedGeometry<CustomModelGeometry> {
    private final ResourceLocation modelLocation;

    public CustomModelGeometry(ResourceLocation modelLocation) {
        this.modelLocation = modelLocation;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        return new CustomBakedModel();
    }
}

class CustomBakedModel implements BakedModel {

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
        // Return baked geometry here
        return Collections.emptyList(); // You can use OBJ parser to return actual quads
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return null;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}

