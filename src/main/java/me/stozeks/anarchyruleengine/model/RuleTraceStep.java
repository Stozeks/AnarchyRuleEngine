package me.stozeks.anarchyruleengine.model;

import java.util.Objects;

public final class RuleTraceStep {

    public enum Type {
        CONDITION,
        ACTION,
        INFO,
        ERROR
    }

    private final Type type;
    private final String component;
    private final boolean successful;
    private final String detail;

    public RuleTraceStep(
            Type type,
            String component,
            boolean successful,
            String detail
    ) {
        this.type = Objects.requireNonNull(type, "type");
        this.component = Objects.requireNonNull(component, "component");
        this.successful = successful;
        this.detail = detail;
    }

    public Type getType() {
        return type;
    }

    public String getComponent() {
        return component;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getDetail() {
        return detail;
    }
}
