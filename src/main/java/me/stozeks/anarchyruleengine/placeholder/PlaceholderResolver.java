package me.stozeks.anarchyruleengine.placeholder;

import me.stozeks.anarchyruleengine.model.InteractionContext;

public interface PlaceholderResolver {

    boolean supports(String placeholder);

    String resolve(
            String placeholder,
            InteractionContext context
    );
}