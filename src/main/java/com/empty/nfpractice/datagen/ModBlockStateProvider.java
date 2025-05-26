package com.empty.nfpractice.datagen;

import com.empty.nfpractice.NFPractice;
import com.empty.nfpractice.init.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, NFPractice.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        topBottomBlockWithItem(ModBlocks.SUPER_CREDIT_PILE);
        blockWithItem(ModBlocks.CATERIUM_ORE);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(),
                cubeAll(deferredBlock.get()));
    }

    private void topBottomBlockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(),
                models().cubeBottomTop(
                        ModBlocks.SUPER_CREDIT_PILE.getId().getPath(),
                        modLoc("block/super_credit_pile_side"),
                        modLoc("block/super_credit_pile_bottom"),
                        modLoc("block/super_credit_pile_top")
                )
        );
    }
}
