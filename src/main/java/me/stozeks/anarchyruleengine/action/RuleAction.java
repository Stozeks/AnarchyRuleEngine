package me.stozeks.anarchyruleengine.action;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.model.RuleExecutionResult;

public interface RuleAction {

    void execute(
            InteractionContext context,
            RuleExecutionResult result
    );
}