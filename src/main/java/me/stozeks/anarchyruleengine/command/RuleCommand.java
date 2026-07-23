package me.stozeks.anarchyruleengine.command;

import me.stozeks.anarchyruleengine.item.ItemService;
import me.stozeks.anarchyruleengine.model.Rule;
import me.stozeks.anarchyruleengine.model.RuleReloadResult;
import me.stozeks.anarchyruleengine.service.DebugService;
import me.stozeks.anarchyruleengine.service.RuleInspectorService;
import me.stozeks.anarchyruleengine.service.RuleReloadService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class RuleCommand implements TabExecutor {

    private static final String RELOAD_PERMISSION = "anarchyruleengine.command.reload";
    private static final String GIVE_PERMISSION = "anarchyruleengine.command.give";
    private static final String INSPECT_PERMISSION = "anarchyruleengine.command.inspect";
    private static final String DEBUG_PERMISSION = "anarchyruleengine.command.debug";
    private static final int MAX_GIVE_AMOUNT = 2304;

    private final RuleReloadService ruleReloadService;
    private final ItemService itemService;
    private final RuleInspectorService ruleInspectorService;
    private final DebugService debugService;
    private final String pluginVersion;

    public RuleCommand(
            RuleReloadService ruleReloadService,
            ItemService itemService,
            RuleInspectorService ruleInspectorService,
            DebugService debugService,
            String pluginVersion
    ) {
        this.ruleReloadService = Objects.requireNonNull(ruleReloadService, "ruleReloadService");
        this.itemService = Objects.requireNonNull(itemService, "itemService");
        this.ruleInspectorService = Objects.requireNonNull(ruleInspectorService, "ruleInspectorService");
        this.debugService = Objects.requireNonNull(debugService, "debugService");
        this.pluginVersion = Objects.requireNonNull(pluginVersion, "pluginVersion");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (arguments.length == 0 || arguments[0].equalsIgnoreCase("help")) {
            sendUsage(sender);
            return true;
        }

        String subcommand = arguments[0];

        if (subcommand.equalsIgnoreCase("version")) {
            handleVersion(sender);
            return true;
        }

        if (subcommand.equalsIgnoreCase("reload")) {
            handleReload(sender);
            return true;
        }

        if (subcommand.equalsIgnoreCase("give")) {
            handleGive(sender, arguments);
            return true;
        }

        if (subcommand.equalsIgnoreCase("inspect")) {
            handleInspect(sender, arguments);
            return true;
        }

        if (subcommand.equalsIgnoreCase("rules")) {
            handleRules(sender);
            return true;
        }

        if (subcommand.equalsIgnoreCase("debug")) {
            handleDebug(sender, arguments);
            return true;
        }

        sendUsage(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] arguments
    ) {
        if (arguments.length == 1) {
            List<String> subcommands = new ArrayList<>();
            subcommands.add("help");
            subcommands.add("version");

            if (sender.hasPermission(RELOAD_PERMISSION)) {
                subcommands.add("reload");
            }
            if (sender.hasPermission(GIVE_PERMISSION)) {
                subcommands.add("give");
            }
            if (sender.hasPermission(INSPECT_PERMISSION)) {
                subcommands.add("inspect");
                subcommands.add("rules");
            }
            if (sender.hasPermission(DEBUG_PERMISSION)) {
                subcommands.add("debug");
            }

            return filterPrefix(subcommands, arguments[0]);
        }

        if (arguments.length == 2 && arguments[0].equalsIgnoreCase("debug")) {
            List<String> values = new ArrayList<>(Arrays.asList("on", "off", "status"));
            for (Player player : Bukkit.getOnlinePlayers()) {
                values.add(player.getName());
            }
            return filterPrefix(values, arguments[1]);
        }

        if (arguments.length == 3 && arguments[0].equalsIgnoreCase("debug")) {
            return filterPrefix(Arrays.asList("on", "off", "status"), arguments[2]);
        }

        if (arguments.length == 2 && arguments[0].equalsIgnoreCase("inspect")) {
            return filterPrefix(ruleInspectorService.getRuleIds(), arguments[1]);
        }

        if (arguments.length == 2 && arguments[0].equalsIgnoreCase("give")) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return filterPrefix(playerNames, arguments[1]);
        }

        if (arguments.length == 3 && arguments[0].equalsIgnoreCase("give")) {
            return filterPrefix(new ArrayList<>(itemService.getItemRegistry().getItemIds()), arguments[2]);
        }

        return Collections.emptyList();
    }


    private void handleVersion(CommandSender sender) {
        sender.sendMessage(
                ChatColor.GOLD + "AnarchyRuleEngine "
                        + ChatColor.WHITE + "v" + pluginVersion
        );
        sender.sendMessage(
                ChatColor.GRAY + "Paper 1.16.5 | Java 8+ | WorldGuard 7"
        );
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission(RELOAD_PERMISSION)) {
            sendNoPermission(sender);
            return;
        }

        RuleReloadResult result = ruleReloadService.reloadRules();

        if (result.isSuccessful()) {
            sender.sendMessage(
                    ChatColor.GREEN + "Successfully loaded "
                            + result.getLoadedRuleCount() + " rule(s) and "
                            + result.getLoadedItemCount() + " custom item(s); "
                            + result.getDisabledRuleCount() + " rule(s) disabled. Loaded in "
                            + result.getDurationMillis() + " ms."
            );
            return;
        }

        sender.sendMessage(ChatColor.RED + "Could not reload rules: " + result.getErrorMessage());
    }

    private void handleGive(CommandSender sender, String[] arguments) {
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
            sender.sendMessage(ChatColor.RED + "Player '" + playerName + "' is not online.");
            return;
        }

        int amount = 1;

        if (arguments.length == 4) {
            try {
                amount = Integer.parseInt(arguments[3]);
            } catch (NumberFormatException exception) {
                sender.sendMessage(ChatColor.RED + "Amount must be a whole number.");
                return;
            }
        }

        if (amount < 1 || amount > MAX_GIVE_AMOUNT) {
            sender.sendMessage(
                    ChatColor.RED + "Amount must be between 1 and " + MAX_GIVE_AMOUNT + "."
            );
            return;
        }

        boolean successful = itemService.give(target, itemId, amount);

        if (!successful) {
            sender.sendMessage(ChatColor.RED + "Custom item '" + itemId + "' does not exist.");
            return;
        }

        sender.sendMessage(
                ChatColor.GREEN + "Gave " + amount + "x " + itemId + " to " + target.getName() + "."
        );

        if (!sender.equals(target)) {
            target.sendMessage(ChatColor.GREEN + "You received " + amount + "x " + itemId + ".");
        }
    }


    private void handleDebug(CommandSender sender, String[] arguments) {
        if (!sender.hasPermission(DEBUG_PERMISSION)) {
            sendNoPermission(sender);
            return;
        }

        Player target;
        String mode;

        if (arguments.length == 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /are debug <player> <on|off|status>");
                return;
            }

            target = (Player) sender;
            mode = arguments[1];
        } else if (arguments.length == 3) {
            target = Bukkit.getPlayerExact(arguments[1]);
            mode = arguments[2];

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + arguments[1] + "' is not online.");
                return;
            }
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /are debug [player] <on|off|status>");
            return;
        }

        if (mode.equalsIgnoreCase("status")) {
            sender.sendMessage(
                    ChatColor.YELLOW + "Debug for " + target.getName() + ": "
                            + (debugService.isEnabled(target)
                            ? ChatColor.GREEN + "enabled"
                            : ChatColor.RED + "disabled")
            );
            return;
        }

        if (!mode.equalsIgnoreCase("on") && !mode.equalsIgnoreCase("off")) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /are debug [player] <on|off|status>");
            return;
        }

        boolean enabled = mode.equalsIgnoreCase("on");
        debugService.setEnabled(target, enabled);

        sender.sendMessage(
                ChatColor.GREEN + "Debug " + (enabled ? "enabled" : "disabled")
                        + " for " + target.getName() + "."
        );

        if (!sender.equals(target)) {
            target.sendMessage(
                    ChatColor.GOLD + "AnarchyRuleEngine debug was "
                            + (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled")
                            + ChatColor.GOLD + " for you."
            );
        }
    }

    private void handleInspect(CommandSender sender, String[] arguments) {
        if (!sender.hasPermission(INSPECT_PERMISSION)) {
            sendNoPermission(sender);
            return;
        }

        if (arguments.length != 2) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /are inspect <ruleId>");
            return;
        }

        Rule rule = ruleInspectorService.findRule(arguments[1]);

        if (rule == null) {
            sender.sendMessage(ChatColor.RED + "Rule '" + arguments[1] + "' does not exist.");
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "----- AnarchyRuleEngine rule -----");
        sender.sendMessage(ChatColor.YELLOW + "ID: " + ChatColor.WHITE + rule.getId());
        sender.sendMessage(
                ChatColor.YELLOW + "Enabled: "
                        + (rule.isEnabled() ? ChatColor.GREEN + "yes" : ChatColor.RED + "no")
        );
        sender.sendMessage(ChatColor.YELLOW + "Priority: " + ChatColor.WHITE + rule.getPriority());
        sender.sendMessage(
                ChatColor.YELLOW + "Stop processing: "
                        + (rule.shouldStopProcessing() ? ChatColor.GREEN + "yes" : ChatColor.GRAY + "no")
        );
        sender.sendMessage(
                ChatColor.YELLOW + "Conditions (" + rule.getConditions().size() + "): "
                        + ChatColor.WHITE + String.join(", ", ruleInspectorService.describeConditions(rule))
        );
        sender.sendMessage(
                ChatColor.YELLOW + "Actions (" + rule.getActions().size() + "): "
                        + ChatColor.WHITE + String.join(", ", ruleInspectorService.describeActions(rule))
        );
    }

    private void handleRules(CommandSender sender) {
        if (!sender.hasPermission(INSPECT_PERMISSION)) {
            sendNoPermission(sender);
            return;
        }

        List<String> ruleIds = ruleInspectorService.getRuleIds();

        if (ruleIds.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No rules are currently loaded.");
            return;
        }

        sender.sendMessage(
                ChatColor.GOLD + "Loaded rules (" + ruleIds.size() + "): "
                        + ChatColor.WHITE + String.join(", ", ruleIds)
        );
    }

    private List<String> filterPrefix(List<String> values, String prefix) {
        String normalizedPrefix = prefix.toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();

        for (String value : values) {
            if (value.toLowerCase(Locale.ROOT).startsWith(normalizedPrefix)) {
                result.add(value);
            }
        }

        result.sort(String.CASE_INSENSITIVE_ORDER);
        return result;
    }

    private void sendNoPermission(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
    }

    private void sendGiveUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Usage: /are give <player> <itemId> [amount]");
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "AnarchyRuleEngine commands:");
        sender.sendMessage(ChatColor.YELLOW + "/are help");
        sender.sendMessage(ChatColor.YELLOW + "/are version");

        if (sender.hasPermission(RELOAD_PERMISSION)) {
            sender.sendMessage(ChatColor.YELLOW + "/are reload");
        }
        if (sender.hasPermission(GIVE_PERMISSION)) {
            sender.sendMessage(ChatColor.YELLOW + "/are give <player> <itemId> [amount]");
        }
        if (sender.hasPermission(INSPECT_PERMISSION)) {
            sender.sendMessage(ChatColor.YELLOW + "/are rules");
            sender.sendMessage(ChatColor.YELLOW + "/are inspect <ruleId>");
        }
        if (sender.hasPermission(DEBUG_PERMISSION)) {
            sender.sendMessage(ChatColor.YELLOW + "/are debug [player] <on|off|status>");
        }
    }
}
