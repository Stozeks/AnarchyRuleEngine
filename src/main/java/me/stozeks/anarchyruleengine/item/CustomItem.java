package me.stozeks.anarchyruleengine.item;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CustomItem {

    private final String id;
    private final Material material;
    private final String displayName;
    private final List<String> lore;

    public CustomItem(
            String id,
            Material material,
            String displayName,
            List<String> lore
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.material = Objects.requireNonNull(material, "material");
        this.displayName = Objects.requireNonNull(
                displayName,
                "displayName"
        );
        this.lore = Collections.unmodifiableList(
                new ArrayList<>(
                        Objects.requireNonNull(lore, "lore")
                )
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

    public List<String> getLore() {
        return lore;
    }
}