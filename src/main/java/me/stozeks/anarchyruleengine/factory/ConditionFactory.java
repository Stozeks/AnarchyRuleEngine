package me.stozeks.anarchyruleengine.factory;

import me.stozeks.anarchyruleengine.condition.AlwaysCondition;
import me.stozeks.anarchyruleengine.condition.MaterialCondition;
import me.stozeks.anarchyruleengine.condition.PermissionCondition;
import me.stozeks.anarchyruleengine.condition.RuleCondition;
import me.stozeks.anarchyruleengine.loader.RuleLoadException;
import me.stozeks.anarchyruleengine.condition.WorldCondition;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ConditionFactory {

    private static final Set<String> SUPPORTED_KEYS = new HashSet<>(
            Arrays.asList(
                    "always",
                    "material",
                    "permission",
                    "world"
            )
    );

    public List<RuleCondition> createConditions(
            ConfigurationSection section
    ) {
        List<RuleCondition> conditions = new ArrayList<>();

        if (section == null) {
            return conditions;
        }

        validateKeys(section);

        boolean always = section.getBoolean("always", false);
        String materialName = section.getString("material");
        String permission = section.getString("permission");
        String worldName = section.getString("world");

        if (always && (
                materialName != null
                        || permission != null
                        || worldName != null
        )) {
            throw new RuleLoadException(
                    "Condition 'always' cannot be combined with other conditions."
            );
        }

        if (always) {
            conditions.add(new AlwaysCondition());
            return conditions;
        }

        if (materialName != null) {
            conditions.add(createMaterialCondition(materialName));
        }

        if (permission != null) {
            conditions.add(createPermissionCondition(permission));
        }

        if (worldName != null) {
            conditions.add(createWorldCondition(worldName));
        }

        return conditions;
    }

    private RuleCondition createMaterialCondition(String materialName) {
        String normalizedName = materialName
                .trim()
                .toUpperCase(Locale.ROOT);

        if (normalizedName.isEmpty()) {
            throw new RuleLoadException(
                    "Material condition cannot be empty."
            );
        }

        Material material = Material.matchMaterial(normalizedName);

        if (material == null) {
            throw new RuleLoadException(
                    "Unknown material '" + materialName + "'."
            );
        }

        return new MaterialCondition(material);
    }

    private RuleCondition createPermissionCondition(String permission) {
        String normalizedPermission = permission.trim();

        if (normalizedPermission.isEmpty()) {
            throw new RuleLoadException(
                    "Permission condition cannot be empty."
            );
        }

        return new PermissionCondition(normalizedPermission);
    }

    private RuleCondition createWorldCondition(String worldName) {
        String normalizedWorldName = worldName.trim();

        if (normalizedWorldName.isEmpty()) {
            throw new RuleLoadException(
                    "World condition cannot be empty."
            );
        }

        return new WorldCondition(normalizedWorldName);
    }

    private void validateKeys(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            if (!SUPPORTED_KEYS.contains(key)) {
                throw new RuleLoadException(
                        "Unknown condition '" + key + "'."
                );
            }
        }
    }
}