package me.stozeks.anarchyruleengine.listener;

import me.stozeks.anarchyruleengine.engine.RuleEngine;
import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.model.RuleExecutionResult;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public final class PlayerInteractListener implements Listener {

    private final RuleEngine ruleEngine;

    public PlayerInteractListener(RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Location location = event.getClickedBlock() != null
                ? event.getClickedBlock().getLocation()
                : event.getPlayer().getLocation();

        InteractionContext context = new InteractionContext(
                event.getPlayer(),
                event.getItem(),
                location,
                event.getAction()
        );

        RuleExecutionResult result = ruleEngine.evaluate(context);

        if (result.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
