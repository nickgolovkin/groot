package com.golovkin.acceptance.utils.common;

import com.golovkin.acceptance.utils.app.Groot;
import com.golovkin.acceptance.utils.app.log.GrootLogAssertions;
import com.golovkin.acceptance.utils.app.log.GrootLogEntry;
import com.golovkin.acceptance.utils.git.GitStub;
import com.golovkin.acceptance.utils.http.HttpStub;
import com.golovkin.acceptance.utils.http.log.HttpRequest;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс, в котором собраны все необходимые тестовые методы
 * Используется для повышения читаемости тестов
 */
public class GrootChecker {
    private Groot groot;
    private GitStub gitStub;
    private HttpStub httpStub;

    public GrootChecker(Groot groot, GitStub gitStub, HttpStub httpStub) {
        this.groot = groot;
        this.gitStub = gitStub;
        this.httpStub = httpStub;
    }

    public void assertOutputEqual(String... expectedOutput) {
        Assertions.assertEquals(Arrays.asList(expectedOutput), groot.getOutput());
    }

    public void assertHttpRequestsEqual(HttpRequest... expectedHttpRequests) {
        List<HttpRequest> actualHttpRequests = httpStub.readRequestsFromLog();
        assertEquals(expectedHttpRequests.length, actualHttpRequests.size(), String.format("Ожидалось, что будет %d http-запросов, но получено %d - %s", expectedHttpRequests.length, actualHttpRequests.size(), actualHttpRequests));

        for (int i = 0; i < expectedHttpRequests.length; i++) {
            HttpRequest expectedLogEntry = expectedHttpRequests[i];
            HttpRequest actualLogEntry = actualHttpRequests.get(i);

            assertEquals(expectedLogEntry.getRequestPath(), actualLogEntry.getRequestPath());
            assertEquals(expectedLogEntry.getRequestMethod(), actualLogEntry.getRequestMethod());
            assertEquals(expectedLogEntry.getRequestBody(), actualLogEntry.getRequestBody());
            assertEquals(expectedLogEntry.getRequestQueryParams().asMap(), actualLogEntry.getRequestQueryParams().asMap());
        }
    }

    public void assertGitRequestsEqual(String... expectedGitRequests) {
        Assertions.assertEquals(Arrays.asList(expectedGitRequests), gitStub.readRequestsFromLog());
    }

    public void assertLogsEqual(GrootLogEntry... expectedLogs) {
        GrootLogAssertions.assertLogEntriesEqual(Arrays.asList(expectedLogs), groot.readLogs());
    }
}
