package me.stozeks.anarchyruleengine.service;

import me.stozeks.anarchyruleengine.engine.RuleEngine;
import me.stozeks.anarchyruleengine.factory.ActionFactory;
import me.stozeks.anarchyruleengine.factory.ConditionFactory;
import me.stozeks.anarchyruleengine.loader.RuleLoadException;
import me.stozeks.anarchyruleengine.loader.RuleLoader;
import me.stozeks.anarchyruleengine.model.Rule;
import me.stozeks.anarchyruleengine.model.RuleReloadResult;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class RuleReloadService {

    private final JavaPlugin plugin;
    private final RuleEngine ruleEngine;
    private final ConditionFactory conditionFactory;
    private final ActionFactory actionFactory;

    public RuleReloadService(
            JavaPlugin plugin,
            RuleEngine ruleEngine,
            ConditionFactory conditionFactory,
            ActionFactory actionFactory
    ) {
        this.plugin = plugin;
        this.ruleEngine = ruleEngine;
        this.conditionFactory = conditionFactory;
        this.actionFactory = actionFactory;
    }

    public RuleReloadResult reloadRules() {
        plugin.reloadConfig();

        plugin.getLogger().info(
                "Reading config from: "
                        + plugin.getDataFolder().getAbsolutePath()
                        + "/config.yml"
        );

        if (plugin.getConfig().getConfigurationSection("rules") == null) {
            plugin.getLogger().warning("Rules section does not exist.");
        } else {
            plugin.getLogger().info(
                    "Rule IDs found: "
                            + plugin.getConfig()
                            .getConfigurationSection("rules")
                            .getKeys(false)
            );
        }

        RuleLoader ruleLoader = new RuleLoader(
                plugin.getConfig(),
                conditionFactory,
                actionFactory
        );

        try {
            List<Rule> loadedRules = ruleLoader.loadRules();

            ruleEngine.replaceRules(loadedRules);

            return RuleReloadResult.success(loadedRules.size());
        } catch (RuleLoadException exception) {
            return RuleReloadResult.failure(exception.getMessage());
        }
    }
}