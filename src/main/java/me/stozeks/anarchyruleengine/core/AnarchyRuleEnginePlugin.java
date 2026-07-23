package me.stozeks.anarchyruleengine.core;

import me.stozeks.anarchyruleengine.command.RuleCommand;
import me.stozeks.anarchyruleengine.engine.RuleEngine;
import me.stozeks.anarchyruleengine.executor.RuleExecutor;
import me.stozeks.anarchyruleengine.factory.ActionFactory;
import me.stozeks.anarchyruleengine.factory.ConditionFactory;
import me.stozeks.anarchyruleengine.item.ItemBuilder;
import me.stozeks.anarchyruleengine.item.ItemLoader;
import me.stozeks.anarchyruleengine.item.ItemRegistry;
import me.stozeks.anarchyruleengine.item.ItemService;
import me.stozeks.anarchyruleengine.listener.PlayerInteractListener;
import me.stozeks.anarchyruleengine.loader.RuleLoadException;
import me.stozeks.anarchyruleengine.loader.RuleLoader;
import me.stozeks.anarchyruleengine.model.Rule;
import me.stozeks.anarchyruleengine.service.ActionServices;
import me.stozeks.anarchyruleengine.service.ConditionServices;
import me.stozeks.anarchyruleengine.service.DebugService;
import me.stozeks.anarchyruleengine.service.RuleReloadService;
import me.stozeks.anarchyruleengine.service.RuleInspectorService;
import me.stozeks.anarchyruleengine.service.CooldownService;
import me.stozeks.anarchyruleengine.placeholder.PlaceholderService;
import me.stozeks.anarchyruleengine.placeholder.PlayerPlaceholderResolver;
import me.stozeks.anarchyruleengine.placeholder.WorldPlaceholderResolver;
import me.stozeks.anarchyruleengine.placeholder.PositionPlaceholderResolver;
import me.stozeks.anarchyruleengine.placeholder.CooldownPlaceholderResolver;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public final class AnarchyRuleEnginePlugin extends JavaPlugin {

    private RuleEngine ruleEngine;

    private ConditionFactory conditionFactory;
    private ActionFactory actionFactory;

    private ItemRegistry itemRegistry;
    private ItemBuilder itemBuilder;
    private ItemService itemService;
    private CooldownService cooldownService;
    private PlaceholderService placeholderService;
    private DebugService debugService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        try {
            itemRegistry = new ItemLoader().load(getConfig());
        } catch (RuleLoadException exception) {
            getLogger().severe(
                    "Could not load custom items: "
                            + exception.getMessage()
            );

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        itemBuilder = new ItemBuilder(this);

        itemService = new ItemService(
                itemRegistry,
                itemBuilder
        );

        cooldownService =
                new CooldownService();

        debugService = new DebugService();

        ConditionServices conditionServices =
                new ConditionServices(
                        itemService,
                        cooldownService
                );

        placeholderService =
                new PlaceholderService(
                        Arrays.asList(
                                new PlayerPlaceholderResolver(),
                                new WorldPlaceholderResolver(),
                                new PositionPlaceholderResolver(),
                                new CooldownPlaceholderResolver(
                                        cooldownService
                                )
                        )
                );

        ActionServices actionServices =
                new ActionServices(
                        itemService,
                        cooldownService,
                        placeholderService
                );

        conditionFactory = new ConditionFactory(
                conditionServices
        );

        actionFactory = new ActionFactory(
                actionServices
        );

        ruleEngine = new RuleEngine(
                new RuleExecutor(getLogger()),
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

        if (cooldownService != null) {
            cooldownService.clearAllCooldowns();
        }

        if (debugService != null) {
            debugService.clear();
        }

        getLogger().info(
                "AnarchyRuleEngine has been disabled."
        );
    }

    public RuleEngine getRuleEngine() {
        return ruleEngine;
    }

    public ItemRegistry getItemRegistry() {
        return itemService != null
                ? itemService.getItemRegistry()
                : itemRegistry;
    }

    public ItemBuilder getItemBuilder() {
        return itemBuilder;
    }

    public ItemService getItemService() {
        return itemService;
    }

    public CooldownService getCooldownService() {
        return cooldownService;
    }

    public PlaceholderService getPlaceholderService() {
        return placeholderService;
    }

    public ConditionFactory getConditionFactory() {
        return conditionFactory;
    }

    public ActionFactory getActionFactory() {
        return actionFactory;
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(
                new PlayerInteractListener(ruleEngine, debugService),
                this
        );
    }

    private void registerCommands() {
        RuleReloadService ruleReloadService =
                new RuleReloadService(
                        this,
                        ruleEngine,
                        itemService,
                        conditionFactory,
                        actionFactory
                );

        PluginCommand ruleCommand = getCommand("are");

        if (ruleCommand == null) {
            throw new IllegalStateException(
                    "Command 'are' is missing from plugin.yml."
            );
        }

        RuleCommand executor = new RuleCommand(
                ruleReloadService,
                itemService,
                new RuleInspectorService(ruleEngine),
                debugService,
                getDescription().getVersion()
        );

        ruleCommand.setExecutor(executor);
        ruleCommand.setTabCompleter(executor);
    }

    private RuleLoader createRuleLoader() {
        return new RuleLoader(
                getConfig(),
                conditionFactory,
                actionFactory
        );
    }
}