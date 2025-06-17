package com.empty.nfpractice.block.entity;

import com.empty.nfpractice.NFPractice;
import com.empty.nfpractice.block.multiblock.MultiBlockPartBlock;
import com.empty.nfpractice.util.LocalBlockPos;
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

import java.util.function.BiFunction;

public class MultiBlockMasterEntity extends BlockEntity {
    private String TYPE_ID;

    public MultiBlockMasterEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MULTIBLOCK_MASTER_BE.get(),pos, blockState);
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
            this.setTypeID(tag.getString("type_id"));
        }
    }

    public @NotNull VoxelShape getBlockShape(LocalBlockPos localPos) {
        Direction dir = this.getBlockState().getValue(MultiBlockMasterBlock.FACING);
        return this.getMultiBlockType().getRotatedShapeAt(localPos, dir);
    }

    public MultiBlockType getMultiBlockType() {
        return MultiBlockType.request(this.TYPE_ID);
    }

    public boolean forEachPartPos(BiFunction<LocalBlockPos, BlockPos, Boolean> comsumer) {
        Direction dir = this.getBlockState().getValue(MultiBlockMasterBlock.FACING);
        BlockPos masterWorldPos = getBlockPos();
        MultiBlockType type = this.getMultiBlockType();
        for (LocalBlockPos localCurPos : type.allLocalPosFacing(dir)) {
            if (localCurPos.equals(BlockPos.ZERO))
                continue;
            BlockPos worldCurPos = masterWorldPos.offset(localCurPos);
            if(!comsumer.apply(localCurPos, worldCurPos))
                return false;
        }
        return true;
    }

    public void createStructure() {
        BlockPos masterWorldPos = getBlockPos();
        this.forEachPartPos(
                (localPos, worldPos) -> {

                    // Place Part
                    level.setBlock(worldPos, ModMultiBlock.MULTIBLOCK_DUMMY.get().defaultBlockState(), 3);
                    BlockEntity be = level.getBlockEntity(worldPos);

                    if (be instanceof MultiBlockPartEntity dummy) {
                        dummy.config(masterWorldPos, localPos);
                    }
                    return true;
                }
        );
    }

    public void removeStructure() {
        BlockPos masterWorldPos = getBlockPos();
        this.forEachPartPos(
                (localPos, worldPos) -> {
                    // Remove Dummy
                    if (level.getBlockState(worldPos).getBlock() == ModMultiBlock.MULTIBLOCK_DUMMY.get()) {
                        level.removeBlock(worldPos, false);
                    }
                    return true;
                }
        );
        // Remove Master
        level.removeBlock(masterWorldPos, false);
    }
}