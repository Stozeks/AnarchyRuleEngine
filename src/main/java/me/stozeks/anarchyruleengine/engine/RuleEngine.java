package me.stozeks.anarchyruleengine.engine;

import me.stozeks.anarchyruleengine.executor.RuleExecutor;
import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.model.Rule;
import me.stozeks.anarchyruleengine.model.RuleExecutionResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class RuleEngine {

    private final RuleExecutor ruleExecutor;
    private final List<Rule> rules;

    public RuleEngine(
            RuleExecutor ruleExecutor,
            List<Rule> rules
    ) {
        this.ruleExecutor = ruleExecutor;
        this.rules = new ArrayList<>(rules);
        sortRulesByPriority();
    }

    public RuleExecutionResult evaluate(InteractionContext context) {
        return evaluate(context, false);
    }

    public RuleExecutionResult evaluate(InteractionContext context, boolean tracingEnabled) {
        RuleExecutionResult result = new RuleExecutionResult(tracingEnabled);

        for (Rule rule : rules) {
            boolean stopProcessing =
                    ruleExecutor.executeIfMatches(
                            rule,
                            context,
                            result
                    );

            if (stopProcessing) {
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

    private void sortRulesByPriority() {
        rules.sort(
                Comparator.comparingInt(Rule::getPriority)
                        .reversed()
        );
    }
}