package me.stozeks.anarchyruleengine.condition;

import me.stozeks.anarchyruleengine.model.InteractionContext;

public final class WorldCondition implements RuleCondition {

    private final String worldName;

    public WorldCondition(String worldName) {
        this.worldName = worldName;
    }

    @Override
    public boolean matches(InteractionContext context) {
        return context.getPlayer()
                .getWorld()
                .getName()
                .equalsIgnoreCase(worldName);
    }
}