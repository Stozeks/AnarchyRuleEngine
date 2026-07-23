package me.stozeks.anarchyruleengine.item;

import me.stozeks.anarchyruleengine.loader.RuleLoadException;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class ItemLoader {

    public ItemRegistry load(FileConfiguration config) {
        Objects.requireNonNull(config, "config");

        ItemRegistry registry = new ItemRegistry();

        ConfigurationSection itemsSection =
                config.getConfigurationSection("items");

        if (itemsSection == null) {
            return registry;
        }

        for (String rawId : itemsSection.getKeys(false)) {
            String itemId = normalizeItemId(rawId);

            ConfigurationSection section =
                    itemsSection.getConfigurationSection(rawId);

            if (section == null) {
                throw new RuleLoadException(
                        "Item '" + rawId
                                + "' must be a configuration section."
                );
            }

            String materialName = section.getString("material");

            if (materialName == null
                    || materialName.trim().isEmpty()) {
                throw new RuleLoadException(
                        "Item '" + rawId
                                + "' is missing material."
                );
            }

            Material material = Material.matchMaterial(
                    materialName.trim().toUpperCase(Locale.ROOT)
            );

            if (material == null || material.isAir()) {
                throw new RuleLoadException(
                        "Item '" + rawId
                                + "' has unknown or invalid material '"
                                + materialName + "'."
                );
            }

            String name = section.getString("name", rawId);
            List<String> lore = section.getStringList("lore");

            registry.register(
                    new CustomItem(
                            itemId,
                            material,
                            name,
                            lore
                    )
            );
        }

        return registry;
    }

    private String normalizeItemId(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new RuleLoadException(
                    "Custom item ID cannot be empty."
            );
        }

        return itemId.trim().toLowerCase(Locale.ROOT);
    }
}
