package me.stozeks.anarchyruleengine.factory;

import me.stozeks.anarchyruleengine.action.CancelAction;
import me.stozeks.anarchyruleengine.action.MessageAction;
import me.stozeks.anarchyruleengine.action.RuleAction;
import me.stozeks.anarchyruleengine.loader.RuleLoadException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ActionFactory {

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
                return new MessageAction(readMessage(actionData));

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
}