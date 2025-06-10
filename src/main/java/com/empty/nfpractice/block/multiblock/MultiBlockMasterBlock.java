package com.empty.nfpractice.block.multiblock;

import com.empty.nfpractice.block.entity.MultiBlockDummyEntity;
import com.empty.nfpractice.block.entity.MultiBlockMasterEntity;
import com.empty.nfpractice.init.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class MultiBlockMasterBlock extends BaseEntityBlock {
    private final MultiBlockType TYPE;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public MultiBlockMasterBlock(MultiBlockType type) {
        this(type, Properties.of().strength(0.5f).noOcclusion());
    }

    public MultiBlockMasterBlock(MultiBlockType type, Properties properties) {
        super(properties);
        this.TYPE = type;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((properties) ->
                (new MultiBlockMasterBlock(this.TYPE)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MultiBlockMasterEntity(pos, state, this.TYPE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction dir = ctx.getHorizontalDirection().getOpposite();
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();

        // This block is Master Block at MASTER OFFSET
        boolean placable = TYPE.eachOccupiedFromMaster((localCurPos) -> {
            // Calculate World Position
            BlockPos worldCurPos = pos.offset(localCurPos);
            // Check if all block pos is avaliable
            return level.getBlockState(worldCurPos).canBeReplaced();
        });

        return placable ? this.getMasterBlockState(dir) : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        Direction dir = state.getValue(FACING);
        // Only Master Block Can be Placed by Player
        if (level.getBlockEntity(pos) instanceof MultiBlockMasterEntity entity) {
            entity.createStructure();
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            Direction dir = state.getValue(FACING);
            if (level.getBlockEntity(pos) instanceof MultiBlockMasterEntity entity) {
                entity.removeStructure();
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
        builder.add(FACING);
    }

    public BlockState getDefaultBlockState() {
        return this.defaultBlockState()
                .setValue(FACING, Direction.NORTH);
    }

    public BlockState getMasterBlockState(Direction facing) {
        return this.defaultBlockState()
                .setValue(FACING, facing);
    }
}
