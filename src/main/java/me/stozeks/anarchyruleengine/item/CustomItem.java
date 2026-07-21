package me.stozeks.anarchyruleengine.item;

import org.bukkit.Material;

import java.util.Objects;

public final class CustomItem {

    private final String id;
    private final Material material;
    private final String displayName;

    public CustomItem(
            String id,
            Material material,
            String displayName
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.material = Objects.requireNonNull(material, "material");
        this.displayName = Objects.requireNonNull(
                displayName,
                "displayName"
        );
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }
}