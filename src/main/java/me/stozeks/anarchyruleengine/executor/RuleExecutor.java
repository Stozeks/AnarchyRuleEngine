package me.stozeks.anarchyruleengine.executor;

import me.stozeks.anarchyruleengine.action.RuleAction;
import me.stozeks.anarchyruleengine.condition.RuleCondition;
import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.model.Rule;
import me.stozeks.anarchyruleengine.model.RuleExecutionResult;
import me.stozeks.anarchyruleengine.model.RuleTrace;
import me.stozeks.anarchyruleengine.model.RuleTraceStep;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RuleExecutor {

    private final Logger logger;

    public RuleExecutor(Logger logger) {
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    public boolean executeIfMatches(
            Rule rule,
            InteractionContext context,
            RuleExecutionResult result
    ) {
        Objects.requireNonNull(rule, "rule");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(result, "result");

        RuleTrace trace = result.isTracingEnabled()
                ? new RuleTrace(rule.getId(), rule.getPriority())
                : null;

        if (trace != null) {
            result.addRuleTrace(trace);
        }

        if (!rule.isEnabled()) {
            if (trace != null) {
                trace.markSkipped("Rule is disabled.");
            }
            return false;
        }

        if (!matches(rule, context, result, trace)) {
            return result.hasFailed();
        }

        if (trace != null) {
            trace.setMatched(true);
        }
        result.markMatched(rule.getId());

        if (!executeActions(rule, context, result, trace)) {
            return true;
        }

        if (rule.shouldStopProcessing()) {
            result.setStopProcessing(true);
            addStep(trace, new RuleTraceStep(
                    RuleTraceStep.Type.INFO,
                    "stop-processing",
                    true,
                    "Further rules were not evaluated."
            ));
            return true;
        }

        return false;
    }

    private boolean matches(
            Rule rule,
            InteractionContext context,
            RuleExecutionResult result,
            RuleTrace trace
    ) {
        for (RuleCondition condition : rule.getConditions()) {
            String name = condition.getClass().getSimpleName();

            try {
                boolean matched = condition.matches(context);
                addStep(trace, new RuleTraceStep(
                        RuleTraceStep.Type.CONDITION,
                        name,
                        matched,
                        matched ? "passed" : "failed"
                ));

                if (!matched) {
                    return false;
                }
            } catch (RuntimeException exception) {
                addStep(trace, new RuleTraceStep(
                        RuleTraceStep.Type.ERROR,
                        name,
                        false,
                        safeMessage(exception)
                ));
                handleFailure(rule, result, "condition " + name, exception);
                return false;
            }
        }

        return true;
    }

    private boolean executeActions(
            Rule rule,
            InteractionContext context,
            RuleExecutionResult result,
            RuleTrace trace
    ) {
        for (RuleAction action : rule.getActions()) {
            String name = action.getClass().getSimpleName();

            try {
                action.execute(context, result);
                result.incrementExecutedActions();
                addStep(trace, new RuleTraceStep(
                        RuleTraceStep.Type.ACTION,
                        name,
                        true,
                        "executed"
                ));
            } catch (RuntimeException exception) {
                addStep(trace, new RuleTraceStep(
                        RuleTraceStep.Type.ERROR,
                        name,
                        false,
                        safeMessage(exception)
                ));
                handleFailure(rule, result, "action " + name, exception);
                return false;
            }
        }

        return true;
    }

    private void handleFailure(
            Rule rule,
            RuleExecutionResult result,
            String stage,
            RuntimeException exception
    ) {
        String message = "Rule '" + rule.getId() + "' failed in " + stage;
        String exceptionMessage = safeMessage(exception);

        if (!exceptionMessage.isEmpty()) {
            message += ": " + exceptionMessage;
        }

        result.markFailure(rule.getId(), message);
        logger.log(Level.SEVERE, message, exception);
    }

    private void addStep(RuleTrace trace, RuleTraceStep step) {
        if (trace != null) {
            trace.addStep(step);
        }
    }

    private String safeMessage(RuntimeException exception) {
        String message = exception.getMessage();
        return message == null ? "" : message.trim();
    }
}
