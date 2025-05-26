package com.empty.nfpractice.datagen;

import com.empty.nfpractice.NFPractice;
import com.empty.nfpractice.init.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NFPractice.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.SUPER_CREDIT.get());
        basicItem(ModItems.MEDAL.get());

        basicItem(ModItems.CATERIUM_INGOT.get());
        basicItem(ModItems.RAW_CATERIUM.get());
    }
}
