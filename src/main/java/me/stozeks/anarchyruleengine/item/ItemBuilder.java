package me.stozeks.anarchyruleengine.item;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ItemBuilder {

    private final NamespacedKey itemIdKey;

    public ItemBuilder(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        this.itemIdKey = new NamespacedKey(plugin, "custom_item_id");
    }

    public ItemStack build(CustomItem customItem) {
        return build(customItem, 1);
    }

    public ItemStack build(CustomItem customItem, int amount) {
        Objects.requireNonNull(customItem, "customItem");

        if (amount < 1) {
            throw new IllegalArgumentException("Item amount must be greater than 0");
        }

        int finalAmount = Math.min(
                amount,
                customItem.getMaterial().getMaxStackSize()
        );

        ItemStack itemStack = new ItemStack(
                customItem.getMaterial(),
                finalAmount
        );

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return itemStack;
        }

        if (customItem.getDisplayName() != null
                && !customItem.getDisplayName().isEmpty()) {

            itemMeta.setDisplayName(
                    colorize(customItem.getDisplayName())
            );
        }

        if (!customItem.getLore().isEmpty()) {
            List<String> coloredLore = new ArrayList<>();

            for (String line : customItem.getLore()) {
                coloredLore.add(colorize(line));
            }

            itemMeta.setLore(coloredLore);
        }

        itemMeta.getPersistentDataContainer().set(
                itemIdKey,
                PersistentDataType.STRING,
                customItem.getId()
        );

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public String getCustomItemId(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return null;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return null;
        }

        return itemMeta.getPersistentDataContainer().get(
                itemIdKey,
                PersistentDataType.STRING
        );
    }

    public boolean isCustomItem(ItemStack itemStack) {
        return getCustomItemId(itemStack) != null;
    }

    private String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}