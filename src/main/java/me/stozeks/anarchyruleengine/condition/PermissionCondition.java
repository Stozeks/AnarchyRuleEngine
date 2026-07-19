package me.stozeks.anarchyruleengine.condition;

import me.stozeks.anarchyruleengine.model.InteractionContext;

public final class PermissionCondition implements RuleCondition {

    private final String permission;

    public PermissionCondition(String permission) {
        this.permission = permission;
    }

    @Override
    public boolean matches(InteractionContext context) {
        return context.getPlayer().hasPermission(permission);
    }
}