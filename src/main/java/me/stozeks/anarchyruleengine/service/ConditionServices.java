package me.stozeks.anarchyruleengine.service;

import me.stozeks.anarchyruleengine.item.ItemService;

import java.util.Objects;

public final class ConditionServices {

    private final ItemService itemService;

    public ConditionServices(ItemService itemService) {
        this.itemService = Objects.requireNonNull(
                itemService,
                "itemService"
        );
    }

    public ItemService getItemService() {
        return itemService;
    }
}