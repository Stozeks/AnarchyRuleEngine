package me.stozeks.anarchyruleengine.item;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;

public final class ItemLoader {

    public ItemRegistry load(FileConfiguration config) {

        ItemRegistry registry = new ItemRegistry();

        ConfigurationSection itemsSection = config.getConfigurationSection("items");

        if (itemsSection == null) {
            return registry;
        }

        for (String id : itemsSection.getKeys(false)) {

            ConfigurationSection section =
                    itemsSection.getConfigurationSection(id);

            if (section == null) {
                continue;
            }

            Material material = Material.matchMaterial(
                    section.getString("material", "STONE")
            );

            if (material == null) {
                continue;
            }

            String name = section.getString("name", id);

            List<String> lore = section.getStringList("lore");

            if (lore == null) {
                lore = Collections.emptyList();
            }

            registry.register(
                    new CustomItem(
                            id,
                            material,
                            name,
                            lore
                    )
            );
        }

        return registry;
    }
}