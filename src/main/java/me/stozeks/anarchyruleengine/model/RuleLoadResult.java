package me.stozeks.anarchyruleengine.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RuleLoadResult {

    private final List<Rule> rules;
    private final int disabledRuleCount;
    private final long durationMillis;

    public RuleLoadResult(List<Rule> rules, int disabledRuleCount, long durationMillis) {
        this.rules = new ArrayList<>(rules);
        this.disabledRuleCount = disabledRuleCount;
        this.durationMillis = durationMillis;
    }

    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    public int getLoadedRuleCount() {
        return rules.size();
    }

    public int getDisabledRuleCount() {
        return disabledRuleCount;
    }

    public long getDurationMillis() {
        return durationMillis;
    }
}
