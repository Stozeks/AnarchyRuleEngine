package me.stozeks.anarchyruleengine.action;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.model.RuleExecutionResult;

public final class CancelAction implements RuleAction {

    @Override
    public void execute(
            InteractionContext context,
            RuleExecutionResult result
    ) {
        result.setCancelled(true);
    }
}