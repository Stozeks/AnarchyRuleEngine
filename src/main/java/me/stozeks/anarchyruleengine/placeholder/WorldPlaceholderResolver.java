package me.stozeks.anarchyruleengine.placeholder;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import org.bukkit.World;

public final class WorldPlaceholderResolver
        implements PlaceholderResolver {

    @Override
    public boolean supports(String placeholder) {
        return "world".equalsIgnoreCase(placeholder);
    }

    @Override
    public String resolve(
            String placeholder,
            InteractionContext context
    ) {
        World world = context.getLocation().getWorld();

        if (world == null) {
            return "";
        }

        return world.getName();
    }
}