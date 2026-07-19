package me.stozeks.anarchyruleengine.core;

import me.stozeks.anarchyruleengine.engine.RuleEngine;
import me.stozeks.anarchyruleengine.listener.PlayerInteractListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public final class AnarchyRuleEnginePlugin extends JavaPlugin {

    private RuleEngine ruleEngine;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ruleEngine = new RuleEngine(Collections.emptyList());
        getServer().getPluginManager().registerEvents(
                new PlayerInteractListener(ruleEngine),
                this
        );

        getLogger().info("AnarchyRuleEngine has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AnarchyRuleEngine has been disabled.");
    }

    public RuleEngine getRuleEngine() {
        return ruleEngine;
    }
}
