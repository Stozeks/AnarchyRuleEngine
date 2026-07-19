package me.stozeks.anarchyruleengine.core;

import me.stozeks.anarchyruleengine.engine.RuleEngine;
import me.stozeks.anarchyruleengine.factory.ActionFactory;
import me.stozeks.anarchyruleengine.factory.ConditionFactory;
import me.stozeks.anarchyruleengine.listener.PlayerInteractListener;
import me.stozeks.anarchyruleengine.loader.RuleLoadException;
import me.stozeks.anarchyruleengine.loader.RuleLoader;
import me.stozeks.anarchyruleengine.model.Rule;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public final class AnarchyRuleEnginePlugin extends JavaPlugin {

    private RuleEngine ruleEngine;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ruleEngine = new RuleEngine(Collections.emptyList());

        try {
            List<Rule> loadedRules = createRuleLoader().loadRules();
            ruleEngine.replaceRules(loadedRules);

            getLogger().info(
                    "Loaded " + loadedRules.size() + " rule(s)."
            );
        } catch (RuleLoadException exception) {
            getLogger().severe(
                    "Could not load rules: " + exception.getMessage()
            );

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(
                new PlayerInteractListener(ruleEngine),
                this
        );

        getLogger().info(
                "AnarchyRuleEngine has been enabled."
        );
    }

    @Override
    public void onDisable() {
        getLogger().info(
                "AnarchyRuleEngine has been disabled."
        );
    }

    public RuleEngine getRuleEngine() {
        return ruleEngine;
    }

    private RuleLoader createRuleLoader() {
        return new RuleLoader(
                getConfig(),
                new ConditionFactory(),
                new ActionFactory()
        );
    }
}