package me.stozeks.anarchyruleengine.condition;

import me.stozeks.anarchyruleengine.model.InteractionContext;

public class YLevelCondition implements RuleCondition {

    private final Integer minY;
    private final Integer maxY;

    public YLevelCondition(Integer minY, Integer maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public boolean matches(InteractionContext context) {
        int y = context.getLocation().getBlockY();

        if (minY != null && y < minY) {
            return false;
        }

        if (maxY != null && y > maxY) {
            return false;
        }

        return true;
    }
}