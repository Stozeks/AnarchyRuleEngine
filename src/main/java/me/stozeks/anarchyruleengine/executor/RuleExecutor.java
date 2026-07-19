package me.stozeks.anarchyruleengine.executor;

import me.stozeks.anarchyruleengine.action.RuleAction;
import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.model.Rule;
import me.stozeks.anarchyruleengine.model.RuleExecutionResult;

public final class RuleExecutor {

    /**
     * Проверяет и выполняет одно правило.
     *
     * @return true, если после выполнения нужно прекратить
     * дальнейшую обработку правил.
     */
    public boolean executeIfMatches(
            Rule rule,
            InteractionContext context,
            RuleExecutionResult result
    ) {
        if (!rule.isEnabled()) {
            return false;
        }

        if (!rule.matches(context)) {
            return false;
        }

        result.setMatched(true);
        result.setMatchedRuleId(rule.getId());

        executeActions(rule, context, result);

        if (rule.shouldStopProcessing()) {
            result.setStopProcessing(true);
            return true;
        }

        return false;
    }

    private void executeActions(
            Rule rule,
            InteractionContext context,
            RuleExecutionResult result
    ) {
        for (RuleAction action : rule.getActions()) {
            action.execute(context, result);
            result.incrementExecutedActions();
        }
    }
}