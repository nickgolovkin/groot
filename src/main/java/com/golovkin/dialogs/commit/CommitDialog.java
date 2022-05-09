package com.golovkin.dialogs.commit;

import com.golovkin.RegexUtils;
import com.golovkin.config.ProjectEntry;
import com.golovkin.dialogs.AbstractDialog;
import com.golovkin.git.Git;
import com.golovkin.git.GitUtils;
import com.golovkin.git.commands.commit.CommitGitCommand;
import com.golovkin.git.commands.commit.CommitGitCommandInput;
import com.golovkin.git.commands.commit.CommitGitCommandOutput;
import com.golovkin.git.exceptions.NothingToCommitException;
import com.golovkin.git.exceptions.TaskNumberFromBranchNameDoesNotMatchTaskNumberFromCommitMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

import static com.golovkin.common.ColorUtils.error;
import static com.golovkin.common.ColorUtils.warn;
import static com.golovkin.common.PrintUtils.printf;

public class CommitDialog extends AbstractDialog<CommitDialogInput, CommitDialogInputParser> {
    private final static Logger LOGGER = LoggerFactory.getLogger(CommitDialog.class);
    private final static Pattern TASK_NUMBER_GROUP_PATTERN = Pattern.compile("\\(\\?\\<taskNumber\\>.+?\\)");
    private final Pattern branchNamePattern;
    private final String taskNumberPattern;

    public CommitDialog(Git git, List<ProjectEntry> projectEntries, String branchNamePattern) {
        super(git, projectEntries);
        // TODO это кривота из-за которой может посыпаться NPE, перенести в start
        this.branchNamePattern = branchNamePattern == null ? null : Pattern.compile(branchNamePattern);
        this.taskNumberPattern = RegexUtils.extractFirstOccurrence(branchNamePattern, TASK_NUMBER_GROUP_PATTERN);
    }

    @Override
    public void start(CommitDialogInput input) {
        CommitGitCommand commitGitCommand = new CommitGitCommand(getGit());
        GitUtils gitUtils = new GitUtils(getGit());

        if (branchNamePattern == null) {
            printf(error("В конфигурации отсутствует шаблон названия ветки (член branchNamePattern)"));
            LOGGER.error("Коммит с сообщением [{}]. Не удалось выполнить коммит. В конфигурации отсутствует шаблон названия ветки (член branchNamePattern)", input.getCommitMessage());
            return;
        }

        if (taskNumberPattern == null) {
            printf(error("В шаблоне названия ветки отсутствует группа taskNumber (?<taskNumber>)"));
            LOGGER.error("Коммит с сообщением [{}]. Не удалось выполнить коммит. В шаблоне названия ветки отсутствует группа taskNumber (?<task_number>). Шаблон названия ветки - [{}]", input.getCommitMessage(), branchNamePattern.pattern());
            return;
        }

        String commitMessage = input.getCommitMessage();

        System.out.println("Коммичу");

        for (ProjectEntry projectEntry : getProjectEntries()) {
            String projectName = projectEntry.getName();
            String projectDirectoryPath = projectEntry.getDirectory();

            String branchName = gitUtils.getCurrentBranch(projectDirectoryPath);

            try {
                CommitGitCommandInput commandInput = new CommitGitCommandInput(commitMessage, branchName, branchNamePattern, taskNumberPattern, projectDirectoryPath);
                CommitGitCommandOutput commandOutput = commitGitCommand.execute(commandInput);

                if (commandOutput.isCannotGetTaskNumberFromBranchName()) {
                    printf("[%s] Коммит [%s] успешно выполнен [%s] (не удалось определить номер таски из ветки, использовано сообщение коммита в исходном виде)", projectName, commandOutput.getResolvedCommitMessage(), branchName);
                    LOGGER.warn("[{}] Коммит в ветке [{}]. Коммит успешно выполнен. Не удалось определить номер таски из ветки, использовано сообщение коммита в исходном виде. Сообщение - [{}]. Команды - [{}]", projectName, branchName, commandOutput.getResolvedCommitMessage(), getGit().getLastExecutedCommandsAsString());
                } else {
                    printf("[%s] Коммит [%s] успешно выполнен [%s]", projectName, commandOutput.getResolvedCommitMessage(), branchName);
                    LOGGER.info("[{}] Коммит в ветке [{}]. Коммит успешно выполнен. Сообщение - [{}]. Команды - [{}]", projectName, branchName, commandOutput.getResolvedCommitMessage(), getGit().getLastExecutedCommandsAsString());
                }
            } catch (TaskNumberFromBranchNameDoesNotMatchTaskNumberFromCommitMessageException e) {
                printf(error("[%s] Номер таски в сообщении коммита [%s] не совпадает с номером таски в названии ветки [%s]"), projectName, e.getTaskNumberFromCommitMessage(), e.getTaskNumberFromBranchName());
                LOGGER.error("[{}] Коммит в ветке [{}]. Не удалось выполнить коммит. Номер таски в сообщении коммита [{}] не совпадает с номером таски в названии ветки [{}]. Команды - [{}]", projectName, branchName, e.getTaskNumberFromCommitMessage(), e.getTaskNumberFromBranchName(), getGit().getLastExecutedCommandsAsString());
            } catch (NothingToCommitException e) {
                printf(warn("[%s] В ветке [%s] не было изменений"), projectName, branchName);
                LOGGER.warn("[{}] Коммит в ветке [{}]. В ветке не было изменений. Команды - [{}]", projectName, branchName, getGit().getLastExecutedCommandsAsString());
            } catch (Exception e) {
                printf(error("[%s] Не удалось выполнить коммит в ветке [%s]"), projectName, branchName);
                LOGGER.error("[{}] Коммит в ветке [{}]. Не удалось выполнить коммит. Причина ошибки - [{}]. Команды - [{}]", projectName, branchName, e.getMessage(), getGit().getLastExecutedCommandsAsString());
            }
        }

        printf("Коммит завершен", commitMessage);
    }

    @Override
    public CommitDialogInputParser getInputParser() {
        return new CommitDialogInputParser();
    }
}
