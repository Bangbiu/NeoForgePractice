package com.empty.nfpractice.init;

import com.empty.nfpractice.NFPractice;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NFPractice.MOD_ID);
        public static final DeferredItem<Item> MEDAL = ITEMS.register("medal",
            () -> new Item(new Item.Properties().stacksTo(50)));

    public static final DeferredItem<Item> SUPER_CREDIT = ITEMS.register("super_credit",
            () -> new Item(new Item.Properties().stacksTo(50)));

    public static final DeferredItem<Item> RAW_CATERIUM = ITEMS.register("raw_caterium",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CATERIUM_INGOT = ITEMS.register("caterium_ingot",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}