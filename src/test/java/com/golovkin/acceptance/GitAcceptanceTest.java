package com.golovkin.acceptance;

import com.golovkin.acceptance.utils.app.log.GrootLogEntry;
import com.golovkin.acceptance.utils.common.log.LogLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.golovkin.common.ColorUtils.error;
import static com.golovkin.common.ColorUtils.warn;

// TODO разделить потом на юнит тесты. В приемочных оставить только то, что имеет разницу для пользователя (например, ошибки с пояснениями или обработка исключенительных ситуаций, кроме как логирование ошибки (хотя один кейс с логированием должен быть))
@DisplayName("Git acceptance test")
public class GitAcceptanceTest extends AbstractAcceptanceTest {
    @DisplayName("new branch")
    @Nested
    public class NewBranch {
        @Test
        public void successfully_created() {
            gitStub().add("-C (.+) branch (.+)", "", 0)
                    .add("-C (.+) checkout (.+)", "Switched to branch '$2'", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("new branch sample_branch");

            check().assertOutputEqual(
                    "Создаю ветку [sample_branch]",
                    "[omniutils] Ветка [sample_branch] успешно создана",
                    "[omniloan] Ветка [sample_branch] успешно создана",
                    "Создание ветки [sample_branch] завершено"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir branch sample_branch",
                    "-C omniutils_dir checkout sample_branch",
                    "-C omniloan_dir branch sample_branch",
                    "-C omniloan_dir checkout sample_branch"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Создание ветки [sample_branch]. Ветка успешно создана. Команды - [-C \"omniutils_dir\" branch sample_branch;-C \"omniutils_dir\" checkout sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Создание ветки [sample_branch]. Ветка успешно создана. Команды - [-C \"omniloan_dir\" branch sample_branch;-C \"omniloan_dir\" checkout sample_branch]")
            );
        }

        @Test
        public void branch_already_exists_in_one_project() {
            gitStub().add("-C omniutils_dir branch (.+)", "fatal: A branch named '$1' already exists.", 1)
                    .add("-C omniutils_dir checkout (.+)", "Switched to branch '$1'", 0)
                    .add("-C omniloan_dir branch (.+)", "", 0)
                    .add("-C omniloan_dir checkout (.+)", "Switched to branch '$1'", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("new branch sample_branch");

            check().assertOutputEqual(
                    "Создаю ветку [sample_branch]",
                    warn("[omniutils] Ветка [sample_branch] уже существует"),
                    "[omniloan] Ветка [sample_branch] успешно создана",
                    "Создание ветки [sample_branch] завершено"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir branch sample_branch",
                    "-C omniutils_dir checkout sample_branch",
                    "-C omniloan_dir branch sample_branch",
                    "-C omniloan_dir checkout sample_branch"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.WARN, "[omniutils] Создание ветки [sample_branch]. Ветка уже существует. Команды - [-C \"omniutils_dir\" branch sample_branch;-C \"omniutils_dir\" checkout sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Создание ветки [sample_branch]. Ветка успешно создана. Команды - [-C \"omniloan_dir\" branch sample_branch;-C \"omniloan_dir\" checkout sample_branch]")
            );
        }

        @Test
        public void cannot_create_branch_in_one_project() {
            gitStub().add("-C omniutils_dir branch (.+)", "some unexpected error\nnew line", 2)
                    .add("-C omniloan_dir branch (.+)", "", 0)
                    .add("-C omniloan_dir checkout (.+)", "Switched to a new branch '$1'", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("new branch sample_branch");

            check().assertOutputEqual(
                    "Создаю ветку [sample_branch]",
                    error("[omniutils] Не удалось создать ветку [sample_branch]"),
                    "[omniloan] Ветка [sample_branch] успешно создана",
                    "Создание ветки [sample_branch] завершено"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir branch sample_branch",
                    "-C omniloan_dir branch sample_branch",
                    "-C omniloan_dir checkout sample_branch"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Создание ветки [sample_branch]. Не удалось создать ветку. Причина ошибки - [some unexpected error new line]. Команды - [-C \"omniutils_dir\" branch sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Создание ветки [sample_branch]. Ветка успешно создана. Команды - [-C \"omniloan_dir\" branch sample_branch;-C \"omniloan_dir\" checkout sample_branch]")
            );
        }
    }

    @DisplayName("delete branch")
    @Nested
    public class DeleteBranch {
        @Test
        public void successfully_deleted() {
            gitStub().add("-C (.+) branch -D (.+)", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("delete branch sample_branch");

            check().assertOutputEqual(
                    "Удаляю ветку [sample_branch]",
                    "[omniutils] Ветка [sample_branch] успешно удалена",
                    "[omniloan] Ветка [sample_branch] успешно удалена",
                    "Удаление ветки [sample_branch] завершено"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir branch -D sample_branch",
                    "-C omniloan_dir branch -D sample_branch"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Удаление ветки [sample_branch]. Ветка успешно удалена. Команды - [-C \"omniutils_dir\" branch -D sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Удаление ветки [sample_branch]. Ветка успешно удалена. Команды - [-C \"omniloan_dir\" branch -D sample_branch]")
            );
        }

        @Test
        public void branch_does_not_exist_in_one_project() {
            gitStub().add("-C omniutils_dir branch -D (.+)", "error: branch '$1' not found.", 1)
                    .add("-C omniloan_dir branch -D (.+)", "Deleted branch $1 (was cc12db8).", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("delete branch sample_branch");

            check().assertOutputEqual(
                    "Удаляю ветку [sample_branch]",
                    warn("[omniutils] Ветка [sample_branch] не существует"),
                    "[omniloan] Ветка [sample_branch] успешно удалена",
                    "Удаление ветки [sample_branch] завершено"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir branch -D sample_branch",
                    "-C omniloan_dir branch -D sample_branch"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.WARN, "[omniutils] Удаление ветки [sample_branch]. Ветка не существует. Команды - [-C \"omniutils_dir\" branch -D sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Удаление ветки [sample_branch]. Ветка успешно удалена. Команды - [-C \"omniloan_dir\" branch -D sample_branch]")
            );
        }

        @Test
        public void cannot_delete_branch_from_one_project() {
            gitStub().add("-C omniutils_dir branch -D (.+)", "error: Cannot delete branch '$1'\nchecked out at '/home/nikita/Documents/git_test'", 1)
                    .add("-C omniloan_dir branch -D (.+)", "Deleted branch $1 (was cc12db8).", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("delete branch sample_branch");

            check().assertOutputEqual(
                    "Удаляю ветку [sample_branch]",
                    error("[omniutils] Не удалось удалить ветку [sample_branch]"),
                    "[omniloan] Ветка [sample_branch] успешно удалена",
                    "Удаление ветки [sample_branch] завершено"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir branch -D sample_branch",
                    "-C omniloan_dir branch -D sample_branch"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Удаление ветки [sample_branch]. Не удалось удалить ветку. Причина ошибки - [error: Cannot delete branch 'sample_branch' checked out at '/home/nikita/Documents/git_test']. Команды - [-C \"omniutils_dir\" branch -D sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Удаление ветки [sample_branch]. Ветка успешно удалена. Команды - [-C \"omniloan_dir\" branch -D sample_branch]")
            );
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
            gitStub().add("-C (.+) branch -M (.+)", "", 0)
                    .add("-C omniutils_dir status", "On branch branch_0\nnothing to commit, working tree clean", 0)
                    .add("-C omniloan_dir status", "On branch branch_1\nnothing to commit, working tree clean", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("rename branch new_sample_branch");

            check().assertOutputEqual(
                    "Переименовываю ветку в [new_sample_branch]",
                    "[omniutils] Ветка [branch_0] успешно переименована в [new_sample_branch]",
                    "[omniloan] Ветка [branch_1] успешно переименована в [new_sample_branch]",
                    "Переименовывание ветки в [new_sample_branch] завершено"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir branch -M new_sample_branch",
                    "-C omniloan_dir status",
                    "-C omniloan_dir branch -M new_sample_branch"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Переименование ветки [branch_0] в [new_sample_branch]. Ветка успешно переименована. Команды - [-C \"omniutils_dir\" branch -M new_sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Переименование ветки [branch_1] в [new_sample_branch]. Ветка успешно переименована. Команды - [-C \"omniloan_dir\" branch -M new_sample_branch]")
            );
        }

        @Test
        public void cannot_rename_branch_in_one_project() {
            gitStub().add("-C omniutils_dir branch -M (.+)", "some unexpected\nerror", 1)
                    .add("-C omniloan_dir branch -M (.+)", "", 0)
                    .add("-C omniutils_dir status", "On branch branch_0\nnothing to commit, working tree clean", 0)
                    .add("-C omniloan_dir status", "On branch branch_1\nnothing to commit, working tree clean", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("rename branch new_sample_branch");

            check().assertOutputEqual(
                    "Переименовываю ветку в [new_sample_branch]",
                    error("[omniutils] Не удалось переименовать ветку [branch_0] в [new_sample_branch]"),
                    "[omniloan] Ветка [branch_1] успешно переименована в [new_sample_branch]",
                    "Переименовывание ветки в [new_sample_branch] завершено"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir branch -M new_sample_branch",
                    "-C omniloan_dir status",
                    "-C omniloan_dir branch -M new_sample_branch"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Переименование ветки [branch_0] в [new_sample_branch]. Не удалось переименовать ветку. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" branch -M new_sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Переименование ветки [branch_1] в [new_sample_branch]. Ветка успешно переименована. Команды - [-C \"omniloan_dir\" branch -M new_sample_branch]")
            );
        }
    }

    @DisplayName("abort")
    @Nested
    public class Abort {
        @Test
        public void successfully_aborted_merge_and_cherry_pick() {
            gitStub().add("-C omniutils_dir merge --abort", "", 0)
                    .add("-C omniutils_dir cherry-pick --abort", "error: no cherry-pick or revert in progress\nfatal: cherry-pick failed", 1)
                    .add("-C omniloan_dir merge --abort", "fatal: There is no merge to abort (MERGE_HEAD missing).", 1)
                    .add("-C omniloan_dir cherry-pick --abort", "", 0)
                    .add("-C omniutils_dir status", "On branch sample_branch\nYou have unmerged paths.\n  (fix conflicts and run \"git commit\")\n  (use \"git merge --abort\" to abort the merge)\n\nUnmerged paths:\n  (use \"git add <file>...\" to mark resolution)\n	both modified:   file\n\nno changes added to commit (use \"git add\" and/or \"git commit -a\")", 0)
                    .add("-C omniloan_dir status", "On branch sample_branch\nYou are currently cherry-picking commit c86117f.\n  (fix conflicts and run \"git cherry-pick --continue\")\n  (use \"git cherry-pick --skip\" to skip this patch)\n  (use \"git cherry-pick --abort\" to cancel the cherry-pick operation)\n\nUnmerged paths:\n  (use \"git add <file>...\" to mark resolution)\n	both modified:   file\n\nno changes added to commit (use \"git add\" and/or \"git commit -a\")", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("abort");

            check().assertOutputEqual(
                    "Произвожу отмену мержа/черри-пика",
                    "[omniutils] Мерж/черри-пик успешно отменен в [sample_branch]",
                    "[omniloan] Мерж/черри-пик успешно отменен в [sample_branch]",
                    "Отмена мержа/черри-пика завершена"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir merge --abort",
                    "-C omniloan_dir status",
                    "-C omniloan_dir merge --abort",
                    "-C omniloan_dir cherry-pick --abort"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Отмена мержа/черри-пика в ветке [sample_branch]. Мерж/черри-пик успешно отменен. Команды - [-C \"omniutils_dir\" merge --abort]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Отмена мержа/черри-пика в ветке [sample_branch]. Мерж/черри-пик успешно отменен. Команды - [-C \"omniloan_dir\" merge --abort;-C \"omniloan_dir\" cherry-pick --abort]")
            );
        }

        @Test
        public void no_merge_to_abort_in_one_project() {
            gitStub().add("-C omniutils_dir merge --abort", "fatal: There is no merge to abort (MERGE_HEAD missing).", 1)
                    .add("-C omniutils_dir cherry-pick --abort", "error: no cherry-pick or revert in progress\nfatal: cherry-pick failed", 1)
                    .add("-C omniloan_dir merge --abort", "", 0)
                    .add("-C omniloan_dir cherry-pick --abort", "error: no cherry-pick or revert in progress\nfatal: cherry-pick failed", 1)
                    .add("-C omniutils_dir status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C omniloan_dir status", "On branch sample_branch\nYou have unmerged paths.\n  (fix conflicts and run \"git commit\")\n  (use \"git merge --abort\" to abort the merge)\n\nUnmerged paths:\n  (use \"git add <file>...\" to mark resolution)\n	both modified:   file\n\nno changes added to commit (use \"git add\" and/or \"git commit -a\")", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("abort");

            check().assertOutputEqual(
                    "Произвожу отмену мержа/черри-пика",
                    warn("[omniutils] Нет мержа/черри-пика для отмены в [sample_branch]"),
                    "[omniloan] Мерж/черри-пик успешно отменен в [sample_branch]",
                    "Отмена мержа/черри-пика завершена"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir merge --abort",
                    "-C omniutils_dir cherry-pick --abort",
                    "-C omniloan_dir status",
                    "-C omniloan_dir merge --abort"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.WARN, "[omniutils] Отмена мержа/черри-пика в ветке [sample_branch]. Нет мержа/черри-пика для отмены. Команды - [-C \"omniutils_dir\" merge --abort;-C \"omniutils_dir\" cherry-pick --abort]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Отмена мержа/черри-пика в ветке [sample_branch]. Мерж/черри-пик успешно отменен. Команды - [-C \"omniloan_dir\" merge --abort]")
            );
        }

        @Test
        public void no_cherry_pick_to_abort_in_one_project() {
            gitStub().add("-C omniutils_dir merge --abort", "fatal: There is no merge to abort (MERGE_HEAD missing).", 1)
                    .add("-C omniutils_dir cherry-pick --abort", "error: no cherry-pick or revert in progress\nfatal: cherry-pick failed", 1)
                    .add("-C omniloan_dir merge --abort", "", 0)
                    .add("-C omniloan_dir cherry-pick --abort", "error: no cherry-pick or revert in progress\nfatal: cherry-pick failed", 1)
                    .add("-C omniutils_dir status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C omniloan_dir status", "On branch sample_branch\nYou have unmerged paths.\n  (fix conflicts and run \"git commit\")\n  (use \"git merge --abort\" to abort the merge)\n\nUnmerged paths:\n  (use \"git add <file>...\" to mark resolution)\n	both modified:   file\n\nno changes added to commit (use \"git add\" and/or \"git commit -a\")", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("abort");

            check().assertOutputEqual(
                    "Произвожу отмену мержа/черри-пика",
                    warn("[omniutils] Нет мержа/черри-пика для отмены в [sample_branch]"),
                    "[omniloan] Мерж/черри-пик успешно отменен в [sample_branch]",
                    "Отмена мержа/черри-пика завершена"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir merge --abort",
                    "-C omniutils_dir cherry-pick --abort",
                    "-C omniloan_dir status",
                    "-C omniloan_dir merge --abort"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.WARN, "[omniutils] Отмена мержа/черри-пика в ветке [sample_branch]. Нет мержа/черри-пика для отмены. Команды - [-C \"omniutils_dir\" merge --abort;-C \"omniutils_dir\" cherry-pick --abort]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Отмена мержа/черри-пика в ветке [sample_branch]. Мерж/черри-пик успешно отменен. Команды - [-C \"omniloan_dir\" merge --abort]")
            );
        }

        @Test
        public void cannot_abort_merge_in_one_project() {
            gitStub().add("-C omniutils_dir merge --abort", "some unexpected\nerror", 1)
                    .add("-C omniutils_dir cherry-pick --abort", "error: no cherry-pick or revert in progress\nfatal: cherry-pick failed", 1)
                    .add("-C omniloan_dir merge --abort", "", 0)
                    .add("-C omniloan_dir cherry-pick --abort", "error: no cherry-pick or revert in progress\nfatal: cherry-pick failed", 1)
                    .add("-C omniutils_dir status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C omniloan_dir status", "On branch sample_branch\nYou have unmerged paths.\n  (fix conflicts and run \"git commit\")\n  (use \"git merge --abort\" to abort the merge)\n\nUnmerged paths:\n  (use \"git add <file>...\" to mark resolution)\n	both modified:   file\n\nno changes added to commit (use \"git add\" and/or \"git commit -a\")", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("abort");

            check().assertOutputEqual(
                    "Произвожу отмену мержа/черри-пика",
                    error("[omniutils] Не удалось отменить мерж/черри-пик в [sample_branch]"),
                    "[omniloan] Мерж/черри-пик успешно отменен в [sample_branch]",
                    "Отмена мержа/черри-пика завершена"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir merge --abort",
                    "-C omniloan_dir status",
                    "-C omniloan_dir merge --abort"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Отмена мержа/черри-пика в ветке [sample_branch]. Не удалось отменить мерж/черри-пик. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" merge --abort]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Отмена мержа/черри-пика в ветке [sample_branch]. Мерж/черри-пик успешно отменен. Команды - [-C \"omniloan_dir\" merge --abort]")
            );
        }

        @Test
        public void cannot_abort_cherry_pick_in_one_project() {
            gitStub().add("-C omniutils_dir merge --abort", "fatal: There is no merge to abort (MERGE_HEAD missing).", 1)
                    .add("-C omniutils_dir cherry-pick --abort", "some unexpected\nerror", 1)
                    .add("-C omniloan_dir merge --abort", "", 0)
                    .add("-C omniloan_dir cherry-pick --abort", "error: no cherry-pick or revert in progress\nfatal: cherry-pick failed", 1)
                    .add("-C omniutils_dir status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C omniloan_dir status", "On branch sample_branch\nYou have unmerged paths.\n  (fix conflicts and run \"git commit\")\n  (use \"git merge --abort\" to abort the merge)\n\nUnmerged paths:\n  (use \"git add <file>...\" to mark resolution)\n	both modified:   file\n\nno changes added to commit (use \"git add\" and/or \"git commit -a\")", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("abort");

            check().assertOutputEqual(
                    "Произвожу отмену мержа/черри-пика",
                    error("[omniutils] Не удалось отменить мерж/черри-пик в [sample_branch]"),
                    "[omniloan] Мерж/черри-пик успешно отменен в [sample_branch]",
                    "Отмена мержа/черри-пика завершена"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir merge --abort",
                    "-C omniutils_dir cherry-pick --abort",
                    "-C omniloan_dir status",
                    "-C omniloan_dir merge --abort"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Отмена мержа/черри-пика в ветке [sample_branch]. Не удалось отменить мерж/черри-пик. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" merge --abort;-C \"omniutils_dir\" cherry-pick --abort]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Отмена мержа/черри-пика в ветке [sample_branch]. Мерж/черри-пик успешно отменен. Команды - [-C \"omniloan_dir\" merge --abort]")
            );
        }
    }

    @DisplayName("checkout")
    @Nested
    public class Checkout {
        @Test
        public void successful_checkout_first_project_has_checkpoint_second_does_not_have_checkpoint() {
            gitStub().add("-C (.+) status", "On branch current_branch\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) rev-parse --verify sample_branch", "e65f46ad23748e015780f395683ca2d9551ab221", 0)
                    .add("-C (.+) commit -a -m \\[GROOT\\] ~Checkpoint~", "[current_branch bac20dd] Hello\n 1 file changed, 1 insertion(+)  create mode 100644 ok", 0)
                    .add("-C (.+) checkout sample_branch", "", 0)
                    .add("-C omniutils_dir log -1 --pretty=%H %B", "e65f46ad23748e015780f395683ca2d9551ab221 [GROOT] ~Checkpoint~", 0)
                    .add("-C omniloan_dir log -1 --pretty=%H %B", "e65f46ad23748e015780f395683ca2d9551ab221 Hello", 0)
                    .add("-C (.+) reset --soft HEAD~1", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("checkout sample_branch");

            check().assertOutputEqual(
                    "Перехожу в ветку [sample_branch]",
                    "[omniutils] Переход в ветку [sample_branch] успешно завершен",
                    "[omniloan] Переход в ветку [sample_branch] успешно завершен",
                    "Переход в ветку [sample_branch] завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir rev-parse --verify sample_branch",
                    "-C omniutils_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniutils_dir checkout sample_branch",
                    "-C omniutils_dir log -1 --pretty=%H %B",
                    "-C omniutils_dir reset --soft HEAD~1",
                    "-C omniloan_dir status",
                    "-C omniloan_dir rev-parse --verify sample_branch",
                    "-C omniloan_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniloan_dir checkout sample_branch",
                    "-C omniloan_dir log -1 --pretty=%H %B"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Переход из ветки [current_branch] в ветку [sample_branch]. Переход в ветку [sample_branch] успешно завершен. Команды - [-C \"omniutils_dir\" rev-parse --verify sample_branch;-C \"omniutils_dir\" commit -a -m \"[GROOT] ~Checkpoint~\";-C \"omniutils_dir\" checkout sample_branch;-C \"omniutils_dir\" log -1 \"--pretty=%H %B\";-C \"omniutils_dir\" reset --soft HEAD~1]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Переход из ветки [current_branch] в ветку [sample_branch]. Переход в ветку [sample_branch] успешно завершен. Команды - [-C \"omniloan_dir\" rev-parse --verify sample_branch;-C \"omniloan_dir\" commit -a -m \"[GROOT] ~Checkpoint~\";-C \"omniloan_dir\" checkout sample_branch;-C \"omniloan_dir\" log -1 \"--pretty=%H %B\"]")
            );
        }

        @Test
        public void one_project_does_not_have_needed_branch() {
            gitStub().add("-C (.+) status", "On branch current_branch\nnothing to commit, working tree clean", 0)
                    .add("-C omniutils_dir rev-parse --verify sample_branch", "fatal: Needed a single revision", 1)
                    .add("-C omniloan_dir rev-parse --verify sample_branch", "e65f46ad23748e015780f395683ca2d9551ab221", 0)
                    .add("-C (.+) commit -a -m \\[GROOT\\] ~Checkpoint~", "[current_branch bac20dd] Hello  1 file changed, 1 insertion(+)  create mode 100644 ok", 0)
                    .add("-C (.+) checkout sample_branch", "", 0)
                    .add("-C omniloan_dir log -1 --pretty=%H %B", "e65f46ad23748e015780f395683ca2d9551ab221 Hello", 0)
                    .add("-C (.+) reset --soft HEAD~1", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("checkout sample_branch");

            check().assertOutputEqual(
                    "Перехожу в ветку [sample_branch]",
                    error("[omniutils] Ветка [sample_branch] не найдена"),
                    "[omniloan] Переход в ветку [sample_branch] успешно завершен",
                    "Переход в ветку [sample_branch] завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir rev-parse --verify sample_branch",
                    "-C omniloan_dir status",
                    "-C omniloan_dir rev-parse --verify sample_branch",
                    "-C omniloan_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniloan_dir checkout sample_branch",
                    "-C omniloan_dir log -1 --pretty=%H %B"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Переход из ветки [current_branch] в ветку [sample_branch]. Ветка [sample_branch] не найдена. Команды - [-C \"omniutils_dir\" rev-parse --verify sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Переход из ветки [current_branch] в ветку [sample_branch]. Переход в ветку [sample_branch] успешно завершен. Команды - [-C \"omniloan_dir\" rev-parse --verify sample_branch;-C \"omniloan_dir\" commit -a -m \"[GROOT] ~Checkpoint~\";-C \"omniloan_dir\" checkout sample_branch;-C \"omniloan_dir\" log -1 \"--pretty=%H %B\"]")
            );
        }

        @Test
        public void cannot_check_branch_existence_in_one_project() {
            gitStub().add("-C (.+) status", "On branch current_branch\nnothing to commit, working tree clean", 0)
                    .add("-C omniutils_dir rev-parse --verify sample_branch", "some unexpected\nerror", 1)
                    .add("-C omniloan_dir rev-parse --verify sample_branch", "e65f46ad23748e015780f395683ca2d9551ab221", 0)
                    .add("-C (.+) commit -a -m \\[GROOT\\] ~Checkpoint~", "[current_branch bac20dd] Hello  1 file changed, 1 insertion(+)  create mode 100644 ok", 0)
                    .add("-C (.+) checkout sample_branch", "", 0)
                    .add("-C omniloan_dir log -1 --pretty=%H %B", "e65f46ad23748e015780f395683ca2d9551ab221 Hello", 0)
                    .add("-C (.+) reset --soft HEAD~1", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("checkout sample_branch");

            check().assertOutputEqual(
                    "Перехожу в ветку [sample_branch]",
                    error("[omniutils] Не удалось перейти в ветку [sample_branch]"),
                    "[omniloan] Переход в ветку [sample_branch] успешно завершен",
                    "Переход в ветку [sample_branch] завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir rev-parse --verify sample_branch",
                    "-C omniloan_dir status",
                    "-C omniloan_dir rev-parse --verify sample_branch",
                    "-C omniloan_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniloan_dir checkout sample_branch",
                    "-C omniloan_dir log -1 --pretty=%H %B"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Переход из ветки [current_branch] в ветку [sample_branch]. Не удалось перейти в ветку [sample_branch]. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" rev-parse --verify sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Переход из ветки [current_branch] в ветку [sample_branch]. Переход в ветку [sample_branch] успешно завершен. Команды - [-C \"omniloan_dir\" rev-parse --verify sample_branch;-C \"omniloan_dir\" commit -a -m \"[GROOT] ~Checkpoint~\";-C \"omniloan_dir\" checkout sample_branch;-C \"omniloan_dir\" log -1 \"--pretty=%H %B\"]")
            );
        }

        @Test
        public void one_project_does_not_have_work_to_save() {
            gitStub().add("-C (.+) status", "On branch current_branch\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) rev-parse --verify sample_branch", "e65f46ad23748e015780f395683ca2d9551ab221", 0)
                    .add("-C omniutils_dir commit -a -m \\[GROOT\\] ~Checkpoint~", "On branch current_branch\nnothing to commit, working tree clean", 1)
                    .add("-C omniloan_dir commit -a -m \\[GROOT\\] ~Checkpoint~", "[current_branch bac20dd] Hello  1 file changed, 1 insertion(+)  create mode 100644 ok", 0)
                    .add("-C (.+) checkout sample_branch", "", 0)
                    .add("-C omniutils_dir log -1 --pretty=%H %B", "e65f46ad23748e015780f395683ca2d9551ab221 [GROOT] ~Checkpoint~", 0)
                    .add("-C omniloan_dir log -1 --pretty=%H %B", "e65f46ad23748e015780f395683ca2d9551ab221 Hello", 0)
                    .add("-C (.+) reset --soft HEAD~1", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("checkout sample_branch");

            check().assertOutputEqual(
                    "Перехожу в ветку [sample_branch]",
                    "[omniutils] Переход в ветку [sample_branch] успешно завершен",
                    "[omniloan] Переход в ветку [sample_branch] успешно завершен",
                    "Переход в ветку [sample_branch] завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir rev-parse --verify sample_branch",
                    "-C omniutils_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniutils_dir checkout sample_branch",
                    "-C omniutils_dir log -1 --pretty=%H %B",
                    "-C omniutils_dir reset --soft HEAD~1",
                    "-C omniloan_dir status",
                    "-C omniloan_dir rev-parse --verify sample_branch",
                    "-C omniloan_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniloan_dir checkout sample_branch",
                    "-C omniloan_dir log -1 --pretty=%H %B"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Переход из ветки [current_branch] в ветку [sample_branch]. Переход в ветку [sample_branch] успешно завершен (несохраненных изменений не было). Команды - [-C \"omniutils_dir\" rev-parse --verify sample_branch;-C \"omniutils_dir\" commit -a -m \"[GROOT] ~Checkpoint~\";-C \"omniutils_dir\" checkout sample_branch;-C \"omniutils_dir\" log -1 \"--pretty=%H %B\";-C \"omniutils_dir\" reset --soft HEAD~1]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Переход из ветки [current_branch] в ветку [sample_branch]. Переход в ветку [sample_branch] успешно завершен. Команды - [-C \"omniloan_dir\" rev-parse --verify sample_branch;-C \"omniloan_dir\" commit -a -m \"[GROOT] ~Checkpoint~\";-C \"omniloan_dir\" checkout sample_branch;-C \"omniloan_dir\" log -1 \"--pretty=%H %B\"]")
            );
        }

        @Test
        public void cannot_save_work_in_one_project() {
            gitStub().add("-C (.+) status", "On branch current_branch\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) rev-parse --verify sample_branch", "e65f46ad23748e015780f395683ca2d9551ab221", 0)
                    .add("-C omniutils_dir commit -a -m \\[GROOT\\] ~Checkpoint~", "some unexpected\nerror", 1)
                    .add("-C omniloan_dir commit -a -m \\[GROOT\\] ~Checkpoint~", "[current_branch bac20dd] Hello  1 file changed, 1 insertion(+)  create mode 100644 ok", 0)
                    .add("-C (.+) checkout sample_branch", "", 0)
                    .add("-C omniutils_dir log -1 --pretty=%H %B", "e65f46ad23748e015780f395683ca2d9551ab221 [GROOT] ~Checkpoint~", 0)
                    .add("-C omniloan_dir log -1 --pretty=%H %B", "e65f46ad23748e015780f395683ca2d9551ab221 Hello", 0)
                    .add("-C (.+) reset --soft HEAD~1", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("checkout sample_branch");

            check().assertOutputEqual(
                    "Перехожу в ветку [sample_branch]",
                    error("[omniutils] Не удалось перейти в ветку [sample_branch]"),
                    "[omniloan] Переход в ветку [sample_branch] успешно завершен",
                    "Переход в ветку [sample_branch] завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir rev-parse --verify sample_branch",
                    "-C omniutils_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniloan_dir status",
                    "-C omniloan_dir rev-parse --verify sample_branch",
                    "-C omniloan_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniloan_dir checkout sample_branch",
                    "-C omniloan_dir log -1 --pretty=%H %B"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Переход из ветки [current_branch] в ветку [sample_branch]. Не удалось перейти в ветку [sample_branch]. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" rev-parse --verify sample_branch;-C \"omniutils_dir\" commit -a -m \"[GROOT] ~Checkpoint~\"]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Переход из ветки [current_branch] в ветку [sample_branch]. Переход в ветку [sample_branch] успешно завершен. Команды - [-C \"omniloan_dir\" rev-parse --verify sample_branch;-C \"omniloan_dir\" commit -a -m \"[GROOT] ~Checkpoint~\";-C \"omniloan_dir\" checkout sample_branch;-C \"omniloan_dir\" log -1 \"--pretty=%H %B\"]")
            );
        }

        @Test
        public void cannot_checkout_in_one_project() {
            gitStub().add("-C (.+) status", "On branch current_branch\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) rev-parse --verify sample_branch", "e65f46ad23748e015780f395683ca2d9551ab221", 0)
                    .add("-C (.+) commit -a -m \\[GROOT\\] ~Checkpoint~", "[current_branch bac20dd] Hello  1 file changed, 1 insertion(+)  create mode 100644 ok", 0)
                    .add("-C omniutils_dir checkout sample_branch", "some unexpected\nerror", 1)
                    .add("-C omniloan_dir checkout sample_branch", "", 0)
                    .add("-C omniutils_dir log -1 --pretty=%H %B", "e65f46ad23748e015780f395683ca2d9551ab221 [GROOT] ~Checkpoint~", 0)
                    .add("-C omniloan_dir log -1 --pretty=%H %B", "e65f46ad23748e015780f395683ca2d9551ab221 Hello", 0)
                    .add("-C (.+) reset --soft HEAD~1", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("checkout sample_branch");

            check().assertOutputEqual(
                    "Перехожу в ветку [sample_branch]",
                    error("[omniutils] Не удалось перейти в ветку [sample_branch]"),
                    "[omniloan] Переход в ветку [sample_branch] успешно завершен",
                    "Переход в ветку [sample_branch] завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir rev-parse --verify sample_branch",
                    "-C omniutils_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniutils_dir checkout sample_branch",
                    "-C omniloan_dir status",
                    "-C omniloan_dir rev-parse --verify sample_branch",
                    "-C omniloan_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniloan_dir checkout sample_branch",
                    "-C omniloan_dir log -1 --pretty=%H %B"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Переход из ветки [current_branch] в ветку [sample_branch]. Не удалось перейти в ветку [sample_branch]. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" rev-parse --verify sample_branch;-C \"omniutils_dir\" commit -a -m \"[GROOT] ~Checkpoint~\";-C \"omniutils_dir\" checkout sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Переход из ветки [current_branch] в ветку [sample_branch]. Переход в ветку [sample_branch] успешно завершен. Команды - [-C \"omniloan_dir\" rev-parse --verify sample_branch;-C \"omniloan_dir\" commit -a -m \"[GROOT] ~Checkpoint~\";-C \"omniloan_dir\" checkout sample_branch;-C \"omniloan_dir\" log -1 \"--pretty=%H %B\"]")
            );
        }

        @Test
        public void cannot_check_last_commit_in_one_project() {
            gitStub().add("-C (.+) status", "On branch current_branch\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) rev-parse --verify sample_branch", "e65f46ad23748e015780f395683ca2d9551ab221", 0)
                    .add("-C (.+) commit -a -m \\[GROOT\\] ~Checkpoint~", "[current_branch bac20dd] Hello  1 file changed, 1 insertion(+)  create mode 100644 ok", 0)
                    .add("-C (.+) checkout sample_branch", "", 0)
                    .add("-C omniutils_dir log -1 --pretty=%H %B", "some unexpected\nerror", 1)
                    .add("-C omniloan_dir log -1 --pretty=%H %B", "e65f46ad23748e015780f395683ca2d9551ab221 Hello", 0)
                    .add("-C (.+) reset --soft HEAD~1", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("checkout sample_branch");

            check().assertOutputEqual(
                    "Перехожу в ветку [sample_branch]",
                    error("[omniutils] Не удалось перейти в ветку [sample_branch]"),
                    "[omniloan] Переход в ветку [sample_branch] успешно завершен",
                    "Переход в ветку [sample_branch] завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir rev-parse --verify sample_branch",
                    "-C omniutils_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniutils_dir checkout sample_branch",
                    "-C omniutils_dir log -1 --pretty=%H %B",
                    "-C omniloan_dir status",
                    "-C omniloan_dir rev-parse --verify sample_branch",
                    "-C omniloan_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniloan_dir checkout sample_branch",
                    "-C omniloan_dir log -1 --pretty=%H %B"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Переход из ветки [current_branch] в ветку [sample_branch]. Не удалось перейти в ветку [sample_branch]. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" rev-parse --verify sample_branch;-C \"omniutils_dir\" commit -a -m \"[GROOT] ~Checkpoint~\";-C \"omniutils_dir\" checkout sample_branch;-C \"omniutils_dir\" log -1 \"--pretty=%H %B\"]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Переход из ветки [current_branch] в ветку [sample_branch]. Переход в ветку [sample_branch] успешно завершен. Команды - [-C \"omniloan_dir\" rev-parse --verify sample_branch;-C \"omniloan_dir\" commit -a -m \"[GROOT] ~Checkpoint~\";-C \"omniloan_dir\" checkout sample_branch;-C \"omniloan_dir\" log -1 \"--pretty=%H %B\"]")
            );
        }

        @Test
        public void cannot_rollback_saved_work_in_one_project() {
            gitStub().add("-C (.+) status", "On branch current_branch\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) rev-parse --verify sample_branch", "e65f46ad23748e015780f395683ca2d9551ab221", 0)
                    .add("-C (.+) commit -a -m \\[GROOT\\] ~Checkpoint~", "[current_branch bac20dd] Hello  1 file changed, 1 insertion(+)  create mode 100644 ok", 0)
                    .add("-C (.+) checkout sample_branch", "", 0)
                    .add("-C omniutils_dir log -1 --pretty=%H %B", "e65f46ad23748e015780f395683ca2d9551ab221 [GROOT] ~Checkpoint~", 0)
                    .add("-C omniloan_dir log -1 --pretty=%H %B", "e65f46ad23748e015780f395683ca2d9551ab221 Hello", 0)
                    .add("-C (.+) reset --soft HEAD~1", "some unexpected\nerror", 1)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("checkout sample_branch");

            check().assertOutputEqual(
                    "Перехожу в ветку [sample_branch]",
                    error("[omniutils] Не удалось перейти в ветку [sample_branch]"),
                    "[omniloan] Переход в ветку [sample_branch] успешно завершен",
                    "Переход в ветку [sample_branch] завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir rev-parse --verify sample_branch",
                    "-C omniutils_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniutils_dir checkout sample_branch",
                    "-C omniutils_dir log -1 --pretty=%H %B",
                    "-C omniutils_dir reset --soft HEAD~1",
                    "-C omniloan_dir status",
                    "-C omniloan_dir rev-parse --verify sample_branch",
                    "-C omniloan_dir commit -a -m [GROOT] ~Checkpoint~",
                    "-C omniloan_dir checkout sample_branch",
                    "-C omniloan_dir log -1 --pretty=%H %B"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Переход из ветки [current_branch] в ветку [sample_branch]. Не удалось перейти в ветку [sample_branch]. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" rev-parse --verify sample_branch;-C \"omniutils_dir\" commit -a -m \"[GROOT] ~Checkpoint~\";-C \"omniutils_dir\" checkout sample_branch;-C \"omniutils_dir\" log -1 \"--pretty=%H %B\";-C \"omniutils_dir\" reset --soft HEAD~1]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Переход из ветки [current_branch] в ветку [sample_branch]. Переход в ветку [sample_branch] успешно завершен. Команды - [-C \"omniloan_dir\" rev-parse --verify sample_branch;-C \"omniloan_dir\" commit -a -m \"[GROOT] ~Checkpoint~\";-C \"omniloan_dir\" checkout sample_branch;-C \"omniloan_dir\" log -1 \"--pretty=%H %B\"]")
            );
        }
    }

    @DisplayName("reset to commit")
    @Nested
    public class ResetToCommit {
        @Test
        public void success() {
            gitStub().add("-C (.+) status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) reset --hard HEAD", "HEAD is now at cc12db8 Hello", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("reset to commit");

            check().assertOutputEqual(
                    "Откатываюсь на текущий коммит",
                    "[omniutils] Откат на текущий коммит в ветке [sample_branch] успешно завершен",
                    "[omniloan] Откат на текущий коммит в ветке [sample_branch] успешно завершен",
                    "Откат на текущий коммит завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir reset --hard HEAD",
                    "-C omniloan_dir status",
                    "-C omniloan_dir reset --hard HEAD"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Откат на текущий коммит в ветке [sample_branch]. Откат на текущий коммит успешно завершен. Команды - [-C \"omniutils_dir\" reset --hard HEAD]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Откат на текущий коммит в ветке [sample_branch]. Откат на текущий коммит успешно завершен. Команды - [-C \"omniloan_dir\" reset --hard HEAD]")
            );
        }

        @Test
        public void cannot_reset_in_one_project() {
            gitStub().add("-C (.+) status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C omniutils_dir reset --hard HEAD", "some unexpected\nerror", 1)
                    .add("-C omniloan_dir reset --hard HEAD", "HEAD is now at cc12db8 Hello", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("reset to commit");

            check().assertOutputEqual(
                    "Откатываюсь на текущий коммит",
                    error("[omniutils] Не удалось откатиться на текущий коммит в ветке [sample_branch]"),
                    "[omniloan] Откат на текущий коммит в ветке [sample_branch] успешно завершен",
                    "Откат на текущий коммит завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir reset --hard HEAD",
                    "-C omniloan_dir status",
                    "-C omniloan_dir reset --hard HEAD"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Откат на текущий коммит в ветке [sample_branch]. Не удалось откатиться на текущий коммит. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" reset --hard HEAD]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Откат на текущий коммит в ветке [sample_branch]. Откат на текущий коммит успешно завершен. Команды - [-C \"omniloan_dir\" reset --hard HEAD]")
            );
        }
    }

    @DisplayName("show changes")
    @Nested
    public class ShowChanges {
        @Test
        public void success() {
            gitStub().add("-C (.+) status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) --no-pager reflog show --no-abbrev sample_branch", "cc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{0}: reset: moving to HEAD~1\ne66a2a8b0fae2fa67ca8d874ad27a3a5bbca77cf branch_2@{1}: commit: kek\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{2}: branch: Created from HEAD", 0)
                    .add("-C (.+) commit --allow-empty -a -m \\[GROOT\\] ~Show changes checkpoint~", "[branch_2 711fbea] [GROOT] ~Show changes checkpoint~", 0)
                    .add("-C (.+) reset --soft (.+)", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("show changes");

            check().assertOutputEqual(
                    "Показываю изменения",
                    "[omniutils] Показываю изменения в ветке [sample_branch]",
                    "[omniloan] Показываю изменения в ветке [sample_branch]",
                    "Показ изменений завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniutils_dir commit --allow-empty -a -m [GROOT] ~Show changes checkpoint~",
                    "-C omniutils_dir reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2",
                    "-C omniloan_dir status",
                    "-C omniloan_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniloan_dir commit --allow-empty -a -m [GROOT] ~Show changes checkpoint~",
                    "-C omniloan_dir reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2"
                    );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Показ изменений в ветке [sample_branch]. Команды - [-C \"omniutils_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniutils_dir\" commit --allow-empty -a -m \"[GROOT] ~Show changes checkpoint~\";-C \"omniutils_dir\" reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Показ изменений в ветке [sample_branch]. Команды - [-C \"omniloan_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniloan_dir\" commit --allow-empty -a -m \"[GROOT] ~Show changes checkpoint~\";-C \"omniloan_dir\" reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2]")
            );
        }

        @Test
        public void cannot_find_branch_start_in_one_project() {
            gitStub().add("-C (.+) status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C omniutils_dir --no-pager reflog show --no-abbrev sample_branch", "cc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{0}: reset: moving to HEAD~1\ne66a2a8b0fae2fa67ca8d874ad27a3a5bbca77cf branch_2@{1}: commit: kek\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{2}: branch: Moved from HEAD", 0)
                    .add("-C omniloan_dir --no-pager reflog show --no-abbrev sample_branch", "cc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{0}: reset: moving to HEAD~1\ne66a2a8b0fae2fa67ca8d874ad27a3a5bbca77cf branch_2@{1}: commit: kek\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{2}: branch: Created from HEAD", 0)
                    .add("-C (.+) commit --allow-empty -a -m \\[GROOT\\] ~Show changes checkpoint~", "[branch_2 711fbea] [GROOT] ~Show changes checkpoint~", 0)
                    .add("-C (.+) reset --soft (.+)", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("show changes");

            check().assertOutputEqual(
                    "Показываю изменения",
                    error("[omniutils] Не удалось показать изменения в ветке [sample_branch]"),
                    "[omniloan] Показываю изменения в ветке [sample_branch]",
                    "Показ изменений завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniloan_dir status",
                    "-C omniloan_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniloan_dir commit --allow-empty -a -m [GROOT] ~Show changes checkpoint~",
                    "-C omniloan_dir reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2"
                    );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Показ изменений в ветке [sample_branch]. Не удалось показать изменения. Причина ошибки - [Не удалось найти начало ветки]. Команды - [-C \"omniutils_dir\" --no-pager reflog show --no-abbrev sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Показ изменений в ветке [sample_branch]. Команды - [-C \"omniloan_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniloan_dir\" commit --allow-empty -a -m \"[GROOT] ~Show changes checkpoint~\";-C \"omniloan_dir\" reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2]")
            );
        }

        @Test
        public void cannot_create_checkpoint_in_one_project() {
            gitStub().add("-C (.+) status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) --no-pager reflog show --no-abbrev sample_branch", "cc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{0}: reset: moving to HEAD~1\ne66a2a8b0fae2fa67ca8d874ad27a3a5bbca77cf branch_2@{1}: commit: kek\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{2}: branch: Created from HEAD", 0)
                    .add("-C omniutils_dir commit --allow-empty -a -m \\[GROOT\\] ~Show changes checkpoint~", "some unexpected\nerror", 1)
                    .add("-C omniloan_dir commit --allow-empty -a -m \\[GROOT\\] ~Show changes checkpoint~", "[branch_2 711fbea] [GROOT] ~Show changes checkpoint~", 0)
                    .add("-C (.+) reset --soft (.+)", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("show changes");

            check().assertOutputEqual(
                    "Показываю изменения",
                    error("[omniutils] Не удалось показать изменения в ветке [sample_branch]"),
                    "[omniloan] Показываю изменения в ветке [sample_branch]",
                    "Показ изменений завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniutils_dir commit --allow-empty -a -m [GROOT] ~Show changes checkpoint~",
                    "-C omniloan_dir status",
                    "-C omniloan_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniloan_dir commit --allow-empty -a -m [GROOT] ~Show changes checkpoint~",
                    "-C omniloan_dir reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2"
                    );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Показ изменений в ветке [sample_branch]. Не удалось показать изменения. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniutils_dir\" commit --allow-empty -a -m \"[GROOT] ~Show changes checkpoint~\"]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Показ изменений в ветке [sample_branch]. Команды - [-C \"omniloan_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniloan_dir\" commit --allow-empty -a -m \"[GROOT] ~Show changes checkpoint~\";-C \"omniloan_dir\" reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2]")
            );
        }

        @Test
        public void cannot_checkout_in_one_project() {
            gitStub().add("-C (.+) status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) --no-pager reflog show --no-abbrev sample_branch", "cc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{0}: reset: moving to HEAD~1\ne66a2a8b0fae2fa67ca8d874ad27a3a5bbca77cf branch_2@{1}: commit: kek\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{2}: branch: Created from HEAD", 0)
                    .add("-C (.+) commit --allow-empty -a -m \\[GROOT\\] ~Show changes checkpoint~", "[branch_2 711fbea] [GROOT] ~Show changes checkpoint~", 0)
                    .add("-C omniutils_dir reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2", "some unexpected\nerror", 1)
                    .add("-C omniutils_dir reset --hard 711fbea", "", 0)
                    .add("-C omniutils_dir reset --soft HEAD~1", "", 0)
                    .add("-C omniloan_dir reset --soft (.+)", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("show changes");

            check().assertOutputEqual(
                    "Показываю изменения",
                    error("[omniutils] Не удалось показать изменения в ветке [sample_branch]"),
                    "[omniloan] Показываю изменения в ветке [sample_branch]",
                    "Показ изменений завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniutils_dir commit --allow-empty -a -m [GROOT] ~Show changes checkpoint~",
                    "-C omniutils_dir reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2",
                    "-C omniutils_dir reset --hard 711fbea",
                    "-C omniutils_dir reset --soft HEAD~1",
                    "-C omniloan_dir status",
                    "-C omniloan_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniloan_dir commit --allow-empty -a -m [GROOT] ~Show changes checkpoint~",
                    "-C omniloan_dir reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2"
                    );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Показ изменений в ветке [sample_branch]. Не удалось показать изменения. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniutils_dir\" commit --allow-empty -a -m \"[GROOT] ~Show changes checkpoint~\";-C \"omniutils_dir\" reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2;-C \"omniutils_dir\" reset --hard 711fbea;-C \"omniutils_dir\" reset --soft HEAD~1]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Показ изменений в ветке [sample_branch]. Команды - [-C \"omniloan_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniloan_dir\" commit --allow-empty -a -m \"[GROOT] ~Show changes checkpoint~\";-C \"omniloan_dir\" reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2]")
            );
        }

        @Test
        public void cannot_reset_to_checkpoint_after_failed_checkout() {
            gitStub().add("-C (.+) status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) --no-pager reflog show --no-abbrev sample_branch", "cc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{0}: reset: moving to HEAD~1\ne66a2a8b0fae2fa67ca8d874ad27a3a5bbca77cf branch_2@{1}: commit: kek\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{2}: branch: Created from HEAD", 0)
                    .add("-C (.+) commit --allow-empty -a -m \\[GROOT\\] ~Show changes checkpoint~", "[branch_2 711fbea] [GROOT] ~Show changes checkpoint~", 0)
                    .add("-C omniutils_dir reset --soft (.+)", "some unexpected\nerror", 1)
                    .add("-C omniutils_dir reset --hard 711fbea", "another error", 1)
                    .add("-C omniloan_dir reset --soft (.+)", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("show changes");

            check().assertOutputEqual(
                    "Показываю изменения",
                    error("[omniutils] Не удалось показать изменения в ветке [sample_branch]"),
                    "[omniloan] Показываю изменения в ветке [sample_branch]",
                    "Показ изменений завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniutils_dir commit --allow-empty -a -m [GROOT] ~Show changes checkpoint~",
                    "-C omniutils_dir reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2",
                    "-C omniutils_dir reset --hard 711fbea",
                    "-C omniloan_dir status",
                    "-C omniloan_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniloan_dir commit --allow-empty -a -m [GROOT] ~Show changes checkpoint~",
                    "-C omniloan_dir reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2"
                    );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Показ изменений в ветке [sample_branch]. Не удалось показать изменения. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniutils_dir\" commit --allow-empty -a -m \"[GROOT] ~Show changes checkpoint~\";-C \"omniutils_dir\" reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2;-C \"omniutils_dir\" reset --hard 711fbea]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Показ изменений в ветке [sample_branch]. Команды - [-C \"omniloan_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniloan_dir\" commit --allow-empty -a -m \"[GROOT] ~Show changes checkpoint~\";-C \"omniloan_dir\" reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2]")
            );
        }

        @Test
        public void cannot_show_changes_in_one_project_because_they_are_currently_showing() {
            gitStub().add("-C (.+) status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C omniutils_dir --no-pager reflog show --no-abbrev sample_branch", "cc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{0}: reset: moving to cc12db8403863270da16d306b5e7aea2ea6121b2\n66fc31a80915349b8d7a458a3c2de0dc326ea884 branch_1@{1}: commit: [GROOT] ~Show changes checkpoint~\n5c0c5977997e9c4946b01dcc0dab05527205de35 branch_1@{2}: reset: moving to 5c0c5977997e9c4946b01dcc0dab05527205de35\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{3}: reset: moving to HEAD~1\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{42}: branch: Created from master", 0)
                    .add("-C omniloan_dir --no-pager reflog show --no-abbrev sample_branch", "cc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{0}: reset: moving to HEAD~1\ne66a2a8b0fae2fa67ca8d874ad27a3a5bbca77cf branch_2@{1}: commit: kek\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_2, branch_1) branch_2@{2}: branch: Created from HEAD", 0)
                    .add("-C (.+) commit --allow-empty -a -m \\[GROOT\\] ~Show changes checkpoint~", "[branch_2 711fbea] [GROOT] ~Show changes checkpoint~", 0)
                    .add("-C (.+) reset --soft (.+)", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("show changes");

            check().assertOutputEqual(
                    "Показываю изменения",
                    warn("[omniutils] Вы уже просматриваете изменения в ветке [sample_branch]"),
                    "[omniloan] Показываю изменения в ветке [sample_branch]",
                    "Показ изменений завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniloan_dir status",
                    "-C omniloan_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniloan_dir commit --allow-empty -a -m [GROOT] ~Show changes checkpoint~",
                    "-C omniloan_dir reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.WARN, "[omniutils] Показ изменений в ветке [sample_branch]. Уже идет показ изменений. Команды - [-C \"omniutils_dir\" --no-pager reflog show --no-abbrev sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Показ изменений в ветке [sample_branch]. Команды - [-C \"omniloan_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniloan_dir\" commit --allow-empty -a -m \"[GROOT] ~Show changes checkpoint~\";-C \"omniloan_dir\" reset --soft cc12db8403863270da16d306b5e7aea2ea6121b2]")
            );
        }
    }

    @DisplayName("unshow changes")
    @Nested
    public class UnshowChanges {
        @Test
        public void success() {
            gitStub().add("-C (.+) status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) --no-pager reflog show --no-abbrev sample_branch", "cc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{0}: reset: moving to cc12db8403863270da16d306b5e7aea2ea6121b2\n5c0c5977997e9c4946b01dcc0dab05527205de35 branch_1@{1}: commit: [GROOT] ~Show changes checkpoint~\n06bae2315061132c4cd8628435ae1ceb82d73026 branch_1@{2}: commit: hello\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{3}: reset: moving to HEAD~1\nc8643b1ca6c6afabe71cbb6a1d563a84db51eed6 branch_1@{4}: commit: [GROOT] ~Show changes checkpoint~\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{5}: Branch: renamed refs/heads/kek to refs/heads/branch_1", 0)
                    .add("-C (.+) reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35", "", 0)
                    .add("-C (.+) reset --soft HEAD~1", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("unshow changes");

            check().assertOutputEqual(
                    "Откатываю показ изменений",
                    "[omniutils] Откат показа изменений в ветке [sample_branch] успешно завершен",
                    "[omniloan] Откат показа изменений в ветке [sample_branch] успешно завершен",
                    "Откат показа изменений завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniutils_dir reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35",
                    "-C omniutils_dir reset --soft HEAD~1",
                    "-C omniloan_dir status",
                    "-C omniloan_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniloan_dir reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35",
                    "-C omniloan_dir reset --soft HEAD~1"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Откат показа изменений в ветке [sample_branch]. Команды - [-C \"omniutils_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniutils_dir\" reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35;-C \"omniutils_dir\" reset --soft HEAD~1]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Откат показа изменений в ветке [sample_branch]. Команды - [-C \"omniloan_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniloan_dir\" reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35;-C \"omniloan_dir\" reset --soft HEAD~1]")
            );
        }

        @Test
        public void cannot_find_checkpoint_in_one_project() {
            gitStub().add("-C (.+) status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C omniutils_dir --no-pager reflog show --no-abbrev sample_branch", "cc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{0}: reset: moving to cc12db8403863270da16d306b5e7aea2ea6121b2\n5c0c5977997e9c4946b01dcc0dab05527205de35 branch_1@{1}: commit: hello\n06bae2315061132c4cd8628435ae1ceb82d73026 branch_1@{2}: commit: hello\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{3}: reset: moving to HEAD~1\nc8643b1ca6c6afabe71cbb6a1d563a84db51eed6 branch_1@{4}: commit: [GROOT] ~Show changes checkpoint~\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{5}: Branch: renamed refs/heads/kek to refs/heads/branch_1", 0)
                    .add("-C omniloan_dir --no-pager reflog show --no-abbrev sample_branch", "cc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{0}: reset: moving to cc12db8403863270da16d306b5e7aea2ea6121b2\n5c0c5977997e9c4946b01dcc0dab05527205de35 branch_1@{1}: commit: [GROOT] ~Show changes checkpoint~\n06bae2315061132c4cd8628435ae1ceb82d73026 branch_1@{2}: commit: hello\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{3}: reset: moving to HEAD~1\nc8643b1ca6c6afabe71cbb6a1d563a84db51eed6 branch_1@{4}: commit: [GROOT] ~Show changes checkpoint~\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{5}: Branch: renamed refs/heads/kek to refs/heads/branch_1", 0)
                    .add("-C (.+) reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35", "", 0)
                    .add("-C (.+) reset --soft HEAD~1", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("unshow changes");

            check().assertOutputEqual(
                    "Откатываю показ изменений",
                    warn("[omniutils] Сейчас не производится показ изменений в ветке [sample_branch]"),
                    "[omniloan] Откат показа изменений в ветке [sample_branch] успешно завершен",
                    "Откат показа изменений завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniloan_dir status",
                    "-C omniloan_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniloan_dir reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35",
                    "-C omniloan_dir reset --soft HEAD~1"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.WARN, "[omniutils] Откат показа изменений в ветке [sample_branch]. Сейчас не производится показ изменений. Команды - [-C \"omniutils_dir\" --no-pager reflog show --no-abbrev sample_branch]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Откат показа изменений в ветке [sample_branch]. Команды - [-C \"omniloan_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniloan_dir\" reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35;-C \"omniloan_dir\" reset --soft HEAD~1]")
            );
        }

        @Test
        public void cannot_reset_to_checkpoint_in_one_project() {
            gitStub().add("-C (.+) status", "On branch sample_branch\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) --no-pager reflog show --no-abbrev sample_branch", "cc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{0}: reset: moving to cc12db8403863270da16d306b5e7aea2ea6121b2\n5c0c5977997e9c4946b01dcc0dab05527205de35 branch_1@{1}: commit: [GROOT] ~Show changes checkpoint~\n06bae2315061132c4cd8628435ae1ceb82d73026 branch_1@{2}: commit: hello\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{3}: reset: moving to HEAD~1\nc8643b1ca6c6afabe71cbb6a1d563a84db51eed6 branch_1@{4}: commit: [GROOT] ~Show changes checkpoint~\ncc12db8403863270da16d306b5e7aea2ea6121b2 (HEAD -> branch_1, branch_2) branch_1@{5}: Branch: renamed refs/heads/kek to refs/heads/branch_1", 0)
                    .add("-C omniutils_dir reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35", "some unexpected\nerror", 1)
                    .add("-C omniloan_dir reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35", "", 0)
                    .add("-C (.+) reset --soft HEAD~1", "", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("unshow changes");

            check().assertOutputEqual(
                    "Откатываю показ изменений",
                    error("[omniutils] Не удалось отменить показ изменений в ветке [sample_branch]"),
                    "[omniloan] Откат показа изменений в ветке [sample_branch] успешно завершен",
                    "Откат показа изменений завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniutils_dir reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35",
                    "-C omniloan_dir status",
                    "-C omniloan_dir --no-pager reflog show --no-abbrev sample_branch",
                    "-C omniloan_dir reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35",
                    "-C omniloan_dir reset --soft HEAD~1"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Откат показа изменений в ветке [sample_branch]. Не удалось отменить показ изменений. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniutils_dir\" reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Откат показа изменений в ветке [sample_branch]. Команды - [-C \"omniloan_dir\" --no-pager reflog show --no-abbrev sample_branch;-C \"omniloan_dir\" reset --hard 5c0c5977997e9c4946b01dcc0dab05527205de35;-C \"omniloan_dir\" reset --soft HEAD~1]")
            );
        }
    }

    @DisplayName("commit")
    @Nested
    public class Commit {
        @Test
        public void success() {
            gitStub().add("-C omniutils_dir status", "On branch feature/CBKZR-8000_2\nChanges to be committed:\n  (use \"git restore --staged <file>...\" to unstage)\n	modified:   file\n	new file:   ok\n", 0)
                    .add("-C omniloan_dir status", "On branch feature/CBKZR-8000\nChanges to be committed:\n  (use \"git restore --staged <file>...\" to unstage)\n	modified:   file\n	new file:   ok\n", 0)
                    .add("-C omniutils_dir commit -a -m (.+)", "[feature/CBKZR-8000 ceca2e6] $1\n 2 files changed, 2 insertions(+), 1 deletion(-)\n create mode 100644 ok", 0)
                    .add("-C omniloan_dir commit -a -m (.+)", "[feature/CBKZR-8000 ceca2e6] $1\n 2 files changed, 2 insertions(+), 1 deletion(-)\n create mode 100644 ok", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .withBranchNamePattern("(feature|bugfix)\\/(?<taskNumber>[A-Z]+-[0-9]+)(_[0-9]+){0,1}")
                    .create();

            groot().run("commit \"Отказ от транзакта\"");

            check().assertOutputEqual(
                    "Коммичу",
                    "[omniutils] Коммит [CBKZR-8000 Отказ от транзакта] успешно выполнен [feature/CBKZR-8000_2]",
                    "[omniloan] Коммит [CBKZR-8000 Отказ от транзакта] успешно выполнен [feature/CBKZR-8000]",
                    "Коммит завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir commit -a -m CBKZR-8000 Отказ от транзакта",
                    "-C omniloan_dir status",
                    "-C omniloan_dir commit -a -m CBKZR-8000 Отказ от транзакта"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Коммит в ветке [feature/CBKZR-8000_2]. Коммит успешно выполенен. Сообщение - [CBKZR-8000 Отказ от транзакта]. Команды - [-C \"omniutils_dir\" commit -a -m \"CBKZR-8000 Отказ от транзакта\"]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Коммит в ветке [feature/CBKZR-8000]. Коммит успешно выполенен. Сообщение - [CBKZR-8000 Отказ от транзакта]. Команды - [-C \"omniloan_dir\" commit -a -m \"CBKZR-8000 Отказ от транзакта\"]")
            );
        }

        @Test
        public void no_branch_name_pattern_in_configuration() {
            gitStub().create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("commit \"CBKZR-8000 Отказ от транзакта\"");

            check().assertOutputEqual(
                    error("В конфигурации отсутствует шаблон названия ветки (член branchNamePattern)")
            );

            check().assertGitRequestsEqual();

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "Коммит с сообщением [CBKZR-8000 Отказ от транзакта]. Не удалось выполнить коммит. В конфигурации отсутствует шаблон названия ветки (член branchNamePattern)")
            );
        }

        @Test
        public void no_task_number_group_in_branch_name_pattern() {
            gitStub().create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .withBranchNamePattern("(feature|bugfix)\\/([A-Z]+-[0-9]+)(_[0-9]+){0,1}")
                    .create();

            groot().run("commit \"CBKZR-8000 Отказ от транзакта\"");

            check().assertOutputEqual(
                    error("В шаблоне названия ветки отсутствует группа taskNumber (?<taskNumber>)")
            );

            check().assertGitRequestsEqual();

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "Коммит с сообщением [CBKZR-8000 Отказ от транзакта]. Не удалось выполнить коммит. В шаблоне названия ветки отсутствует группа taskNumber (?<task_number>). Шаблон названия ветки - [(feature|bugfix)\\/([A-Z]+-[0-9]+)(_[0-9]+){0,1}]")
            );
        }

        @Test
        public void task_number_present_in_commit_message() {
            gitStub().add("-C (.+) status", "On branch feature/CBKZR-8000_2\nChanges to be committed:\n  (use \"git restore --staged <file>...\" to unstage)\n	modified:   file\n	new file:   ok\n", 0)
                    .add("-C (.+) commit -a -m (.+)", "[feature/CBKZR-8000_2 ceca2e6] $2\n 2 files changed, 2 insertions(+), 1 deletion(-)\n create mode 100644 ok", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withBranchNamePattern("(feature|bugfix)\\/(?<taskNumber>[A-Z]+-[0-9]+)(_[0-9]+){0,1}")
                    .create();

            groot().run("commit \"CBKZR-8000 Отказ от транзакта\"");

            check().assertOutputEqual(
                    "Коммичу",
                    "[omniutils] Коммит [CBKZR-8000 Отказ от транзакта] успешно выполнен [feature/CBKZR-8000_2]",
                    "Коммит завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir commit -a -m CBKZR-8000 Отказ от транзакта"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Коммит в ветке [feature/CBKZR-8000_2]. Коммит успешно выполенен. Сообщение - [CBKZR-8000 Отказ от транзакта]. Команды - [-C \"omniutils_dir\" commit -a -m \"CBKZR-8000 Отказ от транзакта\"]")
            );
        }

        @Test
        public void task_number_from_commit_message_does_not_match_task_number_from_branch_name() {
            gitStub().add("-C (.+) status", "On branch feature/CBKZR-8000_2\nChanges to be committed:\n  (use \"git restore --staged <file>...\" to unstage)\n	modified:   file\n	new file:   ok\n", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withBranchNamePattern("(feature|bugfix)\\/(?<taskNumber>[A-Z]+-[0-9]+)(_[0-9]+){0,1}")
                    .create();

            groot().run("commit \"CBKZR-8001 Отказ от транзакта\"");

            check().assertOutputEqual(
                    "Коммичу",
                    error("[omniutils] Номер таски в сообщении коммита [CBKZR-8001] не совпадает с номером таски в названии ветки [CBKZR-8000]"),
                    "Коммит завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Коммит в ветке [feature/CBKZR-8000_2]. Не удалось выполнить коммит. Номер таски в сообщении коммита [CBKZR-8001] не совпадает с номером таски в названии ветки [CBKZR-8000]. Команды - []")
            );
        }

        @Test
        public void cannot_get_task_number_from_branch_name() {
            gitStub().add("-C (.+) status", "On branch feature/reject\nChanges to be committed:\n  (use \"git restore --staged <file>...\" to unstage)\n	modified:   file\n	new file:   ok\n", 0)
                    .add("-C (.+) commit -a -m (.+)", "[feature/reject ceca2e6] $2\n 2 files changed, 2 insertions(+), 1 deletion(-)\n create mode 100644 ok", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withBranchNamePattern("(feature|bugfix)\\/(?<taskNumber>[A-Z]+-[0-9]+)(_[0-9]+){0,1}")
                    .create();

            groot().run("commit \"Отказ от транзакта\"");

            check().assertOutputEqual(
                    "Коммичу",
                    "[omniutils] Коммит [Отказ от транзакта] успешно выполенен [feature/reject] (не удалось определить номер таски из ветки, использовано сообщение коммита в исходном виде)",
                    "Коммит завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir commit -a -m Отказ от транзакта"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.WARN, "[omniutils] Коммит в ветке [feature/reject]. Коммит успешно выполнен. Не удалось определить номер таски из ветки, использовано сообщение коммита в исходном виде. Сообщение - [Отказ от транзакта]. Команды - [-C \"omniutils_dir\" commit -a -m \"Отказ от транзакта\"]")
            );
        }

        @Test
        public void no_changes_in_branch() {
            gitStub().add("-C (.+) status", "On branch feature/CBKZR-8000\nnothing to commit, working tree clean", 0)
                    .add("-C (.+) commit -a -m (.+)", "On branch feature/CBKZR-8000\nnothing to commit, working tree clean", 1)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withBranchNamePattern("(feature|bugfix)\\/(?<taskNumber>[A-Z]+-[0-9]+)(_[0-9]+){0,1}")
                    .create();

            groot().run("commit \"Отказ от транзакта\"");

            check().assertOutputEqual(
                    "Коммичу",
                    warn("[omniutils] В ветке [feature/CBKZR-8000] не было изменений"),
                    "Коммит завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir commit -a -m CBKZR-8000 Отказ от транзакта"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.WARN, "[omniutils] Коммит в ветке [feature/CBKZR-8000]. В ветке не было изменений. Команды - [-C \"omniutils_dir\" commit -a -m \"CBKZR-8000 Отказ от транзакта\"]")
            );
        }

        @Test
        public void cannot_commit() {
            gitStub().add("-C (.+) status", "On branch feature/CBKZR-8000_2\nChanges to be committed:\n  (use \"git restore --staged <file>...\" to unstage)\n	modified:   file\n	new file:   ok\n", 0)
                    .add("-C (.+) commit -a -m (.+)", "some unexpected\nerror", 1)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withBranchNamePattern("(feature|bugfix)\\/(?<taskNumber>[A-Z]+-[0-9]+)(_[0-9]+){0,1}")
                    .create();

            groot().run("commit \"Отказ от транзакта\"");

            check().assertOutputEqual(
                    "Коммичу",
                    error("[omniutils] Не удалось выполнить коммит в ветке [feature/CBKZR-8000_2]"),
                    "Коммит завершен"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir status",
                    "-C omniutils_dir commit -a -m CBKZR-8000 Отказ от транзакта"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Коммит в ветке [feature/CBKZR-8000_2]. Не удалось выполнить коммит. Причина ошибки - [some unexpected error]. Команды - [-C \"omniutils_dir\" commit -a -m \"CBKZR-8000 Отказ от транзакта\"]")
            );
        }
    }

    @DisplayName("current branches")
    @Nested
    public class CurrentBranches {
        @Test
        public void successful_current_branch_name_retrieval() {
            gitStub().add("-C omniutils_dir rev-parse --abbrev-ref HEAD", "feature/CBKZR-8000", 0)
                    .add("-C omniloan_dir rev-parse --abbrev-ref HEAD", "feature/CBKZR-8001", 0)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("current branches");

            check().assertOutputEqual(
                    "Получаю названия веток",
                    "[omniutils] feature/CBKZR-8000",
                    "[omniloan] feature/CBKZR-8001",
                    "Получение названия веток завершено"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir rev-parse --abbrev-ref HEAD",
                    "-C omniloan_dir rev-parse --abbrev-ref HEAD"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.INFO, "[omniutils] Получение названия текущей ветки. Название - [feature/CBKZR-8000]. Команды - [-C omniutils_dir rev-parse --abbrev-ref HEAD]"),
                    new GrootLogEntry(LogLevel.INFO, "[omniloan] Получение названия текущей ветки. Название - [feature/CBKZR-8001]. Команды - [-C omniutils_dir rev-parse --abbrev-ref HEAD]")
            );
        }

        @Test
        public void cannot_retrieve_current_branch_name() {
            gitStub().add("-C omniutils_dir rev-parse --abbrev-ref HEAD", "some unexpected\nerror", 1)
                    .add("-C omniloan_dir rev-parse --abbrev-ref HEAD", "some unexpected\nerror", 1)
                    .create();

            groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                    .withProjectEntry("omniloan", "omniloan_dir", "omniloan_url")
                    .create();

            groot().run("current branches");

            check().assertOutputEqual(
                    "Получаю названия веток",
                    "[omniutils] Не удалось получить название ветки",
                    "[omniloan] Не удалось получить название ветки",
                    "Получение названия веток завершено"
            );

            check().assertGitRequestsEqual(
                    "-C omniutils_dir rev-parse --abbrev-ref HEAD",
                    "-C omniloan_dir rev-parse --abbrev-ref HEAD"
            );

            check().assertLogsEqual(
                    new GrootLogEntry(LogLevel.ERROR, "[omniutils] Получение названия текущей ветки. Не удалось получить название. Причина ошибки - [some unexpected error]. Команды - [-C omniutils_dir rev-parse --abbrev-ref HEAD]"),
                    new GrootLogEntry(LogLevel.ERROR, "[omniloan] Получение названия текущей ветки. Не удалось получить название. Причина ошибки - [some unexpected error]. Команды - [-C omniutils_dir rev-parse --abbrev-ref HEAD]")
            );
        }
    }
}
