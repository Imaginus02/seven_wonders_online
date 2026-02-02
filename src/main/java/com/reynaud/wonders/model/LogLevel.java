package com.reynaud.wonders.model;

public enum LogLevel {
    DEBUG(0, "DEBUG"),
    INFO(1, "INFO"),
    WARNING(2, "WARNING"),
    ERROR(3, "ERROR"),
    CRITICAL(4, "CRITICAL");

    private final int severity;
    private final String label;

    LogLevel(int severity, String label) {
        this.severity = severity;
        this.label = label;
    }

    public int getSeverity() {
        return severity;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Check if this log level is at least as severe as the given level
     */
    public boolean isAtLeastAs(LogLevel level) {
        return this.severity >= level.severity;
    }
}
