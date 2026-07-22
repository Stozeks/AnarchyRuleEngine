package me.stozeks.anarchyruleengine.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

public final class ItemService {

    private final ItemRegistry itemRegistry;
    private final ItemBuilder itemBuilder;

    public ItemService(
            ItemRegistry itemRegistry,
            ItemBuilder itemBuilder
    ) {
        this.itemRegistry = Objects.requireNonNull(
                itemRegistry,
                "itemRegistry"
        );

        this.itemBuilder = Objects.requireNonNull(
                itemBuilder,
                "itemBuilder"
        );
    }

    public CustomItem getCustomItem(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            return null;
        }

        return itemRegistry.get(itemId);
    }

    public ItemStack create(String itemId) {
        return create(itemId, 1);
    }

    public ItemStack create(String itemId, int amount) {
        CustomItem customItem = getCustomItem(itemId);

        if (customItem == null) {
            return null;
        }

        if (amount < 1) {
            throw new IllegalArgumentException(
                    "Item amount must be greater than 0"
            );
        }

        return itemBuilder.build(customItem, amount);
    }

    public boolean give(
            Player player,
            String itemId,
            int amount
    ) {
        Objects.requireNonNull(player, "player");

        CustomItem customItem = getCustomItem(itemId);

        if (customItem == null) {
            return false;
        }

        if (amount < 1) {
            throw new IllegalArgumentException(
                    "Item amount must be greater than 0"
            );
        }

        int remainingAmount = amount;
        int maxStackSize = customItem
                .getMaterial()
                .getMaxStackSize();

        while (remainingAmount > 0) {
            int currentStackSize = Math.min(
                    remainingAmount,
                    maxStackSize
            );

            ItemStack itemStack = itemBuilder.build(
                    customItem,
                    currentStackSize
            );

            Map<Integer, ItemStack> leftovers =
                    player.getInventory().addItem(itemStack);

            for (ItemStack leftover : leftovers.values()) {
                player.getWorld().dropItemNaturally(
                        player.getLocation(),
                        leftover
                );
            }

            remainingAmount -= currentStackSize;
        }

        return true;
    }

    public boolean isCustomItem(ItemStack itemStack) {
        return itemBuilder.isCustomItem(itemStack);
    }

    public String getCustomItemId(ItemStack itemStack) {
        return itemBuilder.getCustomItemId(itemStack);
    }
}