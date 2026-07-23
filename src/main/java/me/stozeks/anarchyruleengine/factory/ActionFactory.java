package me.stozeks.anarchyruleengine.factory;

import me.stozeks.anarchyruleengine.action.CancelAction;
import me.stozeks.anarchyruleengine.action.CooldownAction;
import me.stozeks.anarchyruleengine.action.MessageAction;
import me.stozeks.anarchyruleengine.action.RemoveItemAction;
import me.stozeks.anarchyruleengine.action.RuleAction;
import me.stozeks.anarchyruleengine.loader.RuleLoadException;
import me.stozeks.anarchyruleengine.service.ActionServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class ActionFactory {

    @FunctionalInterface
    public interface ActionCreator {
        RuleAction create(Map<?, ?> actionData);
    }

    private final ActionServices services;
    private final Map<String, ActionCreator> creators = new LinkedHashMap<>();

    public ActionFactory(ActionServices services) {
        this.services = Objects.requireNonNull(services, "services");
        registerBuiltInActions();
    }

    public void registerAction(String type, ActionCreator creator) {
        String normalizedType = normalizeType(type);
        Objects.requireNonNull(creator, "creator");

        if (creators.containsKey(normalizedType)) {
            throw new IllegalArgumentException(
                    "Action type '" + normalizedType + "' is already registered."
            );
        }

        creators.put(normalizedType, creator);
    }

    public Set<String> getRegisteredActionTypes() {
        return Collections.unmodifiableSet(creators.keySet());
    }

    public List<RuleAction> createActions(List<Map<?, ?>> configuredActions) {
        List<RuleAction> actions = new ArrayList<>();

        if (configuredActions == null) {
            return actions;
        }

        for (int index = 0; index < configuredActions.size(); index++) {
            Map<?, ?> actionData = configuredActions.get(index);

            if (actionData == null) {
                throw new RuleLoadException(
                        "Invalid action at index " + index + ": action cannot be null."
                );
            }

            try {
                actions.add(createAction(actionData));
            } catch (RuleLoadException exception) {
                throw new RuleLoadException(
                        "Invalid action at index " + index + ": " + exception.getMessage(),
                        exception
                );
            } catch (RuntimeException exception) {
                throw new RuleLoadException(
                        "Invalid action at index " + index
                                + ": unexpected error in action creator: "
                                + exception.getMessage(),
                        exception
                );
            }
        }

        return actions;
    }

    private RuleAction createAction(Map<?, ?> actionData) {
        Object typeValue = actionData.get("type");

        if (!(typeValue instanceof String)) {
            throw new RuleLoadException("Action type is missing.");
        }

        String type;

        try {
            type = normalizeType((String) typeValue);
        } catch (IllegalArgumentException exception) {
            throw new RuleLoadException(exception.getMessage(), exception);
        }
        ActionCreator creator = creators.get(type);

        if (creator == null) {
            throw new RuleLoadException(
                    "Unknown action type '" + type + "'. Available types: "
                            + String.join(", ", creators.keySet()) + "."
            );
        }

        RuleAction action = creator.create(actionData);

        if (action == null) {
            throw new RuleLoadException(
                    "Action creator for type '" + type + "' returned null."
            );
        }

        return action;
    }

    private void registerBuiltInActions() {
        registerAction("cancel", actionData -> new CancelAction());

        registerAction(
                "message",
                actionData -> new MessageAction(
                        services.getPlaceholderService(),
                        readMessage(actionData)
                )
        );

        registerAction(
                "remove-item",
                actionData -> new RemoveItemAction(readPositiveAmount(actionData))
        );

        registerAction(
                "cooldown",
                actionData -> new CooldownAction(
                        services.getCooldownService(),
                        services.getItemService(),
                        readCooldownId(actionData),
                        readCooldownSeconds(actionData)
                )
        );
    }

    private String normalizeType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Action type cannot be null.");
        }

        String normalizedType = type.trim().toLowerCase(Locale.ROOT);

        if (normalizedType.isEmpty()) {
            throw new IllegalArgumentException("Action type cannot be empty.");
        }

        return normalizedType;
    }

    private String readMessage(Map<?, ?> actionData) {
        Object messageValue = actionData.get("message");

        if (!(messageValue instanceof String)) {
            throw new RuleLoadException("Message action requires a message.");
        }

        String message = ((String) messageValue).trim();

        if (message.isEmpty()) {
            throw new RuleLoadException("Message action cannot contain an empty message.");
        }

        return message;
    }

    private String readCooldownId(Map<?, ?> actionData) {
        Object idValue = actionData.get("id");

        if (idValue == null) {
            return null;
        }

        if (!(idValue instanceof String)) {
            throw new RuleLoadException("Cooldown id must be a string.");
        }

        String id = ((String) idValue).trim().toLowerCase(Locale.ROOT);

        if (id.isEmpty()) {
            throw new RuleLoadException("Cooldown id cannot be empty.");
        }

        return id;
    }

    private long readCooldownSeconds(Map<?, ?> actionData) {
        Object secondsValue = actionData.get("seconds");

        if (!(secondsValue instanceof Number)) {
            throw new RuleLoadException("Cooldown action requires seconds.");
        }

        long seconds = ((Number) secondsValue).longValue();

        if (seconds <= 0) {
            throw new RuleLoadException("Cooldown seconds must be greater than zero.");
        }

        return seconds;
    }

    private int readPositiveAmount(Map<?, ?> actionData) {
        Object amountValue = actionData.get("amount");

        if (!(amountValue instanceof Number)) {
            throw new RuleLoadException(
                    "Remove-item action requires a numeric amount."
            );
        }

        int amount = ((Number) amountValue).intValue();

        if (amount <= 0) {
            throw new RuleLoadException(
                    "Remove-item amount must be greater than zero."
            );
        }

        return amount;
    }
}
