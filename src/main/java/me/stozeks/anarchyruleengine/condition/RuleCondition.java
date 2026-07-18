package me.stozeks.anarchyruleengine.condition;

import me.stozeks.anarchyruleengine.model.InteractionContext;

public interface RuleCondition {

    boolean matches(InteractionContext context);
}