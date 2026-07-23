package me.stozeks.anarchyruleengine.model;

public final class RuleReloadResult {

    private final boolean successful;
    private final int loadedRuleCount;
    private final int loadedItemCount;
    private final int disabledRuleCount;
    private final long durationMillis;
    private final String errorMessage;

    private RuleReloadResult(
            boolean successful,
            int loadedRuleCount,
            int loadedItemCount,
            int disabledRuleCount,
            long durationMillis,
            String errorMessage
    ) {
        this.successful = successful;
        this.loadedRuleCount = loadedRuleCount;
        this.loadedItemCount = loadedItemCount;
        this.disabledRuleCount = disabledRuleCount;
        this.durationMillis = durationMillis;
        this.errorMessage = errorMessage;
    }

    public static RuleReloadResult success(
            int loadedRuleCount,
            int loadedItemCount,
            int disabledRuleCount,
            long durationMillis
    ) {
        return new RuleReloadResult(
                true,
                loadedRuleCount,
                loadedItemCount,
                disabledRuleCount,
                durationMillis,
                null
        );
    }

    public static RuleReloadResult failure(String errorMessage) {
        return new RuleReloadResult(
                false,
                0,
                0,
                0,
                0L,
                errorMessage
        );
    }

    public boolean isSuccessful() {
        return successful;
    }

    public int getLoadedRuleCount() {
        return loadedRuleCount;
    }

    public int getLoadedItemCount() {
        return loadedItemCount;
    }

    public int getDisabledRuleCount() {
        return disabledRuleCount;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
