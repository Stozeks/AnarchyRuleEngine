package me.stozeks.anarchyruleengine.placeholder;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import org.bukkit.Location;

import java.util.Locale;

public final class PositionPlaceholderResolver
        implements PlaceholderResolver {

    @Override
    public boolean supports(String placeholder) {
        String normalizedPlaceholder =
                placeholder.toLowerCase(Locale.ROOT);

        return normalizedPlaceholder.equals("x")
                || normalizedPlaceholder.equals("y")
                || normalizedPlaceholder.equals("z");
    }

    @Override
    public String resolve(
            String placeholder,
            InteractionContext context
    ) {
        Location location = context.getLocation();

        switch (placeholder.toLowerCase(Locale.ROOT)) {
            case "x":
                return Integer.toString(location.getBlockX());

            case "y":
                return Integer.toString(location.getBlockY());

            case "z":
                return Integer.toString(location.getBlockZ());

            default:
                return "";
        }
    }
}