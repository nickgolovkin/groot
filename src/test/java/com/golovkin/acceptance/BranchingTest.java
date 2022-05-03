package com.golovkin.acceptance;

import com.golovkin.acceptance.utils.app.log.GrootLogEntry;
import com.golovkin.acceptance.utils.common.log.LogLevel;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.golovkin.acceptance.utils.app.log.GrootLogAssertions.assertLogEntriesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Branching")
public class BranchingTest extends AbstractAcceptanceTest {

    @DisplayName("new branch")
    @Nested
    public class NewBranch {
        @Test
        public void successfully_created() {
            gitStub().add("--git-dir (.+) checkout -b (.+)", "Switched to a new branch '$2'", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("new branch sample_branch");

            List<String> expectedOutput = Lists.newArrayList(
                    "Создаю ветку [sample_branch]",
                    "[omniutils] Ветка [sample_branch] успешно создана",
                    "[omniloan] Ветка [sample_branch] успешно создана",
                    "Создание ветки [sample_branch] завершено"
            );
            List<String> actualOutput = groot().getOutput();
            assertEquals(expectedOutput, actualOutput);

            List<String> expectedGitRequests = Lists.newArrayList(
                    "--git-dir omniutils_dir/.git checkout -b sample_branch",
                    "--git-dir omniloan_dir/.git checkout -b sample_branch"
            );
            List<String> actualGitRequests = gitStub().readRequestsFromLog();
            assertEquals(expectedGitRequests, actualGitRequests);

            List<GrootLogEntry> expectedGrootLogs = Lists.newArrayList(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Создание ветки [sample_branch]. Ветка успешно создана. Команды - [--git-dir omniutils_dir/.git checkout -b sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Создание ветки [sample_branch]. Ветка успешно создана. Команды - [--git-dir omniloan_dir/.git checkout -b sample_branch]")
            );
            List<GrootLogEntry> actualGrootLogs = groot().readLogs();
            assertLogEntriesEqual(expectedGrootLogs, actualGrootLogs);
        }

        @Test
        public void branch_already_exists_in_one_project() {
            gitStub().add("--git-dir omniutils_dir/\\.git checkout -b (.+)", "fatal: A branch named '$1' already exists.", 1)
                    .add("--git-dir omniloan_dir/\\.git checkout -b (.+)", "Switched to a new branch '$1'", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("new branch sample_branch");

            List<String> expectedOutput = Lists.newArrayList(
                    "Создаю ветку [sample_branch]",
                    "[omniutils] Ветка [sample_branch] уже существует",
                    "[omniloan] Ветка [sample_branch] успешно создана",
                    "Создание ветки [sample_branch] завершено"
            );
            List<String> actualOutput = groot().getOutput();
            assertEquals(expectedOutput, actualOutput);

            List<String> expectedGitRequests = Lists.newArrayList(
                    "--git-dir omniutils_dir/.git checkout -b sample_branch",
                    "--git-dir omniloan_dir/.git checkout -b sample_branch"
            );
            List<String> actualGitRequests = gitStub().readRequestsFromLog();
            assertEquals(expectedGitRequests, actualGitRequests);

            List<GrootLogEntry> expectedGrootLogs = Lists.newArrayList(
                    new GrootLogEntry(LogLevel.WARN, "[omniutils] Создание ветки [sample_branch]. Ветка уже существует. Команды - [--git-dir omniutils_dir/.git checkout -b sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Создание ветки [sample_branch]. Ветка успешно создана. Команды - [--git-dir omniloan_dir/.git checkout -b sample_branch]")
            );
            List<GrootLogEntry> actualGrootLogs = groot().readLogs();
            assertLogEntriesEqual(expectedGrootLogs, actualGrootLogs);
        }

        @Test
        public void cannot_create_branch_in_one_project() {
            gitStub().add("--git-dir omniutils_dir/\\.git checkout -b (.+)", "some unexpected error\nnew line", 2)
                    .add("--git-dir omniloan_dir/\\.git checkout -b (.+)", "Switched to a new branch '$1'", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("new branch sample_branch");

            List<String> expectedOutput = Lists.newArrayList(
                    "Создаю ветку [sample_branch]",
                    "[omniutils] Не удалось создать ветку [sample_branch]",
                    "[omniloan] Ветка [sample_branch] успешно создана",
                    "Создание ветки [sample_branch] завершено"
            );
            List<String> actualOutput = groot().getOutput();
            assertEquals(expectedOutput, actualOutput);

            List<String> expectedGitRequests = Lists.newArrayList(
                    "--git-dir omniutils_dir/.git checkout -b sample_branch",
                    "--git-dir omniloan_dir/.git checkout -b sample_branch"
            );
            List<String> actualGitRequests = gitStub().readRequestsFromLog();
            assertEquals(expectedGitRequests, actualGitRequests);

            List<GrootLogEntry> expectedGrootLogs = Lists.newArrayList(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Создание ветки [sample_branch]. Не удалось создать ветку. Причина ошибки - [some unexpected error new line]. Команды - [--git-dir omniutils_dir/.git checkout -b sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Создание ветки [sample_branch]. Ветка успешно создана. Команды - [--git-dir omniloan_dir/.git checkout -b sample_branch]")
            );
            List<GrootLogEntry> actualGrootLogs = groot().readLogs();
            assertLogEntriesEqual(expectedGrootLogs, actualGrootLogs);
        }
    }
}
