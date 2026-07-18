package me.stozeks.anarchyruleengine.core;

import org.bukkit.plugin.java.JavaPlugin;

public final class AnarchyRuleEnginePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getLogger().info("AnarchyRuleEngine has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AnarchyRuleEngine has been disabled.");
    }
}