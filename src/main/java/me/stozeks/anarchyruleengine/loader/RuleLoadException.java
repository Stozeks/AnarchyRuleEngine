package me.stozeks.anarchyruleengine.loader;

public final class RuleLoadException extends RuntimeException {

    public RuleLoadException(String message) {
        super(message);
    }

    public RuleLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}