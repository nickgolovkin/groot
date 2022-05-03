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
            gitStub().add("--git-dir (.+) branch (.+)", "", 0)
                    .add("--git-dir (.+) checkout (.+)", "Switched to branch '$2'", 0)
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
                    "--git-dir omniutils_dir/.git branch sample_branch",
                    "--git-dir omniutils_dir/.git checkout sample_branch",
                    "--git-dir omniloan_dir/.git branch sample_branch",
                    "--git-dir omniloan_dir/.git checkout sample_branch"
            );
            List<String> actualGitRequests = gitStub().readRequestsFromLog();
            assertEquals(expectedGitRequests, actualGitRequests);

            List<GrootLogEntry> expectedGrootLogs = Lists.newArrayList(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Создание ветки [sample_branch]. Ветка успешно создана. Команды - [--git-dir omniutils_dir/.git branch sample_branch;--git-dir omniutils_dir/.git checkout sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Создание ветки [sample_branch]. Ветка успешно создана. Команды - [--git-dir omniloan_dir/.git branch sample_branch;--git-dir omniloan_dir/.git checkout sample_branch]")
            );
            List<GrootLogEntry> actualGrootLogs = groot().readLogs();
            assertLogEntriesEqual(expectedGrootLogs, actualGrootLogs);
        }

        @Test
        public void branch_already_exists_in_one_project() {
            gitStub().add("--git-dir omniutils_dir/\\.git branch (.+)", "fatal: A branch named '$1' already exists.", 1)
                    .add("--git-dir omniutils_dir/\\.git checkout (.+)", "Switched to branch '$1'", 0)
                    .add("--git-dir omniloan_dir/\\.git branch (.+)", "", 0)
                    .add("--git-dir omniloan_dir/\\.git checkout (.+)", "Switched to branch '$1'", 0)
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
                    "--git-dir omniutils_dir/.git branch sample_branch",
                    "--git-dir omniutils_dir/.git checkout sample_branch",
                    "--git-dir omniloan_dir/.git branch sample_branch",
                    "--git-dir omniloan_dir/.git checkout sample_branch"
            );
            List<String> actualGitRequests = gitStub().readRequestsFromLog();
            assertEquals(expectedGitRequests, actualGitRequests);

            List<GrootLogEntry> expectedGrootLogs = Lists.newArrayList(
                    new GrootLogEntry(LogLevel.WARN, "[omniutils] Создание ветки [sample_branch]. Ветка уже существует. Команды - [--git-dir omniutils_dir/.git branch sample_branch;--git-dir omniutils_dir/.git checkout sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Создание ветки [sample_branch]. Ветка успешно создана. Команды - [--git-dir omniloan_dir/.git branch sample_branch;--git-dir omniloan_dir/.git checkout sample_branch]")
            );
            List<GrootLogEntry> actualGrootLogs = groot().readLogs();
            assertLogEntriesEqual(expectedGrootLogs, actualGrootLogs);
        }

        @Test
        public void cannot_create_branch_in_one_project() {
            gitStub().add("--git-dir omniutils_dir/\\.git branch (.+)", "some unexpected error\nnew line", 2)
                    .add("--git-dir omniloan_dir/\\.git branch (.+)", "", 0)
                    .add("--git-dir omniloan_dir/\\.git checkout (.+)", "Switched to a new branch '$1'", 0)
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
                    "--git-dir omniutils_dir/.git branch sample_branch",
                    "--git-dir omniloan_dir/.git branch sample_branch",
                    "--git-dir omniloan_dir/.git checkout sample_branch"
            );
            List<String> actualGitRequests = gitStub().readRequestsFromLog();
            assertEquals(expectedGitRequests, actualGitRequests);

            List<GrootLogEntry> expectedGrootLogs = Lists.newArrayList(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Создание ветки [sample_branch]. Не удалось создать ветку. Причина ошибки - [some unexpected error new line]. Команды - [--git-dir omniutils_dir/.git branch sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Создание ветки [sample_branch]. Ветка успешно создана. Команды - [--git-dir omniloan_dir/.git branch sample_branch;--git-dir omniloan_dir/.git checkout sample_branch]")
            );
            List<GrootLogEntry> actualGrootLogs = groot().readLogs();
            assertLogEntriesEqual(expectedGrootLogs, actualGrootLogs);
        }
    }

    @DisplayName("delete branch")
    @Nested
    public class DeleteBranch {
        @Test
        public void successfully_deleted() {
            gitStub().add("--git-dir (.+) branch -D (.+)", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("delete branch sample_branch");

            List<String> expectedOutput = Lists.newArrayList(
                    "Удаляю ветку [sample_branch]",
                    "[omniutils] Ветка [sample_branch] успешно удалена",
                    "[omniloan] Ветка [sample_branch] успешно удалена",
                    "Удаление ветки [sample_branch] завершено"
            );
            List<String> actualOutput = groot().getOutput();
            assertEquals(expectedOutput, actualOutput);

            List<String> expectedGitRequests = Lists.newArrayList(
                    "--git-dir omniutils_dir/.git branch -D sample_branch",
                    "--git-dir omniloan_dir/.git branch -D sample_branch"
            );
            List<String> actualGitRequests = gitStub().readRequestsFromLog();
            assertEquals(expectedGitRequests, actualGitRequests);

            List<GrootLogEntry> expectedGrootLogs = Lists.newArrayList(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Удаление ветки [sample_branch]. Ветка успешно удалена. Команды - [--git-dir omniutils_dir/.git branch -D sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Удаление ветки [sample_branch]. Ветка успешно удалена. Команды - [--git-dir omniloan_dir/.git branch -D sample_branch]")
            );
            List<GrootLogEntry> actualGrootLogs = groot().readLogs();
            assertLogEntriesEqual(expectedGrootLogs, actualGrootLogs);
        }

        @Test
        public void branch_does_not_exist_in_one_project() {
            gitStub().add("--git-dir omniutils_dir/.git branch -D (.+)", "error: branch '$1' not found.", 1)
                    .add("--git-dir omniloan_dir/.git branch -D (.+)", "Deleted branch $1 (was cc12db8).", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("delete branch sample_branch");

            List<String> expectedOutput = Lists.newArrayList(
                    "Удаляю ветку [sample_branch]",
                    "[omniutils] Ветка [sample_branch] не существует",
                    "[omniloan] Ветка [sample_branch] успешно удалена",
                    "Удаление ветки [sample_branch] завершено"
            );
            List<String> actualOutput = groot().getOutput();
            assertEquals(expectedOutput, actualOutput);

            List<String> expectedGitRequests = Lists.newArrayList(
                    "--git-dir omniutils_dir/.git branch -D sample_branch",
                    "--git-dir omniloan_dir/.git branch -D sample_branch"
            );
            List<String> actualGitRequests = gitStub().readRequestsFromLog();
            assertEquals(expectedGitRequests, actualGitRequests);

            List<GrootLogEntry> expectedGrootLogs = Lists.newArrayList(
                    new GrootLogEntry(LogLevel.WARN, "[omniutils] Удаление ветки [sample_branch]. Ветка не существует. Команды - [--git-dir omniutils_dir/.git branch -D sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Удаление ветки [sample_branch]. Ветка успешно удалена. Команды - [--git-dir omniloan_dir/.git branch -D sample_branch]")
            );
            List<GrootLogEntry> actualGrootLogs = groot().readLogs();
            assertLogEntriesEqual(expectedGrootLogs, actualGrootLogs);
        }

        @Test
        public void cannot_delete_branch_from_one_project() {
            gitStub().add("--git-dir omniutils_dir/.git branch -D (.+)", "error: Cannot delete branch '$1'\nchecked out at '/home/nikita/Documents/git_test'", 1)
                    .add("--git-dir omniloan_dir/.git branch -D (.+)", "Deleted branch $1 (was cc12db8).", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("delete branch sample_branch");

            List<String> expectedOutput = Lists.newArrayList(
                    "Удаляю ветку [sample_branch]",
                    "[omniutils] Не удалось удалить ветку [sample_branch]",
                    "[omniloan] Ветка [sample_branch] успешно удалена",
                    "Удаление ветки [sample_branch] завершено"
            );
            List<String> actualOutput = groot().getOutput();
            assertEquals(expectedOutput, actualOutput);

            List<String> expectedGitRequests = Lists.newArrayList(
                    "--git-dir omniutils_dir/.git branch -D sample_branch",
                    "--git-dir omniloan_dir/.git branch -D sample_branch"
            );
            List<String> actualGitRequests = gitStub().readRequestsFromLog();
            assertEquals(expectedGitRequests, actualGitRequests);

            List<GrootLogEntry> expectedGrootLogs = Lists.newArrayList(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Удаление ветки [sample_branch]. Не удалось удалить ветку. Причина ошибки - [error: Cannot delete branch 'sample_branch' checked out at '/home/nikita/Documents/git_test']. Команды - [--git-dir omniutils_dir/.git branch -D sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Удаление ветки [sample_branch]. Ветка успешно удалена. Команды - [--git-dir omniloan_dir/.git branch -D sample_branch]")
            );
            List<GrootLogEntry> actualGrootLogs = groot().readLogs();
            assertLogEntriesEqual(expectedGrootLogs, actualGrootLogs);
        }
    }

    @DisplayName("rename branch")
    @Nested
    public class RenameBranch {
        @Test
        public void successfully_renamed() {
            /**
             * TODO: Немного разошлось то, что git status не показывается в списке выполненных команд при логировании
             */
            gitStub().add("--git-dir (.+) branch -M (.+)", "", 0)
                    .add("--git-dir omniutils_dir/.git status", "On branch branch_0\nnothing to commit, working tree clean", 0)
                    .add("--git-dir omniloan_dir/.git status", "On branch branch_1\nnothing to commit, working tree clean", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("rename branch new_sample_branch");

            List<String> expectedOutput = Lists.newArrayList(
                    "Переименовываю ветку в [new_sample_branch]",
                    "[omniutils] Ветка [branch_0] успешно переименована в [new_sample_branch]",
                    "[omniloan] Ветка [branch_1] успешно переименована в [new_sample_branch]",
                    "Переименовывание ветки в [new_sample_branch] завершено"
            );
            List<String> actualOutput = groot().getOutput();
            assertEquals(expectedOutput, actualOutput);

            List<String> expectedGitRequests = Lists.newArrayList(
                    "--git-dir omniutils_dir/.git status",
                    "--git-dir omniutils_dir/.git branch -M new_sample_branch",
                    "--git-dir omniloan_dir/.git status",
                    "--git-dir omniloan_dir/.git branch -M new_sample_branch"
            );
            List<String> actualGitRequests = gitStub().readRequestsFromLog();
            assertEquals(expectedGitRequests, actualGitRequests);

            List<GrootLogEntry> expectedGrootLogs = Lists.newArrayList(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Переименование ветки [branch_0] в [new_sample_branch]. Ветка успешно переименована. Команды - [--git-dir omniutils_dir/.git branch -M new_sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Переименование ветки [branch_1] в [new_sample_branch]. Ветка успешно переименована. Команды - [--git-dir omniloan_dir/.git branch -M new_sample_branch]")
            );
            List<GrootLogEntry> actualGrootLogs = groot().readLogs();
            assertLogEntriesEqual(expectedGrootLogs, actualGrootLogs);
        }

        @Test
        public void cannot_rename_branch_in_one_project() {
            gitStub().add("--git-dir omniutils_dir/.git branch -M (.+)", "some unexpected\nerror", 1)
                    .add("--git-dir omniloan_dir/.git branch -M (.+)", "", 0)
                    .add("--git-dir omniutils_dir/.git status", "On branch branch_0\nnothing to commit, working tree clean", 0)
                    .add("--git-dir omniloan_dir/.git status", "On branch branch_1\nnothing to commit, working tree clean", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("rename branch new_sample_branch");

            List<String> expectedOutput = Lists.newArrayList(
                    "Переименовываю ветку в [new_sample_branch]",
                    "[omniutils] Не удалось переименовать ветку [branch_0] в [new_sample_branch]",
                    "[omniloan] Ветка [branch_1] успешно переименована в [new_sample_branch]",
                    "Переименовывание ветки в [new_sample_branch] завершено"
            );
            List<String> actualOutput = groot().getOutput();
            assertEquals(expectedOutput, actualOutput);

            List<String> expectedGitRequests = Lists.newArrayList(
                    "--git-dir omniutils_dir/.git status",
                    "--git-dir omniutils_dir/.git branch -M new_sample_branch",
                    "--git-dir omniloan_dir/.git status",
                    "--git-dir omniloan_dir/.git branch -M new_sample_branch"
            );
            List<String> actualGitRequests = gitStub().readRequestsFromLog();
            assertEquals(expectedGitRequests, actualGitRequests);

            List<GrootLogEntry> expectedGrootLogs = Lists.newArrayList(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Переименование ветки [branch_0] в [new_sample_branch]. Не удалось переименовать ветку. Причина ошибки - [some unexpected error]. Команды - [--git-dir omniutils_dir/.git branch -M new_sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Переименование ветки [branch_1] в [new_sample_branch]. Ветка успешно переименована. Команды - [--git-dir omniloan_dir/.git branch -M new_sample_branch]")
            );
            List<GrootLogEntry> actualGrootLogs = groot().readLogs();
            assertLogEntriesEqual(expectedGrootLogs, actualGrootLogs);
        }
    }
}
