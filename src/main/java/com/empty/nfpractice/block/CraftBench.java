package com.empty.nfpractice.block;

import com.empty.nfpractice.NFPractice;
import com.empty.nfpractice.block.entity.CraftBenchEntity;
import com.empty.nfpractice.init.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;



public class CraftBench extends MultiBlockBlock {
    public static final MapCodec<CraftBench> CODEC = simpleCodec(CraftBench::new);
    public static BlockPos MASTER_OFFSET = new BlockPos(0, 0, 0);
    public static LocalBound BOUND = new LocalBound(1, 1, 2);


    public CraftBench() {
        super();
    }

    public CraftBench(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return MultiBlockBlock.isMasterBlock(blockState)
                ? new CraftBenchEntity(blockPos, blockState)
                : null;
    }
}
