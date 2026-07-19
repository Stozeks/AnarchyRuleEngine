package me.stozeks.anarchyruleengine.model;

public final class RuleReloadResult {

    private final boolean successful;
    private final int loadedRuleCount;
    private final String errorMessage;

    private RuleReloadResult(
            boolean successful,
            int loadedRuleCount,
            String errorMessage
    ) {
        this.successful = successful;
        this.loadedRuleCount = loadedRuleCount;
        this.errorMessage = errorMessage;
    }

    public static RuleReloadResult success(int loadedRuleCount) {
        return new RuleReloadResult(
                true,
                loadedRuleCount,
                null
        );
    }

    public static RuleReloadResult failure(String errorMessage) {
        return new RuleReloadResult(
                false,
                0,
                errorMessage
        );
    }

    public boolean isSuccessful() {
        return successful;
    }

    public int getLoadedRuleCount() {
        return loadedRuleCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}