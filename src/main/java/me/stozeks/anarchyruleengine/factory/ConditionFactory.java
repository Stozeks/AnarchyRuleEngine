package me.stozeks.anarchyruleengine.factory;
import me.stozeks.anarchyruleengine.condition.AlwaysCondition;
import me.stozeks.anarchyruleengine.condition.MaterialCondition;
import me.stozeks.anarchyruleengine.condition.PermissionCondition;
import me.stozeks.anarchyruleengine.condition.InteractionActionCondition;
import me.stozeks.anarchyruleengine.condition.YLevelCondition;
import me.stozeks.anarchyruleengine.condition.RegionCondition;
import me.stozeks.anarchyruleengine.condition.RuleCondition;
import me.stozeks.anarchyruleengine.loader.RuleLoadException;
import me.stozeks.anarchyruleengine.condition.WorldCondition;
import me.stozeks.anarchyruleengine.condition.ItemCondition;
import me.stozeks.anarchyruleengine.service.ConditionServices;
import org.bukkit.event.block.Action;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Objects;

public final class ConditionFactory {

    private static final Set<String> SUPPORTED_KEYS = new HashSet<>(
            Arrays.asList(
                    "always",
                    "material",
                    "item",
                    "permission",
                    "world",
                    "region",
                    "y-min",
                    "y-max",
                    "interaction-action"
            )
    );

    private final ConditionServices services;

    public ConditionFactory(ConditionServices services) {
        this.services = Objects.requireNonNull(
                services,
                "services"
        );
    }

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
        String itemId = section.getString("item");
        String permission = section.getString("permission");
        String worldName = section.getString("world");
        String regionName = section.getString("region");
        String interactionActionName = section.getString("interaction-action");

        Integer minY = section.contains("y-min")
                ? section.getInt("y-min")
                : null;

        Integer maxY = section.contains("y-max")
                ? section.getInt("y-max")
                : null;

        if (always && (
                materialName != null
                        || itemId != null
                        || permission != null
                        || worldName != null
                        || regionName != null
                        || minY != null
                        || maxY != null
                        || interactionActionName != null
        )) {
            throw new RuleLoadException(
                    "Condition 'always' cannot be combined with other conditions."
            );
        }

        if (always) {
            conditions.add(new AlwaysCondition());
            return conditions;
        }

        if (itemId != null) {
            conditions.add(createItemCondition(itemId));
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

        if (regionName != null) {
            conditions.add(createRegionCondition(regionName));
        }

        if (minY != null || maxY != null) {
            conditions.add(createYLevelCondition(minY, maxY));
        }

        if (interactionActionName != null) {
            conditions.add(createInteractionActionCondition(interactionActionName));
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

    private RuleCondition createItemCondition(String itemId) {
        String normalizedItemId = itemId.trim();

        if (normalizedItemId.isEmpty()) {
            throw new RuleLoadException(
                    "Item condition cannot be empty."
            );
        }

        if (services.getItemService().getCustomItem(normalizedItemId) == null) {
            throw new RuleLoadException(
                    "Unknown custom item '" + itemId + "'."
            );
        }

        return new ItemCondition(
                services.getItemService(),
                normalizedItemId
        );
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

    private RuleCondition createRegionCondition(String regionName) {
        String normalizedRegionName = regionName.trim();

        if (normalizedRegionName.isEmpty()) {
            throw new RuleLoadException(
                    "Region condition cannot be empty."
            );
        }

        return new RegionCondition(normalizedRegionName);
    }

    private RuleCondition createYLevelCondition(Integer minY, Integer maxY) {
        if (minY != null && maxY != null && minY > maxY) {
            throw new RuleLoadException(
                    "Condition 'y-min' cannot be greater than 'y-max'."
            );
        }

        return new YLevelCondition(minY, maxY);
    }

    private RuleCondition createInteractionActionCondition(String actionName) {

        String normalizedAction = actionName
                .trim()
                .toUpperCase(Locale.ROOT);

        if (normalizedAction.isEmpty()) {
            throw new RuleLoadException(
                    "Interaction action cannot be empty."
            );
        }

        Action action;

        try {
            action = Action.valueOf(normalizedAction);
        } catch (IllegalArgumentException exception) {
            throw new RuleLoadException(
                    "Unknown interaction action '" + actionName + "'."
            );
        }

        return new InteractionActionCondition(action);
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