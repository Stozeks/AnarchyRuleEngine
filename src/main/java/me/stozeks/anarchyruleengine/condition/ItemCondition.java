package me.stozeks.anarchyruleengine.condition;

import me.stozeks.anarchyruleengine.item.ItemService;
import me.stozeks.anarchyruleengine.model.InteractionContext;

import java.util.Objects;

public final class ItemCondition implements RuleCondition {

    private final ItemService itemService;
    private final String expectedItemId;

    public ItemCondition(
            ItemService itemService,
            String expectedItemId
    ) {
        this.itemService = Objects.requireNonNull(
                itemService,
                "itemService"
        );

        this.expectedItemId = Objects.requireNonNull(
                expectedItemId,
                "expectedItemId"
        );
    }

    @Override
    public boolean matches(InteractionContext context) {
        String actualItemId = itemService.getCustomItemId(
                context.getItem()
        );

        return expectedItemId.equals(actualItemId);
    }
}