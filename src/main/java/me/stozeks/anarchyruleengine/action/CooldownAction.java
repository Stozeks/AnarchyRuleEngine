package me.stozeks.anarchyruleengine.action;

import me.stozeks.anarchyruleengine.item.ItemService;
import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.model.RuleExecutionResult;
import me.stozeks.anarchyruleengine.service.CooldownService;

import java.util.Objects;

public final class CooldownAction implements RuleAction {

    private final CooldownService cooldownService;
    private final ItemService itemService;
    private final String cooldownId;
    private final long durationMillis;

    public CooldownAction(
            CooldownService cooldownService,
            ItemService itemService,
            String cooldownId,
            long durationSeconds
    ) {
        this.cooldownService = Objects.requireNonNull(
                cooldownService,
                "cooldownService"
        );

        this.itemService = Objects.requireNonNull(
                itemService,
                "itemService"
        );

        this.cooldownId = cooldownId;

        if (durationSeconds <= 0) {
            throw new IllegalArgumentException(
                    "Cooldown duration must be greater than zero."
            );
        }

        try {
            this.durationMillis = Math.multiplyExact(
                    durationSeconds,
                    1000L
            );
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "Cooldown duration is too large.",
                    exception
            );
        }
    }

    @Override
    public void execute(
            InteractionContext context,
            RuleExecutionResult result
    ) {

        String id = cooldownId;

        if (id == null) {
            id = itemService.getCustomItemId(
                    context.getItem()
            );

            if (id == null || id.isEmpty()) {
                return;
            }
        }

        cooldownService.startCooldown(
                context.getPlayer().getUniqueId(),
                id,
                durationMillis
        );
    }
}