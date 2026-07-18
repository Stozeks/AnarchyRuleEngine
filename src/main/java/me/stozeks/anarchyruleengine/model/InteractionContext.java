package me.stozeks.anarchyruleengine.model;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public final class InteractionContext {

    private final Player player;
    private final ItemStack item;
    private final Location location;
    private final Action interactionAction;

    public InteractionContext(
            Player player,
            ItemStack item,
            Location location,
            Action interactionAction
    ) {
        this.player = player;
        this.item = item;
        this.location = location;
        this.interactionAction = interactionAction;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public Location getLocation() {
        return location;
    }

    public Action getInteractionAction() {
        return interactionAction;
    }
}