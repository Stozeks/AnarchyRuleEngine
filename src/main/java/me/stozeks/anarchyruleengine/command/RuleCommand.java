package me.stozeks.anarchyruleengine.command;

import me.stozeks.anarchyruleengine.model.RuleReloadResult;
import me.stozeks.anarchyruleengine.service.RuleReloadService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class RuleCommand implements CommandExecutor {

    private static final String RELOAD_PERMISSION =
            "anarchyruleengine.command.reload";

    private final RuleReloadService ruleReloadService;

    public RuleCommand(RuleReloadService ruleReloadService) {
        this.ruleReloadService = ruleReloadService;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] arguments
    ) {
        if (arguments.length == 0) {
            sendUsage(sender);
            return true;
        }

        if (!arguments[0].equalsIgnoreCase("reload")) {
            sendUsage(sender);
            return true;
        }

        if (!sender.hasPermission(RELOAD_PERMISSION)) {
            sender.sendMessage(
                    ChatColor.RED
                            + "You do not have permission to use this command."
            );
            return true;
        }

        RuleReloadResult result =
                ruleReloadService.reloadRules();

        if (result.isSuccessful()) {
            sender.sendMessage(
                    ChatColor.GREEN
                            + "Successfully loaded "
                            + result.getLoadedRuleCount()
                            + " rule(s)."
            );
            return true;
        }

        sender.sendMessage(
                ChatColor.RED
                        + "Could not reload rules: "
                        + result.getErrorMessage()
        );

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(
                ChatColor.YELLOW + "Usage: /are reload"
        );
    }
}