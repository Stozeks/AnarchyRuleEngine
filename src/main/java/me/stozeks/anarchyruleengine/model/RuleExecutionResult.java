package me.stozeks.anarchyruleengine.model;

public final class RuleExecutionResult {

    private boolean matched;
    private boolean cancelled;
    private boolean stopProcessing;
    private String matchedRuleId;
    private int executedActions;

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
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

    public void setMatchedRuleId(String matchedRuleId) {
        this.matchedRuleId = matchedRuleId;
    }

    public int getExecutedActions() {
        return executedActions;
    }

    public void incrementExecutedActions() {
        executedActions++;
    }
}