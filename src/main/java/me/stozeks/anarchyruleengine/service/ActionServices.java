package me.stozeks.anarchyruleengine.service;

import me.stozeks.anarchyruleengine.item.ItemService;
import me.stozeks.anarchyruleengine.placeholder.PlaceholderService;

import java.util.Objects;

public final class ActionServices {

    private final ItemService itemService;
    private final CooldownService cooldownService;
    private final PlaceholderService placeholderService;

    public ActionServices(
            ItemService itemService,
            CooldownService cooldownService,
            PlaceholderService placeholderService
    ) {
        this.itemService = Objects.requireNonNull(
                itemService,
                "itemService"
        );

        this.cooldownService = Objects.requireNonNull(
                cooldownService,
                "cooldownService"
        );

        this.placeholderService = Objects.requireNonNull(
                placeholderService,
                "placeholderService"
        );
    }

    public ItemService getItemService() {
        return itemService;
    }

    public CooldownService getCooldownService() {
        return cooldownService;
    }

    public PlaceholderService getPlaceholderService() {
        return placeholderService;
    }
}