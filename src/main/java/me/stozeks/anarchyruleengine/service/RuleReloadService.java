package me.stozeks.anarchyruleengine.service;

import me.stozeks.anarchyruleengine.engine.RuleEngine;
import me.stozeks.anarchyruleengine.factory.ActionFactory;
import me.stozeks.anarchyruleengine.factory.ConditionFactory;
import me.stozeks.anarchyruleengine.item.ItemLoader;
import me.stozeks.anarchyruleengine.item.ItemRegistry;
import me.stozeks.anarchyruleengine.item.ItemService;
import me.stozeks.anarchyruleengine.loader.RuleLoader;
import me.stozeks.anarchyruleengine.model.Rule;
import me.stozeks.anarchyruleengine.model.RuleLoadResult;
import me.stozeks.anarchyruleengine.model.RuleReloadResult;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public final class RuleReloadService {

    private final JavaPlugin plugin;
    private final RuleEngine ruleEngine;
    private final ItemService itemService;
    private final ConditionFactory conditionFactory;
    private final ActionFactory actionFactory;

    public RuleReloadService(
            JavaPlugin plugin,
            RuleEngine ruleEngine,
            ItemService itemService,
            ConditionFactory conditionFactory,
            ActionFactory actionFactory
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.ruleEngine = Objects.requireNonNull(ruleEngine, "ruleEngine");
        this.itemService = Objects.requireNonNull(itemService, "itemService");
        this.conditionFactory = Objects.requireNonNull(
                conditionFactory,
                "conditionFactory"
        );
        this.actionFactory = Objects.requireNonNull(
                actionFactory,
                "actionFactory"
        );
    }

    public RuleReloadResult reloadRules() {
        ItemRegistry previousRegistry = itemService.getItemRegistry();

        try {
            plugin.reloadConfig();

            ItemRegistry loadedItems =
                    new ItemLoader().load(plugin.getConfig());

            itemService.replaceItemRegistry(loadedItems);

            RuleLoadResult loadResult = new RuleLoader(
                    plugin.getConfig(),
                    conditionFactory,
                    actionFactory
            ).load();

            List<Rule> loadedRules = loadResult.getRules();
            ruleEngine.replaceRules(loadedRules);

            return RuleReloadResult.success(
                    loadResult.getLoadedRuleCount(),
                    loadedItems.getAll().size(),
                    loadResult.getDisabledRuleCount(),
                    loadResult.getDurationMillis()
            );
        } catch (RuntimeException exception) {
            itemService.replaceItemRegistry(previousRegistry);

            String message = exception.getMessage();

            if (message == null || message.trim().isEmpty()) {
                message = exception.getClass().getSimpleName();
            }

            plugin.getLogger().warning(
                    "Configuration reload failed: " + message
            );

            return RuleReloadResult.failure(message);
        }
    }
}
