package com.empty.nfpractice.block.entity;

import com.empty.nfpractice.block.multiblock.LocalBlockPos;
import com.empty.nfpractice.block.multiblock.MultiBlockMasterBlock;
import com.empty.nfpractice.block.multiblock.MultiBlockType;
import com.empty.nfpractice.init.ModBlockEntities;
import com.empty.nfpractice.init.ModMultiBlock;
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
    private String TYPE_ID;

    public MultiBlockMasterEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MULTIBLOCK_MASTER_BE.get(), pos, blockState);
        this.TYPE_ID = MultiBlockType.DEFAULT_ID;
    }

    public String getTypeID() {
        return TYPE_ID;
    }

    public void setTypeID(String TYPE_ID) {
        this.TYPE_ID = TYPE_ID;
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

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (TYPE_ID != null) {
            tag.putString("type_id", this.TYPE_ID);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("type_id")) {
            this.TYPE_ID = tag.getString("type_id");
        }
    }

    public BlockPos getWorldPosFromMaster(BlockPos masterWorldPos, LocalBlockPos localPos) {
        Direction dir = this.getBlockState().getValue(MultiBlockMasterBlock.FACING);
        return this.getMultiBlockType().getWorldPosFromMaster(masterWorldPos, localPos, dir);
    }

    @NotNull
    public VoxelShape getBlockShape(LocalBlockPos localPos) {
        return this.getMultiBlockType().SHAPES.shapeAt(localPos);
    }

    public MultiBlockType getMultiBlockType() {
        return MultiBlockType.TYPES.getOrDefault(this.TYPE_ID, MultiBlockType.DEFAULT);
    }

    public void createStructure() {
        BlockPos masterWorldPos = getBlockPos();
        MultiBlockType type = this.getMultiBlockType();
        for (LocalBlockPos localCurPos : type.SHAPES) {
            BlockPos worldCurPos = this.getWorldPosFromMaster(masterWorldPos, localCurPos);
            if (worldCurPos.equals(masterWorldPos)) {
                // At Master Position -> Skip
                continue;
            }
            // Place Dummy
            level.setBlock(worldCurPos, ModMultiBlock.MULTIBLOCK_DUMMY.get().defaultBlockState(), 3);
            BlockEntity be = level.getBlockEntity(worldCurPos);
            if (be instanceof MultiBlockDummyEntity dummy) {
                dummy.config(masterWorldPos, localCurPos);
            }
        }
    }

    public void removeStructure() {
        BlockPos masterWorldPos = this.getBlockPos();
        MultiBlockType type = this.getMultiBlockType();
        for (LocalBlockPos localCurPos : type.SHAPES) {
            BlockPos worldCurPos = this.getWorldPosFromMaster(masterWorldPos, localCurPos);
            if (worldCurPos.equals(masterWorldPos)) {
                // At Master Position -> Skip
                continue;
            }

            // Remove Dummy
            if (level.getBlockState(worldCurPos).getBlock() == ModMultiBlock.MULTIBLOCK_DUMMY.get()) {
                level.removeBlock(worldCurPos, false);
            }
        };
        // Remove Master
        level.removeBlock(masterWorldPos, false);
    }

}