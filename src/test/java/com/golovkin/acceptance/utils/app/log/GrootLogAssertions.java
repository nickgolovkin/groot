package com.golovkin.acceptance.utils.app.log;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GrootLogAssertions {
    private GrootLogAssertions() {
    }

    public static void assertLogEntriesEqual(List<GrootLogEntry> expectedLogEntries, List<GrootLogEntry> actualLogEntries) {
        assertEquals(expectedLogEntries.size(), actualLogEntries.size(), String.format("Ожидалось, что в логах Groot будет %d записей, но получено %d - %s", expectedLogEntries.size(), actualLogEntries.size(), actualLogEntries));

        for (int i = 0; i < expectedLogEntries.size(); i++) {
            GrootLogEntry expectedLogEntry = expectedLogEntries.get(i);
            GrootLogEntry actualLogEntry = actualLogEntries.get(i);

            assertEquals(expectedLogEntry.getLogLevel(), actualLogEntry.getLogLevel(), String.format("Для [%s] ожидался уровень логирования %s, но получен %s", expectedLogEntry.getMessage(), expectedLogEntry.getLogLevel(), actualLogEntry.getLogLevel()));
            assertEquals(expectedLogEntry.getMessage(), actualLogEntry.getMessage());
        }
    }
}
