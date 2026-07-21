package me.stozeks.anarchyruleengine.condition;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import org.bukkit.event.block.Action;

public final class InteractionActionCondition implements RuleCondition {

    private final Action expectedAction;

    public InteractionActionCondition(Action expectedAction) {
        this.expectedAction = expectedAction;
    }

    @Override
    public boolean matches(InteractionContext context) {
        return context.getInteractionAction() == expectedAction;
    }
}