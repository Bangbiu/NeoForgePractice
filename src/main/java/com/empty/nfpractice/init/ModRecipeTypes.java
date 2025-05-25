package com.empty.nfpractice.init;

import com.empty.nfpractice.NFPractice;
import com.empty.nfpractice.recipe.MultipleInputRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, NFPractice.MOD_ID);

    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, NFPractice.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<MultipleInputRecipe>> MULT_INPUT_TYPE =
            registerType("multiple_input_crafting");
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MultipleInputRecipe>> MULT_INPUT_SERIALIZER =
            RECIPE_SERIALIZERS.register("multiple_input_crafting", MultipleInputRecipe.Serializer::new);

    private static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> registerType(String name) {
        return RECIPE_TYPES.register(name, () -> new RecipeType<>() {
            @Override
            public String toString() {
                return NFPractice.of(name).toString();
            }
        });
    }

    public static void register(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);
    }
}
