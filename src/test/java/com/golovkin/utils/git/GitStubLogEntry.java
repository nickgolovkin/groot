package com.golovkin.utils.git;

import java.time.LocalDateTime;

public class GitStubLogEntry {
    private final LocalDateTime dateTime;
    private final String request;
    private final String response;
    private final int exitCode;

    public GitStubLogEntry(LocalDateTime dateTime, String request, String response, int exitCode) {
        this.dateTime = dateTime;
        this.request = request;
        this.response = response;
        this.exitCode = exitCode;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getRequest() {
        return request;
    }

    public String getResponse() {
        return response;
    }

    public int getExitCode() {
        return exitCode;
    }
}
