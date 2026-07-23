package me.stozeks.anarchyruleengine.placeholder;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.service.CooldownService;

import java.util.Locale;
import java.util.Objects;

public final class CooldownPlaceholderResolver
        implements PlaceholderResolver {

    private static final String PREFIX = "cooldown:";

    private final CooldownService cooldownService;

    public CooldownPlaceholderResolver(
            CooldownService cooldownService
    ) {
        this.cooldownService = Objects.requireNonNull(
                cooldownService,
                "cooldownService"
        );
    }

    @Override
    public boolean supports(String placeholder) {
        if (placeholder == null) {
            return false;
        }

        return placeholder
                .toLowerCase(Locale.ROOT)
                .startsWith(PREFIX);
    }

    @Override
    public String resolve(
            String placeholder,
            InteractionContext context
    ) {
        String cooldownId =
                placeholder.substring(PREFIX.length()).trim();

        if (cooldownId.isEmpty()) {
            return "0";
        }

        long remainingSeconds =
                cooldownService.getRemainingSeconds(
                        context.getPlayer().getUniqueId(),
                        cooldownId
                );

        return Long.toString(remainingSeconds);
    }
}