package com.empty.nfpractice.block.multiblock;

import com.empty.nfpractice.block.entity.MultiBlockMasterEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MultiBlockMasterBlock extends BaseEntityBlock {
    private final String TYPE_ID;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public MultiBlockMasterBlock(String typeID) {
        this(typeID, Properties.of().strength(0.5f).noOcclusion());
    }

    public MultiBlockMasterBlock(String typeID, Properties properties) {
        super(properties);
        this.TYPE_ID = typeID;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((properties) ->
                (new MultiBlockMasterBlock(this.TYPE_ID)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        MultiBlockMasterEntity be = new MultiBlockMasterEntity(pos, state);
        be.setTypeID(this.TYPE_ID);
        return be;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        MultiBlockType type = this.getType();
        Direction dir = ctx.getHorizontalDirection().getOpposite();
        BlockPos masterWorldPos = ctx.getClickedPos();
        Level level = ctx.getLevel();

        // This block is Master Block at MASTER OFFSET
        for (LocalBlockPos localCurPos : type.shapes) {
            BlockPos worldCurPos = type.getWorldPosFromMaster(masterWorldPos, localCurPos, dir);
            // Check if all block pos is avaliable
            if (!level.getBlockState(worldCurPos).canBeReplaced()) {
                return null;
            }
        }

        return this.getMasterBlockState(dir);
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
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getType().getFullShape();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getType().getMasterShape();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public MultiBlockType getType() {
        return MultiBlockType.request(this.TYPE_ID);
    }

    public BlockState getMasterBlockState(Direction facing) {
        return this.defaultBlockState()
                .setValue(FACING, facing);
    }
}
