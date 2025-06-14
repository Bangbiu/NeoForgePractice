package com.empty.nfpractice.init;

import com.empty.nfpractice.NFPractice;
import com.empty.nfpractice.block.entity.CraftBenchEntity;
import com.empty.nfpractice.block.entity.MultiBlockDummyEntity;
import com.empty.nfpractice.block.entity.MultiBlockMasterEntity;
import com.empty.nfpractice.block.entity.PedestalBlockEntity;

import com.empty.nfpractice.block.multiblock.MultiBlockType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, NFPractice.MOD_ID);

    public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL_BE =
            BLOCK_ENTITIES.register("pedestal_be", () -> BlockEntityType.Builder.of(
                    PedestalBlockEntity::new, ModBlocks.PEDESTAL.get()).build(null));

    public static final Supplier<BlockEntityType<CraftBenchEntity>> CRAFT_BENCH_BE =
            BLOCK_ENTITIES.register("craft_bench_be", () -> BlockEntityType.Builder.of(
                    CraftBenchEntity::new, ModBlocks.CRAFT_BENCH.get()).build(null));

    public static final Supplier<BlockEntityType<MultiBlockDummyEntity>> MULTIBLOCK_DUMMY_BE =
            BLOCK_ENTITIES.register("multiblock_dummy_be", () -> BlockEntityType.Builder.of(
                    MultiBlockDummyEntity::new, ModMultiBlock.MULTIBLOCK_DUMMY.get()).build(null));

    public static final Supplier<BlockEntityType<MultiBlockMasterEntity>> MULTIBLOCK_MASTER_BE =
            BLOCK_ENTITIES.register("multiblock_master_be", () -> BlockEntityType.Builder.of(
                    MultiBlockMasterEntity::new,
                    ModMultiBlock.MULTIBLOCK_MASTER.get(),
                    ModMultiBlock.TEST_MULTIBLOCK.get()
            ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
