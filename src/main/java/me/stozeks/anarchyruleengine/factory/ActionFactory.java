package me.stozeks.anarchyruleengine.factory;

import me.stozeks.anarchyruleengine.action.CancelAction;
import me.stozeks.anarchyruleengine.action.MessageAction;
import me.stozeks.anarchyruleengine.action.RemoveItemAction;
import me.stozeks.anarchyruleengine.action.CooldownAction;
import me.stozeks.anarchyruleengine.action.RuleAction;
import me.stozeks.anarchyruleengine.loader.RuleLoadException;
import me.stozeks.anarchyruleengine.service.ActionServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class ActionFactory {

    private final ActionServices services;

    public ActionFactory(ActionServices services) {
        this.services = Objects.requireNonNull(
                services,
                "services"
        );
    }

    public List<RuleAction> createActions(
            List<Map<?, ?>> configuredActions
    ) {
        List<RuleAction> actions = new ArrayList<>();

        for (int index = 0; index < configuredActions.size(); index++) {
            Map<?, ?> actionData = configuredActions.get(index);

            try {
                actions.add(createAction(actionData));
            } catch (RuleLoadException exception) {
                throw new RuleLoadException(
                        "Invalid action at index " + index
                                + ": " + exception.getMessage(),
                        exception
                );
            }
        }

        return actions;
    }

    private RuleAction createAction(Map<?, ?> actionData) {
        Object typeValue = actionData.get("type");

        if (!(typeValue instanceof String)) {
            throw new RuleLoadException(
                    "Action type is missing."
            );
        }

        String type = ((String) typeValue)
                .trim()
                .toLowerCase(Locale.ROOT);

        switch (type) {
            case "cancel":
                return new CancelAction();

            case "message":
                return new MessageAction(
                        services.getPlaceholderService(),
                        readMessage(actionData)
                );

            case "remove-item":
                return new RemoveItemAction(
                        readPositiveAmount(actionData)
                );

            case "cooldown":
                return new CooldownAction(
                        services.getCooldownService(),
                        services.getItemService(),
                        readCooldownId(actionData),
                        readCooldownSeconds(actionData)
                );

            default:
                throw new RuleLoadException(
                        "Unknown action type '" + type + "'."
                );
        }
    }

    private String readMessage(Map<?, ?> actionData) {
        Object messageValue = actionData.get("message");

        if (!(messageValue instanceof String)) {
            throw new RuleLoadException(
                    "Message action requires a message."
            );
        }

        String message = ((String) messageValue).trim();

        if (message.isEmpty()) {
            throw new RuleLoadException(
                    "Message action cannot contain an empty message."
            );
        }

        return message;
    }

    private String readCooldownId(
            Map<?, ?> actionData
    ) {
        Object idValue = actionData.get("id");

        if (idValue == null) {
            return null;
        }

        if (!(idValue instanceof String)) {
            throw new RuleLoadException(
                    "Cooldown id must be a string."
            );
        }

        String id = ((String) idValue)
                .trim()
                .toLowerCase(Locale.ROOT);

        if (id.isEmpty()) {
            throw new RuleLoadException(
                    "Cooldown id cannot be empty."
            );
        }

        return id;
    }

    private long readCooldownSeconds(
            Map<?, ?> actionData
    ) {
        Object secondsValue = actionData.get("seconds");

        if (!(secondsValue instanceof Number)) {
            throw new RuleLoadException(
                    "Cooldown action requires seconds."
            );
        }

        long seconds = ((Number) secondsValue).longValue();

        if (seconds <= 0) {
            throw new RuleLoadException(
                    "Cooldown seconds must be greater than zero."
            );
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