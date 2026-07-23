package me.stozeks.anarchyruleengine.factory;

import me.stozeks.anarchyruleengine.condition.AlwaysCondition;
import me.stozeks.anarchyruleengine.condition.CooldownCondition;
import me.stozeks.anarchyruleengine.condition.InteractionActionCondition;
import me.stozeks.anarchyruleengine.condition.ItemCondition;
import me.stozeks.anarchyruleengine.condition.MaterialCondition;
import me.stozeks.anarchyruleengine.condition.OnCooldownCondition;
import me.stozeks.anarchyruleengine.condition.PermissionCondition;
import me.stozeks.anarchyruleengine.condition.RegionCondition;
import me.stozeks.anarchyruleengine.condition.RuleCondition;
import me.stozeks.anarchyruleengine.condition.WorldCondition;
import me.stozeks.anarchyruleengine.condition.YLevelCondition;
import me.stozeks.anarchyruleengine.loader.RuleLoadException;
import me.stozeks.anarchyruleengine.service.ConditionServices;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ConditionFactory {

    @FunctionalInterface
    public interface ConditionCreator {
        RuleCondition create(ConfigurationSection section);
    }

    private static final class RegisteredCondition {
        private final Set<String> keys;
        private final ConditionCreator creator;

        private RegisteredCondition(Set<String> keys, ConditionCreator creator) {
            this.keys = keys;
            this.creator = creator;
        }

        private boolean isConfigured(ConfigurationSection section) {
            for (String key : keys) {
                if (section.contains(key)) {
                    return true;
                }
            }

            return false;
        }
    }

    private final ConditionServices services;
    private final Map<String, RegisteredCondition> conditions = new LinkedHashMap<>();
    private final Set<String> supportedKeys = new LinkedHashSet<>();

    public ConditionFactory(ConditionServices services) {
        this.services = Objects.requireNonNull(services, "services");
        registerBuiltInConditions();
    }

    public void registerCondition(
            String id,
            Collection<String> keys,
            ConditionCreator creator
    ) {
        String normalizedId = normalizeIdentifier(id, "Condition registration id");
        Objects.requireNonNull(keys, "keys");
        Objects.requireNonNull(creator, "creator");

        if (conditions.containsKey(normalizedId)) {
            throw new IllegalArgumentException(
                    "Condition registration '" + normalizedId + "' already exists."
            );
        }

        Set<String> normalizedKeys = new LinkedHashSet<>();

        for (String key : keys) {
            String normalizedKey = normalizeIdentifier(key, "Condition key");

            if (supportedKeys.contains(normalizedKey)) {
                throw new IllegalArgumentException(
                        "Condition key '" + normalizedKey + "' is already registered."
                );
            }

            normalizedKeys.add(normalizedKey);
        }

        if (normalizedKeys.isEmpty()) {
            throw new IllegalArgumentException(
                    "Condition registration must contain at least one key."
            );
        }

        conditions.put(
                normalizedId,
                new RegisteredCondition(normalizedKeys, creator)
        );
        supportedKeys.addAll(normalizedKeys);
    }

    public Set<String> getSupportedConditionKeys() {
        return Collections.unmodifiableSet(supportedKeys);
    }

    public List<RuleCondition> createConditions(ConfigurationSection section) {
        List<RuleCondition> createdConditions = new ArrayList<>();

        if (section == null) {
            return createdConditions;
        }

        validateKeys(section);

        boolean always = false;

        if (section.contains("always")) {
            Object alwaysValue = section.get("always");
            if (!(alwaysValue instanceof Boolean)) {
                throw new RuleLoadException("Condition 'always' must be true or false.");
            }
            always = (Boolean) alwaysValue;
        }

        if (always) {
            if (section.getKeys(false).size() != 1) {
                throw new RuleLoadException(
                        "Condition 'always' cannot be combined with other conditions."
                );
            }

            createdConditions.add(new AlwaysCondition());
            return createdConditions;
        }

        for (Map.Entry<String, RegisteredCondition> entry : conditions.entrySet()) {
            if ("always".equals(entry.getKey())) {
                continue;
            }

            RegisteredCondition registered = entry.getValue();

            if (!registered.isConfigured(section)) {
                continue;
            }

            try {
                RuleCondition condition = registered.creator.create(section);

                if (condition == null) {
                    throw new RuleLoadException(
                            "Condition creator '" + entry.getKey() + "' returned null."
                    );
                }

                createdConditions.add(condition);
            } catch (RuleLoadException exception) {
                throw exception;
            } catch (RuntimeException exception) {
                throw new RuleLoadException(
                        "Unexpected error in condition '" + entry.getKey()
                                + "': " + exception.getMessage(),
                        exception
                );
            }
        }

        return createdConditions;
    }

    private void registerBuiltInConditions() {
        registerCondition(
                "always",
                Arrays.asList("always"),
                section -> new AlwaysCondition()
        );

        registerCondition(
                "item",
                Arrays.asList("item"),
                section -> createItemCondition(section.getString("item"))
        );

        registerCondition(
                "material",
                Arrays.asList("material"),
                section -> createMaterialCondition(section.getString("material"))
        );

        registerCondition(
                "permission",
                Arrays.asList("permission"),
                section -> createPermissionCondition(section.getString("permission"))
        );

        registerCondition(
                "world",
                Arrays.asList("world"),
                section -> createWorldCondition(section.getString("world"))
        );

        registerCondition(
                "region",
                Arrays.asList("region"),
                section -> createRegionCondition(section.getString("region"))
        );

        registerCondition(
                "y-level",
                Arrays.asList("y-min", "y-max"),
                section -> createYLevelCondition(
                        readOptionalInteger(section, "y-min"),
                        readOptionalInteger(section, "y-max")
                )
        );

        registerCondition(
                "interaction-action",
                Arrays.asList("interaction-action"),
                section -> createInteractionActionCondition(
                        section.get("interaction-action")
                )
        );

        registerCondition(
                "cooldown",
                Arrays.asList("cooldown"),
                section -> createCooldownCondition(section.getString("cooldown"))
        );

        registerCondition(
                "on-cooldown",
                Arrays.asList("on-cooldown"),
                section -> createOnCooldownCondition(section.getString("on-cooldown"))
        );
    }

    private Integer readOptionalInteger(ConfigurationSection section, String key) {
        if (!section.contains(key)) {
            return null;
        }

        Object value = section.get(key);
        if (!(value instanceof Integer)) {
            throw new RuleLoadException("Condition '" + key + "' must be a whole number.");
        }

        return (Integer) value;
    }

    private RuleCondition createMaterialCondition(String materialName) {
        String normalizedName = requireText(materialName, "Material condition")
                .toUpperCase(Locale.ROOT);
        Material material = Material.matchMaterial(normalizedName);

        if (material == null) {
            throw new RuleLoadException("Unknown material '" + materialName + "'.");
        }

        return new MaterialCondition(material);
    }

    private RuleCondition createItemCondition(String itemId) {
        String normalizedItemId = requireText(itemId, "Item condition");

        if (services.getItemService().getCustomItem(normalizedItemId) == null) {
            throw new RuleLoadException("Unknown custom item '" + itemId + "'.");
        }

        return new ItemCondition(services.getItemService(), normalizedItemId);
    }

    private RuleCondition createPermissionCondition(String permission) {
        return new PermissionCondition(requireText(permission, "Permission condition"));
    }

    private RuleCondition createWorldCondition(String worldName) {
        return new WorldCondition(requireText(worldName, "World condition"));
    }

    private RuleCondition createRegionCondition(String regionName) {
        return new RegionCondition(requireText(regionName, "Region condition"));
    }

    private RuleCondition createYLevelCondition(Integer minY, Integer maxY) {
        if (minY != null && maxY != null && minY > maxY) {
            throw new RuleLoadException(
                    "Condition 'y-min' cannot be greater than 'y-max'."
            );
        }

        return new YLevelCondition(minY, maxY);
    }

    private RuleCondition createInteractionActionCondition(Object configuredValue) {
        Set<Action> actions = EnumSet.noneOf(Action.class);

        if (configuredValue instanceof String) {
            actions.add(parseInteractionAction((String) configuredValue));
        } else if (configuredValue instanceof List<?>) {
            List<?> configuredActions = (List<?>) configuredValue;

            if (configuredActions.isEmpty()) {
                throw new RuleLoadException("Interaction action list cannot be empty.");
            }

            for (Object actionValue : configuredActions) {
                if (!(actionValue instanceof String)) {
                    throw new RuleLoadException(
                            "Every interaction action must be a string."
                    );
                }

                actions.add(parseInteractionAction((String) actionValue));
            }
        } else {
            throw new RuleLoadException(
                    "Interaction action must be a string or a list."
            );
        }

        return new InteractionActionCondition(actions);
    }

    private Action parseInteractionAction(String actionName) {
        String normalizedAction = requireText(actionName, "Interaction action")
                .toUpperCase(Locale.ROOT);

        try {
            return Action.valueOf(normalizedAction);
        } catch (IllegalArgumentException exception) {
            throw new RuleLoadException(
                    "Unknown interaction action '" + actionName + "'."
            );
        }
    }

    private RuleCondition createCooldownCondition(String cooldownId) {
        String normalizedCooldownId = requireText(cooldownId, "Cooldown condition")
                .toLowerCase(Locale.ROOT);
        return new CooldownCondition(services.getCooldownService(), normalizedCooldownId);
    }

    private RuleCondition createOnCooldownCondition(String cooldownId) {
        String normalizedCooldownId = requireText(cooldownId, "On-cooldown condition")
                .toLowerCase(Locale.ROOT);
        return new OnCooldownCondition(
                services.getCooldownService(),
                normalizedCooldownId
        );
    }

    private void validateKeys(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            if (!supportedKeys.contains(key.toLowerCase(Locale.ROOT))) {
                throw new RuleLoadException("Unknown condition '" + key + "'.");
            }
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null) {
            throw new RuleLoadException(fieldName + " is missing.");
        }

        String normalized = value.trim();

        if (normalized.isEmpty()) {
            throw new RuleLoadException(fieldName + " cannot be empty.");
        }

        return normalized;
    }

    private String normalizeIdentifier(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null.");
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }

        return normalized;
    }
}
