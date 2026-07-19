package me.stozeks.anarchyruleengine.condition;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import org.bukkit.Material;

public final class MaterialCondition implements RuleCondition {

    private final Material material;

    public MaterialCondition(Material material) {
        this.material = material;
    }

    @Override
    public boolean matches(InteractionContext context) {
        return context.getItem() != null
                && context.getItem().getType() == material;
    }
}