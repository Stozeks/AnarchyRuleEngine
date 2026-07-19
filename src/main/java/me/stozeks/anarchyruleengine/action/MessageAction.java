package me.stozeks.anarchyruleengine.action;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.model.RuleExecutionResult;
import org.bukkit.ChatColor;

public final class MessageAction implements RuleAction {

    private final String message;

    public MessageAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(
            InteractionContext context,
            RuleExecutionResult result
    ) {
        context.getPlayer().sendMessage(
                ChatColor.translateAlternateColorCodes('&', message)
        );
    }
}