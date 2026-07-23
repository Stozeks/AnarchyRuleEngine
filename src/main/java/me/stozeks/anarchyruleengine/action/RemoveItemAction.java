package me.stozeks.anarchyruleengine.action;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.model.RuleExecutionResult;
import org.bukkit.inventory.ItemStack;

public final class RemoveItemAction implements RuleAction {

    private final int amount;

    public RemoveItemAction(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }

        this.amount = amount;
    }

    @Override
    public void execute(
            InteractionContext context,
            RuleExecutionResult result
    ) {
        ItemStack item = context.getItem();

        if (item == null || item.getType().isAir()) {
            return;
        }

        int remainingAmount = item.getAmount() - amount;

        if (remainingAmount <= 0) {
            item.setAmount(0);
            return;
        }

        item.setAmount(remainingAmount);
    }
}