package com.empty.nfpractice.block.entity;

import com.empty.nfpractice.NFPractice;
import com.empty.nfpractice.block.multiblock.MultiBlockMasterBlock;
import com.empty.nfpractice.block.multiblock.MultiBlockType;
import com.empty.nfpractice.init.ModBlockEntities;
import com.empty.nfpractice.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiBlockMasterEntity extends BlockEntity {
    private final MultiBlockType TYPE;

    public MultiBlockMasterEntity(BlockPos pos, BlockState blockState, MultiBlockType type) {
        super(ModBlockEntities.MULTIBLOCK_MASTER_BE.get(), pos, blockState);
        this.TYPE = type;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    public BlockPos turnToMasterDir(BlockPos localPos) {
        Direction dir = this.getBlockState().getValue(MultiBlockMasterBlock.FACING);

        int x = localPos.getX();
        int y = localPos.getY();
        int z = localPos.getZ();

        switch (dir) {
            case EAST:
                return new BlockPos(-z, y, x); // 90 degrees CW
            case SOUTH:
                return new BlockPos(-x, y, -z); // 180 degrees
            case WEST:
                return new BlockPos(z, y, -x); // 270 degrees CW
            default:
                return localPos; // no rotation
        }
    }

    public BlockPos getDummyWorldPosFromMaster(BlockPos masterWorldPos, BlockPos localPos) {
        // Rotated Position According to Structure's FACING
        BlockPos rotatedLocalCurPos = this.turnToMasterDir(localPos);
        BlockPos roatatedMasterOffset = this.turnToMasterDir(TYPE.MASTER_OFFSET);
        // Master World Pos - Master Offset = Structure Origin World Pos
        // Structure Origin World Pos + Dummy Local Pos(localCurPos) = Dummy World Pos
        return masterWorldPos.subtract(roatatedMasterOffset).offset(rotatedLocalCurPos);
    }

    @NotNull
    public VoxelShape getDummyShape(BlockPos localPos) {
        return this.TYPE.SHAPES.apply(localPos);
    }

    public void createStructure() {
        BlockPos masterWorldPos = getBlockPos();
        NFPractice.LOGGER.info("Master At: {}", masterWorldPos);
        TYPE.eachOccupied((localCurPos) -> {
            BlockPos worldCurPos = this.getDummyWorldPosFromMaster(masterWorldPos, localCurPos);
            if (worldCurPos.equals(masterWorldPos)) {
                // At Master Position -> Skip
                return true;
            }
            // Place Dummy
            level.setBlock(worldCurPos, ModBlocks.MULTIBLOCK_DUMMY.get().defaultBlockState(), 3);
            NFPractice.LOGGER.info("Dummy At: {}", worldCurPos);
            BlockEntity be = level.getBlockEntity(worldCurPos);
            if (be instanceof MultiBlockDummyEntity dummy) {
                dummy.config(masterWorldPos, localCurPos);
            }
            return true;
        });
    }

    public void removeStructure() {
        BlockPos masterWorldPos = this.getBlockPos();
        TYPE.eachOccupied((localCurPos) -> {
            BlockPos worldCurPos = this.getDummyWorldPosFromMaster(masterWorldPos, localCurPos);
            if (worldCurPos.equals(masterWorldPos)) {
                // At Master Position -> Skip
                return true;
            }

            // Remove Dummy
            if (level.getBlockState(worldCurPos).getBlock() == ModBlocks.MULTIBLOCK_DUMMY.get()) {
                level.removeBlock(worldCurPos, false);
            }
            return true;
        });
        // Remove Master
        level.removeBlock(masterWorldPos, false);
    }
}