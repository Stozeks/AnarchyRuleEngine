package me.stozeks.anarchyruleengine.condition;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import org.bukkit.event.block.Action;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public final class InteractionActionCondition
        implements RuleCondition {

    private final Set<Action> allowedActions;

    public InteractionActionCondition(
            Set<Action> allowedActions
    ) {
        Objects.requireNonNull(
                allowedActions,
                "allowedActions"
        );

        if (allowedActions.isEmpty()) {
            throw new IllegalArgumentException(
                    "Allowed interaction actions cannot be empty."
            );
        }

        this.allowedActions = Collections.unmodifiableSet(
                EnumSet.copyOf(allowedActions)
        );
    }

    @Override
    public boolean matches(InteractionContext context) {
        return allowedActions.contains(
                context.getInteractionAction()
        );
    }
}