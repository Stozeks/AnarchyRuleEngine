package me.stozeks.anarchyruleengine.service;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.model.RuleExecutionResult;
import me.stozeks.anarchyruleengine.model.RuleTrace;
import me.stozeks.anarchyruleengine.model.RuleTraceStep;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class DebugService {

    private final Set<UUID> enabledPlayers = new HashSet<>();

    public boolean isEnabled(Player player) {
        return enabledPlayers.contains(player.getUniqueId());
    }

    public boolean setEnabled(Player player, boolean enabled) {
        if (enabled) {
            return enabledPlayers.add(player.getUniqueId());
        }

        return enabledPlayers.remove(player.getUniqueId());
    }

    public void clear() {
        enabledPlayers.clear();
    }

    public void sendTrace(
            Player player,
            InteractionContext context,
            RuleExecutionResult result
    ) {
        if (!isEnabled(player)) {
            return;
        }

        player.sendMessage(ChatColor.DARK_GRAY + "----- "
                + ChatColor.GOLD + "ARE DEBUG"
                + ChatColor.DARK_GRAY + " -----");
        player.sendMessage(ChatColor.GRAY + "Action: " + ChatColor.WHITE
                + context.getInteractionAction().name());
        player.sendMessage(ChatColor.GRAY + "Item: " + ChatColor.WHITE
                + context.getItem().getType().name());
        player.sendMessage(ChatColor.GRAY + "Rules checked: " + ChatColor.WHITE
                + result.getRuleTraces().size());

        for (RuleTrace trace : result.getRuleTraces()) {
            ChatColor statusColor = trace.isMatched() ? ChatColor.GREEN : ChatColor.RED;
            String status = trace.isMatched() ? "MATCHED" : "NOT MATCHED";

            if (trace.isSkipped()) {
                statusColor = ChatColor.GRAY;
                status = "SKIPPED";
            }

            player.sendMessage(
                    ChatColor.YELLOW + trace.getRuleId()
                            + ChatColor.DARK_GRAY + " [priority=" + trace.getPriority() + "] "
                            + statusColor + status
            );

            if (trace.isSkipped()) {
                player.sendMessage(ChatColor.DARK_GRAY + "  -> "
                        + ChatColor.GRAY + trace.getSkipReason());
                continue;
            }

            for (RuleTraceStep step : trace.getSteps()) {
                ChatColor stepColor = step.isSuccessful()
                        ? ChatColor.GREEN
                        : ChatColor.RED;
                String symbol = step.isSuccessful() ? "✔" : "✘";

                if (step.getType() == RuleTraceStep.Type.INFO) {
                    stepColor = ChatColor.GRAY;
                    symbol = "•";
                } else if (step.getType() == RuleTraceStep.Type.ERROR) {
                    stepColor = ChatColor.DARK_RED;
                    symbol = "!";
                }

                String line = ChatColor.DARK_GRAY + "  " + symbol + " "
                        + stepColor + step.getComponent();

                if (step.getDetail() != null && !step.getDetail().trim().isEmpty()) {
                    line += ChatColor.GRAY + " — " + step.getDetail();
                }

                player.sendMessage(line);
            }
        }

        if (result.hasFailed()) {
            player.sendMessage(ChatColor.DARK_RED + "Execution failed: "
                    + ChatColor.RED + result.getFailureMessage());
        } else if (!result.isMatched()) {
            player.sendMessage(ChatColor.GRAY + "No rule matched this interaction.");
        } else {
            player.sendMessage(ChatColor.GRAY + "Matched: " + ChatColor.WHITE
                    + String.join(", ", result.getMatchedRuleIds())
                    + ChatColor.GRAY + "; actions: " + ChatColor.WHITE
                    + result.getExecutedActions());
        }
    }
}
