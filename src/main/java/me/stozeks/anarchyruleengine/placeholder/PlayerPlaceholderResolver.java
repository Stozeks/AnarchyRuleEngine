package me.stozeks.anarchyruleengine.placeholder;

import me.stozeks.anarchyruleengine.model.InteractionContext;

public final class PlayerPlaceholderResolver
        implements PlaceholderResolver {

    @Override
    public boolean supports(String placeholder) {
        return "player".equalsIgnoreCase(placeholder);
    }

    @Override
    public String resolve(
            String placeholder,
            InteractionContext context
    ) {
        return context.getPlayer().getName();
    }
}