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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;



public class CraftBench extends BaseEntityBlock {
    public static final MapCodec<CraftBench> CODEC = simpleCodec(CraftBench::new);
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 3, 16);


    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);


    public CraftBench() {
        super(BlockBehaviour.Properties.of().strength(1.0f).noOcclusion());
    }

    public CraftBench(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, Part.LEFT));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction dir = ctx.getHorizontalDirection().getOpposite();
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();

        // This block is the LEFT part. The RIGHT part will go clockwise from it.
        BlockPos secondPos = pos.relative(dir.getClockWise());

        // Check if both positions are replaceable
        BlockState secondState = level.getBlockState(secondPos);
        BlockState firstState = level.getBlockState(pos);

        if (!firstState.canBeReplaced(ctx) || !secondState.canBeReplaced(ctx)) {
            return null; // Cancel placement
        }

        return this.defaultBlockState().setValue(FACING, dir).setValue(PART, Part.LEFT);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        Direction dir = state.getValue(FACING);
        BlockPos offset = pos.relative(dir.getClockWise());

        level.setBlock(offset, state.setValue(PART, Part.RIGHT), 3);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            Direction dir = state.getValue(FACING);
            Part part = state.getValue(PART);

            BlockPos other = (part == Part.LEFT)
                    ? pos.relative(dir.getClockWise())
                    : pos.relative(dir.getCounterClockWise());

            BlockState otherState = level.getBlockState(other);
            if (otherState.getBlock() == this) {
                level.removeBlock(other, false);
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                be.setRemoved();
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
        if (!(world instanceof Level level)) return;

        Direction dir = state.getValue(FACING);
        Part part = state.getValue(PART);
        BlockPos otherPos = (part == Part.LEFT)
                ? pos.relative(dir.getClockWise())
                : pos.relative(dir.getCounterClockWise());

        BlockState otherState = level.getBlockState(otherPos);

        if (otherState.getBlock() != this ||
                otherState.getValue(PART) == part ||
                otherState.getValue(FACING) != dir) {

            // Remove this half if the other half is invalid
            level.destroyBlock(pos, false);
        }
    }




    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return blockState.getValue(PART) == Part.LEFT
                ? new CraftBenchEntity(blockPos, blockState)
                : null;
    }

    public enum Part implements StringRepresentable {
        LEFT, RIGHT;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }
}
