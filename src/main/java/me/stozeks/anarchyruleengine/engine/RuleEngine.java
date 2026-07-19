package me.stozeks.anarchyruleengine.engine;

import me.stozeks.anarchyruleengine.action.RuleAction;
import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.model.Rule;
import me.stozeks.anarchyruleengine.model.RuleExecutionResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class RuleEngine {

    private final List<Rule> rules;

    public RuleEngine(List<Rule> rules) {
        this.rules = new ArrayList<>(rules);
        sortRulesByPriority();
    }

    public RuleExecutionResult evaluate(InteractionContext context) {
        RuleExecutionResult result = new RuleExecutionResult();

        for (Rule rule : rules) {
            if (!rule.isEnabled()) {
                continue;
            }

            if (!rule.matches(context)) {
                continue;
            }

            result.setMatched(true);
            result.setMatchedRuleId(rule.getId());

            executeActions(rule, context, result);

            if (rule.shouldStopProcessing()) {
                result.setStopProcessing(true);
                break;
            }
        }

        return result;
    }

    public void replaceRules(List<Rule> newRules) {
        rules.clear();
        rules.addAll(newRules);
        sortRulesByPriority();
    }

    public List<Rule> getRules() {
        return new ArrayList<>(rules);
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

    private void sortRulesByPriority() {
        rules.sort(
                Comparator.comparingInt(Rule::getPriority).reversed()
        );
    }
}