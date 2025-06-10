package com.empty.nfpractice.block.entity;

import com.empty.nfpractice.block.multiblock.MultiBlockType;
import com.empty.nfpractice.init.ModBlockEntities;
import com.empty.nfpractice.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
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

    public VoxelShape getDummyShape(BlockPos pos) {
        BlockPos origin = getBlockPos();
        BlockPos localPos = pos.subtract(origin);
        return this.TYPE.SHAPES.apply(localPos);
    }

    public void createStructure() {
        BlockPos origin = getBlockPos();
        TYPE.eachOccupiedFromMaster((localCurPos) -> {
            // Calculate World Position
            BlockPos worldCurPos = origin.offset(localCurPos);
            if (worldCurPos.equals(origin)) {
                // At Master Position
                return true;
            }
            // Place Dummy
            level.setBlock(worldCurPos, ModBlocks.MULTIBLOCK_DUMMY.get().defaultBlockState(), 3);
            BlockEntity be = level.getBlockEntity(worldCurPos);
            if (be instanceof MultiBlockDummyEntity dummy) {
                dummy.setMasterPos(origin);
            }
            return true;
        });
    }

    public void removeStructure() {
        BlockPos origin = getBlockPos();
        TYPE.eachOccupiedFromMaster((localCurPos) -> {
            // Calculate World Position
            BlockPos worldCurPos = origin.offset(localCurPos);
            if (worldCurPos.equals(origin)) {
                // At Master Position
                return true;
            }
            // Remove Dummy
            if (level.getBlockState(worldCurPos).getBlock() == ModBlocks.MULTIBLOCK_DUMMY.get()) {
                level.removeBlock(worldCurPos, false);
            }
            return true;
        });
        // Remove Master
        level.removeBlock(origin, false);
    }
}