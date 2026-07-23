package me.stozeks.anarchyruleengine.listener;

import me.stozeks.anarchyruleengine.engine.RuleEngine;
import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.model.RuleExecutionResult;
import me.stozeks.anarchyruleengine.service.DebugService;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;

public final class PlayerInteractListener implements Listener {

    private final RuleEngine ruleEngine;
    private final DebugService debugService;

    public PlayerInteractListener(
            RuleEngine ruleEngine,
            DebugService debugService
    ) {
        this.ruleEngine = Objects.requireNonNull(ruleEngine, "ruleEngine");
        this.debugService = Objects.requireNonNull(debugService, "debugService");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR
                && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getItem() == null || event.getItem().getType().isAir()) {
            return;
        }

        Location location = event.getClickedBlock() != null
                ? event.getClickedBlock().getLocation()
                : event.getPlayer().getLocation();

        InteractionContext context = new InteractionContext(
                event.getPlayer(),
                event.getItem(),
                location,
                action
        );

        RuleExecutionResult result = ruleEngine.evaluate(
                context,
                debugService.isEnabled(event.getPlayer())
        );

        if (result.isCancelled()) {
            event.setCancelled(true);
        }

        debugService.sendTrace(event.getPlayer(), context, result);
    }
}
