package me.stozeks.anarchyruleengine.condition;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.service.CooldownService;

import java.util.Objects;

public final class CooldownCondition implements RuleCondition {

    private final CooldownService cooldownService;
    private final String cooldownId;

    public CooldownCondition(
            CooldownService cooldownService,
            String cooldownId
    ) {
        this.cooldownService = Objects.requireNonNull(
                cooldownService,
                "cooldownService"
        );

        this.cooldownId = Objects.requireNonNull(
                cooldownId,
                "cooldownId"
        );
    }

    @Override
    public boolean matches(InteractionContext context) {
        return !cooldownService.isOnCooldown(
                context.getPlayer().getUniqueId(),
                cooldownId
        );
    }
}