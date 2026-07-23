package me.stozeks.anarchyruleengine.loader;

import me.stozeks.anarchyruleengine.action.RuleAction;
import me.stozeks.anarchyruleengine.condition.RuleCondition;
import me.stozeks.anarchyruleengine.factory.ActionFactory;
import me.stozeks.anarchyruleengine.factory.ConditionFactory;
import me.stozeks.anarchyruleengine.model.Rule;
import me.stozeks.anarchyruleengine.model.RuleLoadResult;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RuleLoader {

    private static final Set<String> SUPPORTED_RULE_KEYS = Collections.unmodifiableSet(
            new LinkedHashSet<>(Arrays.asList(
                    "enabled", "priority", "stop-processing", "conditions", "actions"
            ))
    );

    private final FileConfiguration configuration;
    private final ConditionFactory conditionFactory;
    private final ActionFactory actionFactory;

    public RuleLoader(FileConfiguration configuration, ConditionFactory conditionFactory, ActionFactory actionFactory) {
        this.configuration = configuration;
        this.conditionFactory = conditionFactory;
        this.actionFactory = actionFactory;
    }

    public List<Rule> loadRules() {
        return load().getRules();
    }

    public RuleLoadResult load() {
        long startedAt = System.nanoTime();
        ConfigurationSection rulesSection = configuration.getConfigurationSection("rules");

        if (rulesSection == null) {
            if (configuration.contains("rules")) {
                throw new RuleLoadException("Root key 'rules' must be a configuration section.");
            }
            return new RuleLoadResult(Collections.emptyList(), 0, elapsedMillis(startedAt));
        }

        List<Rule> loadedRules = new ArrayList<>();
        int disabledRuleCount = 0;

        for (String ruleId : rulesSection.getKeys(false)) {
            ConfigurationSection ruleSection = rulesSection.getConfigurationSection(ruleId);
            if (ruleSection == null) {
                throw error(ruleId, "Rule value must be a configuration section.");
            }

            try {
                Rule rule = loadRule(ruleId, ruleSection);
                loadedRules.add(rule);
                if (!rule.isEnabled()) {
                    disabledRuleCount++;
                }
            } catch (RuleLoadException exception) {
                throw error(ruleId, exception.getMessage(), exception);
            }
        }

        loadedRules.sort(Comparator.comparingInt(Rule::getPriority).reversed());
        return new RuleLoadResult(loadedRules, disabledRuleCount, elapsedMillis(startedAt));
    }

    private Rule loadRule(String ruleId, ConfigurationSection section) {
        validateRuleId(ruleId);
        validateRuleKeys(section);

        boolean enabled = readBoolean(section, "enabled", true);
        int priority = readInteger(section, "priority", 0);
        boolean stopProcessing = readBoolean(section, "stop-processing", false);

        if (!section.isConfigurationSection("conditions")) {
            if (section.contains("conditions")) {
                throw new RuleLoadException("Field 'conditions' must be a configuration section.");
            }
            throw new RuleLoadException("Missing required section 'conditions'.");
        }

        Object rawActions = section.get("actions");
        if (rawActions == null) {
            throw new RuleLoadException("Missing required list 'actions'.");
        }
        if (!(rawActions instanceof List<?>)) {
            throw new RuleLoadException("Field 'actions' must be a list.");
        }

        List<?> rawActionList = (List<?>) rawActions;
        for (int index = 0; index < rawActionList.size(); index++) {
            if (!(rawActionList.get(index) instanceof java.util.Map<?, ?>)) {
                throw new RuleLoadException(
                        "Action at index " + index + " must be a configuration map."
                );
            }
        }

        List<RuleCondition> conditions = conditionFactory.createConditions(
                section.getConfigurationSection("conditions")
        );
        List<RuleAction> actions = actionFactory.createActions(section.getMapList("actions"));

        if (conditions.isEmpty()) {
            throw new RuleLoadException(
                    "Rule must contain at least one condition. Use 'always: true' for an unconditional rule."
            );
        }
        if (actions.isEmpty()) {
            throw new RuleLoadException("Rule must contain at least one action.");
        }

        return new Rule(ruleId, enabled, priority, conditions, actions, stopProcessing);
    }

    private void validateRuleKeys(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            if (!SUPPORTED_RULE_KEYS.contains(key)) {
                throw new RuleLoadException(
                        "Unknown field '" + key + "'. Supported fields: "
                                + String.join(", ", SUPPORTED_RULE_KEYS) + "."
                );
            }
        }
    }

    private boolean readBoolean(ConfigurationSection section, String key, boolean defaultValue) {
        if (!section.contains(key)) {
            return defaultValue;
        }
        Object value = section.get(key);
        if (!(value instanceof Boolean)) {
            throw new RuleLoadException("Field '" + key + "' must be true or false.");
        }
        return (Boolean) value;
    }

    private int readInteger(ConfigurationSection section, String key, int defaultValue) {
        if (!section.contains(key)) {
            return defaultValue;
        }
        Object value = section.get(key);
        if (!(value instanceof Integer)) {
            throw new RuleLoadException("Field '" + key + "' must be a whole number.");
        }
        return (Integer) value;
    }

    private void validateRuleId(String ruleId) {
        if (ruleId == null || ruleId.trim().isEmpty()) {
            throw new RuleLoadException("Rule ID cannot be empty.");
        }
        if (!ruleId.matches("[a-zA-Z0-9_-]+")) {
            throw new RuleLoadException(
                    "Rule ID may contain only letters, numbers, '-' and '_'."
            );
        }
    }

    private RuleLoadException error(String ruleId, String message) {
        return new RuleLoadException("Could not load rule '" + ruleId + "': " + message);
    }

    private RuleLoadException error(String ruleId, String message, Throwable cause) {
        return new RuleLoadException("Could not load rule '" + ruleId + "': " + message, cause);
    }

    private long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000L;
    }
}
