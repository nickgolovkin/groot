package com.golovkin.acceptance.utils.app.log;

import java.time.LocalDateTime;

public class GrootLogEntry {
    private final LocalDateTime dateTime;
    private final String message;

    public GrootLogEntry(LocalDateTime dateTime, String message) {
        this.dateTime = dateTime;
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getMessage() {
        return message;
    }
}
