package com.empty.nfpractice.block.entity;

import com.empty.nfpractice.util.LocalBlockPos;
import com.empty.nfpractice.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MultiBlockDummyEntity extends BlockEntity {
    private BlockPos masterPos;
    private LocalBlockPos localPos;

    public MultiBlockDummyEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MULTIBLOCK_DUMMY_BE.get(), pos, blockState);
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

    public BlockPos getMasterPos() {
        return masterPos;
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos = masterPos;
    }

    public BlockPos getLocalPos() {
        return localPos;
    }

    public void setLocalPos(LocalBlockPos localPos) {
        this.localPos = localPos;
    }

    public void config(BlockPos masterPos, LocalBlockPos localPos) {
        this.localPos = localPos;
        this.masterPos = masterPos;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (masterPos != null) {
            if (level.getBlockEntity(this.masterPos) instanceof MultiBlockMasterEntity entity) {
                return entity.getBlockShape(this.localPos);
            }
        }

        return Shapes.block();
    }

    public void removeStructure() {
        if (masterPos != null) {
            if (level.getBlockEntity(masterPos) instanceof MultiBlockMasterEntity entity) {
                entity.removeStructure();
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (masterPos != null) {
            tag.putLong("MasterPos", masterPos.asLong());
        }

        if (localPos != null) {
            tag.putLong("LocalPos", localPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("MasterPos")) {
            masterPos = BlockPos.of(tag.getLong("MasterPos"));
        }
        if (tag.contains("LocalPos")) {
            localPos = LocalBlockPos.of(BlockPos.of(tag.getLong("LocalPos")));
        }
    }
}