package com.empty.nfpractice.recipe;
import com.empty.nfpractice.init.ModRecipeTypes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class MultipleInputRecipe implements Recipe<RecipeInput> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final Ingredient input;
    private final int count;

    public MultipleInputRecipe(ResourceLocation id, Ingredient input, int count, ItemStack output) {
        this.id = id;
        this.input = input;
        this.count = count;
        this.output = output;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        int found = 0;

        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (input.test(stack)) {
                found += stack.getCount();
            }
        }
        return found >= count;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output;
    }

    public ResourceLocation getId() {
        return id;
    }

    public Ingredient getIngredient() {
        return this.input;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public int getRequiredCount() {
        return this.count;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.MULT_INPUT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.MULT_INPUT_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<MultipleInputRecipe> {

        private static final MapCodec<MultipleInputRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(MultipleInputRecipe::getIngredient),
                ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(MultipleInputRecipe::getRequiredCount),
                ItemStack.CODEC.fieldOf("result").forGetter(MultipleInputRecipe::getOutput)
        ).apply(instance, (ingredient, count, output) ->
                new MultipleInputRecipe(null, ingredient, count, output) // ID set later
        ));

        private static final StreamCodec<RegistryFriendlyByteBuf, MultipleInputRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredient());
                            buf.writeVarInt(recipe.getRequiredCount());
                            ItemStack.STREAM_CODEC.encode(buf, recipe.getOutput());
                        },
                        buf -> {
                            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                            int count = buf.readVarInt();
                            ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                            return new MultipleInputRecipe(null, ingredient, count, output); // id is injected later
                        }
                );

        @Override
        public MapCodec<MultipleInputRecipe> codec() {
            return MAP_CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MultipleInputRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

