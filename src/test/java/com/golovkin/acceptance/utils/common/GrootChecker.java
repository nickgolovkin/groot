package com.golovkin.acceptance.utils.common;

import com.golovkin.acceptance.utils.app.Groot;
import com.golovkin.acceptance.utils.app.log.GrootLogAssertions;
import com.golovkin.acceptance.utils.app.log.GrootLogEntry;
import com.golovkin.acceptance.utils.git.GitStub;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

/**
 * Класс, в котором собраны все необходимые тестовые методы
 * Используется для повышения читаемости тестов
 */
public class GrootChecker {
    private Groot groot;
    private GitStub gitStub;

    public GrootChecker(Groot groot, GitStub gitStub) {
        this.groot = groot;
        this.gitStub = gitStub;
    }

    public void assertOutputEqual(String... expectedOutput) {
        Assertions.assertEquals(Arrays.asList(expectedOutput), groot.getOutput());
    }

    public void assertGitRequestsEqual(String... expectedGitRequests) {
        Assertions.assertEquals(Arrays.asList(expectedGitRequests), gitStub.readRequestsFromLog());
    }

    public void assertLogsEqual(GrootLogEntry... expectedLogs) {
        GrootLogAssertions.assertLogEntriesEqual(Arrays.asList(expectedLogs), groot.readLogs());
    }
}
