package me.stozeks.anarchyruleengine.model;

import me.stozeks.anarchyruleengine.action.RuleAction;
import me.stozeks.anarchyruleengine.condition.RuleCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Rule {

    private final String id;
    private final boolean enabled;
    private final int priority;
    private final List<RuleCondition> conditions;
    private final List<RuleAction> actions;
    private final boolean stopProcessing;

    public Rule(
            String id,
            boolean enabled,
            int priority,
            List<RuleCondition> conditions,
            List<RuleAction> actions,
            boolean stopProcessing
    ) {
        this.id = id;
        this.enabled = enabled;
        this.priority = priority;
        this.conditions = new ArrayList<>(conditions);
        this.actions = new ArrayList<>(actions);
        this.stopProcessing = stopProcessing;
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getPriority() {
        return priority;
    }

    public List<RuleCondition> getConditions() {
        return Collections.unmodifiableList(conditions);
    }

    public List<RuleAction> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public boolean shouldStopProcessing() {
        return stopProcessing;
    }

    public boolean matches(InteractionContext context) {
        for (RuleCondition condition : conditions) {
            if (!condition.matches(context)) {
                return false;
            }
        }

        return true;
    }
}