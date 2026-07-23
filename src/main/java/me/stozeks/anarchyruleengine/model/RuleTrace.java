package me.stozeks.anarchyruleengine.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class RuleTrace {

    private final String ruleId;
    private final int priority;
    private final List<RuleTraceStep> steps = new ArrayList<>();
    private boolean matched;
    private boolean skipped;
    private String skipReason;

    public RuleTrace(String ruleId, int priority) {
        this.ruleId = Objects.requireNonNull(ruleId, "ruleId");
        this.priority = priority;
    }

    public String getRuleId() {
        return ruleId;
    }

    public int getPriority() {
        return priority;
    }

    public void addStep(RuleTraceStep step) {
        steps.add(Objects.requireNonNull(step, "step"));
    }

    public List<RuleTraceStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public String getSkipReason() {
        return skipReason;
    }

    public void markSkipped(String reason) {
        this.skipped = true;
        this.skipReason = reason;
    }
}
