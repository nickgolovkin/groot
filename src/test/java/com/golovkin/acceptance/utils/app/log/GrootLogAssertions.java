package com.golovkin.acceptance.utils.app.log;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GrootLogAssertions {
    private GrootLogAssertions() {
    }

    public static void assertLogEntriesEqual(List<GrootLogEntry> expectedLogEntries, List<GrootLogEntry> actualLogEntries) {
        assertEquals(expectedLogEntries.size(), actualLogEntries.size());

        for (int i = 0; i < expectedLogEntries.size(); i++) {
            GrootLogEntry expectedLogEntry = expectedLogEntries.get(i);
            GrootLogEntry actualLogEntry = actualLogEntries.get(i);

            assertEquals(expectedLogEntry.getLogLevel(), actualLogEntry.getLogLevel());
            assertEquals(expectedLogEntry.getMessage(), actualLogEntry.getMessage());
        }
    }
}
