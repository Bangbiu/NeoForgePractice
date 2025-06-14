package com.empty.nfpractice.init;

import com.empty.nfpractice.NFPractice;
import com.empty.nfpractice.block.multiblock.MultiBlockDummyBlock;
import com.empty.nfpractice.block.multiblock.MultiBlockMasterBlock;
import com.empty.nfpractice.block.multiblock.MultiBlockType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModMultiBlock {
    public static final Map<ResourceLocation, MultiBlockType> TYPES =
            Map.of(NFPractice.of("multiblock_master"), MultiBlockType.DEFAULT);

    public static final DeferredRegister.Blocks MULTIBLOCKS =
            DeferredRegister.createBlocks(NFPractice.MOD_ID);

    public static final DeferredBlock<Block> MULTIBLOCK_DUMMY = registerBlock("multiblock_dummy",
            () -> new MultiBlockDummyBlock());

    public static final DeferredBlock<MultiBlockMasterBlock> MULTIBLOCK_MASTER = registerMultiBlock("multiblock_master");

    public static Supplier<MultiBlockType> typeSupplier(String name) {
        return () -> (TYPES.getOrDefault(NFPractice.of(name), MultiBlockType.DEFAULT));
    }

    private static DeferredBlock<MultiBlockMasterBlock> registerMultiBlock(String name) {
        return registerBlock(name, () -> new MultiBlockMasterBlock(typeSupplier(name)));
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = MULTIBLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        MULTIBLOCKS.register(eventBus);
    }
}
