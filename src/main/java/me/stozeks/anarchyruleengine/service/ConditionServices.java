package me.stozeks.anarchyruleengine.service;

import me.stozeks.anarchyruleengine.item.ItemService;

import java.util.Objects;

public final class ConditionServices {

    private final ItemService itemService;
    private final CooldownService cooldownService;

    public ConditionServices(
            ItemService itemService,
            CooldownService cooldownService
    ) {
        this.itemService = Objects.requireNonNull(
                itemService,
                "itemService"
        );

        this.cooldownService = Objects.requireNonNull(
                cooldownService,
                "cooldownService"
        );
    }

    public ItemService getItemService() {
        return itemService;
    }

    public CooldownService getCooldownService() {
        return cooldownService;
    }
}