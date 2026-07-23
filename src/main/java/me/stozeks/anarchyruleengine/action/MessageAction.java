package me.stozeks.anarchyruleengine.action;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.model.RuleExecutionResult;
import me.stozeks.anarchyruleengine.placeholder.PlaceholderService;
import org.bukkit.ChatColor;

public final class MessageAction implements RuleAction {

    private final String message;
    private final PlaceholderService placeholderService;

    public MessageAction(
            PlaceholderService placeholderService,
            String message
    ) {
        this.placeholderService = placeholderService;
        this.message = message;
    }

    @Override
    public void execute(
            InteractionContext context,
            RuleExecutionResult result
    ) {
        String text =
                placeholderService.replace(
                        message,
                        context
                );

        context.getPlayer().sendMessage(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        text
                )
        );
    }
}