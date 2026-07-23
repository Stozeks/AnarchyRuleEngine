package me.stozeks.anarchyruleengine.item;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ItemRegistry {

    private final Map<String, CustomItem> items = new LinkedHashMap<>();

    public void register(CustomItem item) {
        items.put(item.getId().toLowerCase(), item);
    }

    public CustomItem get(String id) {
        if (id == null) {
            return null;
        }

        return items.get(id.toLowerCase());
    }

    public boolean contains(String id) {
        return get(id) != null;
    }

    public Collection<CustomItem> getAll() {
        return Collections.unmodifiableCollection(items.values());
    }

    public Set<String> getItemIds() {
        return Collections.unmodifiableSet(items.keySet());
    }

    public void clear() {
        items.clear();
    }
}