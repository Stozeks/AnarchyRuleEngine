package me.stozeks.anarchyruleengine.loader;

import me.stozeks.anarchyruleengine.action.RuleAction;
import me.stozeks.anarchyruleengine.condition.RuleCondition;
import me.stozeks.anarchyruleengine.factory.ActionFactory;
import me.stozeks.anarchyruleengine.factory.ConditionFactory;
import me.stozeks.anarchyruleengine.model.Rule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public final class RuleLoader {

    private final FileConfiguration configuration;
    private final ConditionFactory conditionFactory;
    private final ActionFactory actionFactory;

    public RuleLoader(
            FileConfiguration configuration,
            ConditionFactory conditionFactory,
            ActionFactory actionFactory
    ) {
        this.configuration = configuration;
        this.conditionFactory = conditionFactory;
        this.actionFactory = actionFactory;
    }

    public List<Rule> loadRules() {
        ConfigurationSection rulesSection =
                configuration.getConfigurationSection("rules");

        if (rulesSection == null) {
            return new ArrayList<>();
        }

        List<Rule> loadedRules = new ArrayList<>();

        for (String ruleId : rulesSection.getKeys(false)) {
            ConfigurationSection ruleSection =
                    rulesSection.getConfigurationSection(ruleId);

            if (ruleSection == null) {
                throw new RuleLoadException(
                        "Rule '" + ruleId
                                + "' must be a configuration section."
                );
            }

            try {
                loadedRules.add(loadRule(ruleId, ruleSection));
            } catch (RuleLoadException exception) {
                throw new RuleLoadException(
                        "Could not load rule '" + ruleId
                                + "': " + exception.getMessage(),
                        exception
                );
            }
        }

        return loadedRules;
    }

    private Rule loadRule(
            String ruleId,
            ConfigurationSection section
    ) {
        validateRuleId(ruleId);

        List<RuleCondition> conditions =
                conditionFactory.createConditions(
                        section.getConfigurationSection("conditions")
                );

        List<RuleAction> actions =
                actionFactory.createActions(
                        section.getMapList("actions")
                );

        if (conditions.isEmpty()) {
            throw new RuleLoadException(
                    "Rule must contain at least one condition. "
                            + "Use 'always: true' for an unconditional rule."
            );
        }

        if (actions.isEmpty()) {
            throw new RuleLoadException(
                    "Rule must contain at least one action."
            );
        }

        return new Rule(
                ruleId,
                section.getBoolean("enabled", true),
                section.getInt("priority", 0),
                conditions,
                actions,
                section.getBoolean("stop-processing", false)
        );
    }

    private void validateRuleId(String ruleId) {
        if (ruleId == null || ruleId.trim().isEmpty()) {
            throw new RuleLoadException(
                    "Rule ID cannot be empty."
            );
        }
    }
}