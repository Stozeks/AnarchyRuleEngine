package me.stozeks.anarchyruleengine.core;

import me.stozeks.anarchyruleengine.command.RuleCommand;
import me.stozeks.anarchyruleengine.engine.RuleEngine;
import me.stozeks.anarchyruleengine.executor.RuleExecutor;
import me.stozeks.anarchyruleengine.factory.ActionFactory;
import me.stozeks.anarchyruleengine.factory.ConditionFactory;
import me.stozeks.anarchyruleengine.listener.PlayerInteractListener;
import me.stozeks.anarchyruleengine.loader.RuleLoadException;
import me.stozeks.anarchyruleengine.item.ItemLoader;
import me.stozeks.anarchyruleengine.item.ItemRegistry;
import me.stozeks.anarchyruleengine.loader.RuleLoader;
import me.stozeks.anarchyruleengine.model.Rule;
import me.stozeks.anarchyruleengine.service.RuleReloadService;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public final class AnarchyRuleEnginePlugin extends JavaPlugin {

    private RuleEngine ruleEngine;

    private ConditionFactory conditionFactory;
    private ActionFactory actionFactory;
    private ItemRegistry itemRegistry;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        itemRegistry = new ItemLoader().load(getConfig());

        getLogger().info(
                "Loaded " + itemRegistry.getAll().size() + " custom item(s)."
        );

        conditionFactory = new ConditionFactory();
        actionFactory = new ActionFactory();

        ruleEngine = new RuleEngine(
                new RuleExecutor(),
                Collections.emptyList()
        );

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

        registerListeners();
        registerCommands();

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

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(
                new PlayerInteractListener(ruleEngine),
                this
        );
    }

    private void registerCommands() {
        RuleReloadService ruleReloadService =
                new RuleReloadService(
                        this,
                        ruleEngine,
                        conditionFactory,
                        actionFactory
                );

        PluginCommand ruleCommand = getCommand("are");

        if (ruleCommand == null) {
            throw new IllegalStateException(
                    "Command 'are' is missing from plugin.yml."
            );
        }

        ruleCommand.setExecutor(
                new RuleCommand(ruleReloadService)
        );
    }

    private RuleLoader createRuleLoader() {
        return new RuleLoader(
                getConfig(),
                conditionFactory,
                actionFactory
        );
    }
}