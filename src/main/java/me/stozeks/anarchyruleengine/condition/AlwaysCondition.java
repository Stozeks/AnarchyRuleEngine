package me.stozeks.anarchyruleengine.condition;

import me.stozeks.anarchyruleengine.model.InteractionContext;

public final class AlwaysCondition implements RuleCondition {

    @Override
    public boolean matches(InteractionContext context) {
        return true;
    }
}