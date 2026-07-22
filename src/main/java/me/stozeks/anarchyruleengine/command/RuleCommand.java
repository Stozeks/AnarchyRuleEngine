package me.stozeks.anarchyruleengine.command;

import me.stozeks.anarchyruleengine.item.ItemService;
import me.stozeks.anarchyruleengine.model.RuleReloadResult;
import me.stozeks.anarchyruleengine.service.RuleReloadService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class RuleCommand implements CommandExecutor {

    private static final String RELOAD_PERMISSION =
            "anarchyruleengine.command.reload";

    private static final String GIVE_PERMISSION =
            "anarchyruleengine.command.give";

    private static final int MAX_GIVE_AMOUNT = 2304;

    private final RuleReloadService ruleReloadService;
    private final ItemService itemService;

    public RuleCommand(
            RuleReloadService ruleReloadService,
            ItemService itemService
    ) {
        this.ruleReloadService = Objects.requireNonNull(
                ruleReloadService,
                "ruleReloadService"
        );

        this.itemService = Objects.requireNonNull(
                itemService,
                "itemService"
        );
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

        String subcommand = arguments[0];

        if (subcommand.equalsIgnoreCase("reload")) {
            handleReload(sender);
            return true;
        }

        if (subcommand.equalsIgnoreCase("give")) {
            handleGive(sender, arguments);
            return true;
        }

        sendUsage(sender);
        return true;
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission(RELOAD_PERMISSION)) {
            sendNoPermission(sender);
            return;
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
            return;
        }

        sender.sendMessage(
                ChatColor.RED
                        + "Could not reload rules: "
                        + result.getErrorMessage()
        );
    }

    private void handleGive(
            CommandSender sender,
            String[] arguments
    ) {
        if (!sender.hasPermission(GIVE_PERMISSION)) {
            sendNoPermission(sender);
            return;
        }

        if (arguments.length < 3 || arguments.length > 4) {
            sendGiveUsage(sender);
            return;
        }

        String playerName = arguments[1];
        String itemId = arguments[2];

        Player target = Bukkit.getPlayerExact(playerName);

        if (target == null) {
            sender.sendMessage(
                    ChatColor.RED
                            + "Player '"
                            + playerName
                            + "' is not online."
            );
            return;
        }

        int amount = 1;

        if (arguments.length == 4) {
            try {
                amount = Integer.parseInt(arguments[3]);
            } catch (NumberFormatException exception) {
                sender.sendMessage(
                        ChatColor.RED
                                + "Amount must be a whole number."
                );
                return;
            }
        }

        if (amount < 1 || amount > MAX_GIVE_AMOUNT) {
            sender.sendMessage(
                    ChatColor.RED
                            + "Amount must be between 1 and "
                            + MAX_GIVE_AMOUNT
                            + "."
            );
            return;
        }

        boolean successful = itemService.give(
                target,
                itemId,
                amount
        );

        if (!successful) {
            sender.sendMessage(
                    ChatColor.RED
                            + "Custom item '"
                            + itemId
                            + "' does not exist."
            );
            return;
        }

        sender.sendMessage(
                ChatColor.GREEN
                        + "Gave "
                        + amount
                        + "x "
                        + itemId
                        + " to "
                        + target.getName()
                        + "."
        );

        if (!sender.equals(target)) {
            target.sendMessage(
                    ChatColor.GREEN
                            + "You received "
                            + amount
                            + "x "
                            + itemId
                            + "."
            );
        }
    }

    private void sendNoPermission(CommandSender sender) {
        sender.sendMessage(
                ChatColor.RED
                        + "You do not have permission to use this command."
        );
    }

    private void sendGiveUsage(CommandSender sender) {
        sender.sendMessage(
                ChatColor.YELLOW
                        + "Usage: /are give <player> <itemId> [amount]"
        );
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(
                ChatColor.YELLOW + "AnarchyRuleEngine commands:"
        );

        sender.sendMessage(
                ChatColor.YELLOW + "/are reload"
        );

        sender.sendMessage(
                ChatColor.YELLOW
                        + "/are give <player> <itemId> [amount]"
        );
    }
}