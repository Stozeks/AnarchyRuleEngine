package me.stozeks.anarchyruleengine.service;

import me.stozeks.anarchyruleengine.action.RuleAction;
import me.stozeks.anarchyruleengine.condition.RuleCondition;
import me.stozeks.anarchyruleengine.engine.RuleEngine;
import me.stozeks.anarchyruleengine.model.Rule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class RuleInspectorService {

    private final RuleEngine ruleEngine;

    public RuleInspectorService(RuleEngine ruleEngine) {
        this.ruleEngine = Objects.requireNonNull(ruleEngine, "ruleEngine");
    }

    public Rule findRule(String ruleId) {
        if (ruleId == null) {
            return null;
        }

        String normalizedId = ruleId.trim();
        if (normalizedId.isEmpty()) {
            return null;
        }

        for (Rule rule : ruleEngine.getRules()) {
            if (rule.getId().equalsIgnoreCase(normalizedId)) {
                return rule;
            }
        }

        return null;
    }

    public List<String> getRuleIds() {
        List<String> ruleIds = new ArrayList<>();

        for (Rule rule : ruleEngine.getRules()) {
            ruleIds.add(rule.getId());
        }

        ruleIds.sort(String.CASE_INSENSITIVE_ORDER);
        return ruleIds;
    }

    public List<String> describeConditions(Rule rule) {
        Objects.requireNonNull(rule, "rule");
        List<String> descriptions = new ArrayList<>();

        for (RuleCondition condition : rule.getConditions()) {
            descriptions.add(toReadableName(condition.getClass().getSimpleName(), "Condition"));
        }

        return descriptions;
    }

    public List<String> describeActions(Rule rule) {
        Objects.requireNonNull(rule, "rule");
        List<String> descriptions = new ArrayList<>();

        for (RuleAction action : rule.getActions()) {
            descriptions.add(toReadableName(action.getClass().getSimpleName(), "Action"));
        }

        return descriptions;
    }

    private String toReadableName(String simpleName, String suffix) {
        String baseName = simpleName.endsWith(suffix)
                ? simpleName.substring(0, simpleName.length() - suffix.length())
                : simpleName;

        StringBuilder result = new StringBuilder();

        for (int index = 0; index < baseName.length(); index++) {
            char current = baseName.charAt(index);

            if (index > 0 && Character.isUpperCase(current)) {
                result.append('-');
            }

            result.append(Character.toLowerCase(current));
        }

        return result.toString().toLowerCase(Locale.ROOT);
    }
}
