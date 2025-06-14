package com.empty.nfpractice.init;

import com.empty.nfpractice.NFPractice;
import com.empty.nfpractice.block.multiblock.MultiBlockDummyBlock;
import com.empty.nfpractice.block.multiblock.MultiBlockMasterBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMultiBlock {

    public static final DeferredRegister.Blocks MULTIBLOCKS =
            DeferredRegister.createBlocks(NFPractice.MOD_ID);

    public static final DeferredBlock<Block> MULTIBLOCK_DUMMY = registerBlock("multiblock_dummy",
            () -> new MultiBlockDummyBlock());

    public static final DeferredBlock<MultiBlockMasterBlock> MULTIBLOCK_MASTER = registerMultiBlock("multiblock_master");

    private static DeferredBlock<MultiBlockMasterBlock> registerMultiBlock(String name) {
        return registerBlock(name, () -> new MultiBlockMasterBlock(name));
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
