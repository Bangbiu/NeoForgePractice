package com.empty.nfpractice.block;

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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class MultiBlockBlock extends BaseEntityBlock {
    public static final MapCodec<MultiBlockBlock> CODEC = simpleCodec(MultiBlockBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty OFFSET_X = IntegerProperty.create("offset_x_local", 0, 118);
    public static final IntegerProperty OFFSET_Y = IntegerProperty.create("offset_y_local", 0, 118);
    public static final IntegerProperty OFFSET_Z = IntegerProperty.create("offset_z_local", 0, 118);

    public static BlockPos MASTER_OFFSET = new BlockPos(0, 0, 0);
    public static LocalBound BOUND = new LocalBound(2, 2, 2);
    public static StructShapes SHAPES = new StructShapes();

    public static BlockPos getStateLocalPos(BlockState state) {
        return new BlockPos(
                state.getValue(OFFSET_X),
                state.getValue(OFFSET_Y),
                state.getValue(OFFSET_Z)
                );
    }

    public static BlockState setStateLocalPos(BlockState state, BlockPos localPos) {
        return state.setValue(OFFSET_X, localPos.getX())
                .setValue(OFFSET_Y, localPos.getY())
                .setValue(OFFSET_Z, localPos.getZ());
    }

    public static boolean isMasterBlock(BlockState state) {
        return MultiBlockBlock.getStateLocalPos(state).equals(MASTER_OFFSET);
    }

    public MultiBlockBlock() {
        this(BlockBehaviour.Properties.of().strength(0.5f).noOcclusion());
    }

    public MultiBlockBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getDefaultBlockState());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockPos structPos = MultiBlockBlock.getStateLocalPos(state);
        return SHAPES.apply(structPos);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction dir = ctx.getHorizontalDirection().getOpposite();
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();

        // This block is Master Block at MASTER OFFSET
        boolean placable = SHAPES.eachOccupied((localCurPos) -> {
            // Calculate World Position
            BlockPos worldCurPos = pos.offset(localCurPos.subtract(MASTER_OFFSET));
            // Check if all block pos is avaliable
            return level.getBlockState(worldCurPos).canBeReplaced();
        });

        if (placable) {
            return this.getMasterBlockState(dir);
        }

        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        Direction dir = state.getValue(FACING);
        // Only Master Block Can be Placed by Player
        SHAPES.eachOccupied((localCurPos) -> {
            // Calculate World Position
            BlockPos worldCurPos = pos.offset(localCurPos.subtract(MASTER_OFFSET));
            if (localCurPos.equals(MASTER_OFFSET)) {
                return true;
            }
            // Place Dummy
            level.setBlock(worldCurPos, MultiBlockBlock.setStateLocalPos(state, localCurPos), 3);
            return true;
        });
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            Direction dir = state.getValue(FACING);
            BlockPos thislocalPos = getStateLocalPos(state);

            SHAPES.eachOccupied((localCurPos) -> {
                // Calculate World Position
                BlockPos worldCurPos = pos.offset(localCurPos.subtract(thislocalPos));
                // Check if other block is in MultiBlock
                BlockState curState = level.getBlockState(worldCurPos);
                if (curState.getBlock() == this) {
                    level.removeBlock(worldCurPos, false);
                    if (localCurPos.equals(MASTER_OFFSET)) {
                        BlockEntity be = level.getBlockEntity(worldCurPos);
                        if (be != null) {
                            be.setRemoved();
                        }
                    }

                }
                return true;
            });
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OFFSET_X, OFFSET_Y, OFFSET_Z);
    }

    public BlockState getDefaultBlockState() {
        return this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(OFFSET_X, 0)
                .setValue(OFFSET_Y, 0)
                .setValue(OFFSET_Z, 0);
    }

    public BlockState getMasterBlockState(Direction facing) {
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(OFFSET_X, MASTER_OFFSET.getX())
                .setValue(OFFSET_Y, MASTER_OFFSET.getY())
                .setValue(OFFSET_Z, MASTER_OFFSET.getZ());
    }

    public static class LocalBound extends BoundingBox {
        public LocalBound() {
            this(1, 1, 1);
        }

        public LocalBound(BlockPos pos) {
            this(pos.getX(), pos.getY(), pos.getZ());
        }

        public LocalBound(int maxX, int maxY, int maxZ) {
            super(0, 0, 0, maxX, maxY, maxZ);
        }

        @Override
        public int minX() {
            return 0;
        }

        @Override
        public int minY() {
            return 0;
        }

        @Override
        public int minZ() {
            return 0;
        }
    }

    public static class StructShapes implements Function<BlockPos, VoxelShape> {
        public static VoxelShape FULL_BLOCK = Block.box(0, 0, 0, 16, 16, 16);
        @Override
        public VoxelShape apply(BlockPos blockPos) {
            if (!this.occupied(blockPos)) {
                return null;
            }
            return FULL_BLOCK;
        }

        @NotNull
        public boolean occupied(BlockPos blockPos) {
            if (!BOUND.isInside(blockPos)) {
                return false;
            }
            // Rules
            return true;
        }

        @NotNull
        public boolean eachOccupied(Function<BlockPos, Boolean> func) {
            for (int x = 0; x < BOUND.maxX(); x++) {
                for (int y = 0; y < BOUND.maxY(); y++) {
                    for (int z = 0; z < BOUND.maxZ(); z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (this.occupied(pos) ) {
                            if (!func.apply(pos)) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
    }
}
