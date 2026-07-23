package me.stozeks.anarchyruleengine.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RuleExecutionResult {

    private final boolean tracingEnabled;

    public RuleExecutionResult() {
        this(false);
    }

    public RuleExecutionResult(boolean tracingEnabled) {
        this.tracingEnabled = tracingEnabled;
    }

    private boolean matched;
    private boolean cancelled;
    private boolean stopProcessing;
    private boolean failed;
    private String matchedRuleId;
    private String failedRuleId;
    private String failureMessage;
    private int executedActions;
    private final List<String> matchedRuleIds = new ArrayList<>();
    private final List<RuleTrace> ruleTraces = new ArrayList<>();

    public boolean isMatched() {
        return matched;
    }

    public void markMatched(String ruleId) {
        matched = true;
        matchedRuleId = ruleId;
        matchedRuleIds.add(ruleId);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean shouldStopProcessing() {
        return stopProcessing;
    }

    public void setStopProcessing(boolean stopProcessing) {
        this.stopProcessing = stopProcessing;
    }

    public String getMatchedRuleId() {
        return matchedRuleId;
    }

    public List<String> getMatchedRuleIds() {
        return Collections.unmodifiableList(matchedRuleIds);
    }

    public boolean isTracingEnabled() {
        return tracingEnabled;
    }

    public void addRuleTrace(RuleTrace trace) {
        if (tracingEnabled) {
            ruleTraces.add(trace);
        }
    }

    public List<RuleTrace> getRuleTraces() {
        return Collections.unmodifiableList(ruleTraces);
    }

    public int getExecutedActions() {
        return executedActions;
    }

    public void incrementExecutedActions() {
        executedActions++;
    }

    public boolean hasFailed() {
        return failed;
    }

    public String getFailedRuleId() {
        return failedRuleId;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void markFailure(
            String ruleId,
            String failureMessage
    ) {
        this.failed = true;
        this.failedRuleId = ruleId;
        this.failureMessage = failureMessage;
        this.stopProcessing = true;
    }
}
