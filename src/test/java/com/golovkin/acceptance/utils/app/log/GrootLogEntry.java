package com.golovkin.acceptance.utils.app.log;

import com.golovkin.acceptance.utils.common.log.LogLevel;

import java.time.LocalDateTime;

public class GrootLogEntry {
    private final LocalDateTime dateTime;
    private final LogLevel logLevel;
    private final String message;

    public GrootLogEntry(LogLevel logLevel, String message) {
        this(null, logLevel, message);
    }

    public GrootLogEntry(LocalDateTime dateTime, LogLevel logLevel, String message) {
        this.dateTime = dateTime;
        this.logLevel = logLevel;
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s", logLevel, dateTime, message);
    }
}
