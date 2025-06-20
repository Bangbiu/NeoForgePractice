package com.empty.nfpractice.init;

import com.empty.nfpractice.NFPractice;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NFPractice.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TABS = TABS.register(NFPractice.MOD_ID, () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group." + NFPractice.MOD_ID))
            .icon(() -> ModItems.MEDAL.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.MEDAL);
                output.accept(ModItems.SUPER_CREDIT);
                output.accept(ModBlocks.SUPER_CREDIT_PILE);

                output.accept(ModBlocks.CATERIUM_ORE);
                output.accept(ModItems.RAW_CATERIUM);
                output.accept(ModItems.CATERIUM_INGOT);

                output.accept(ModBlocks.CRAFT_BENCH);
                output.accept(ModBlocks.PEDESTAL);

                output.accept(ModMultiBlock.MULTIBLOCK_MASTER);
                output.accept(ModMultiBlock.TEST_MULTIBLOCK);
            }).build());

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }

}
