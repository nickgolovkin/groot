package com.golovkin.dialogs.abort;

import com.golovkin.config.ProjectEntry;
import com.golovkin.dialogs.AbstractDialog;
import com.golovkin.dialogs.deletebranch.DeleteBranchDialogInput;
import com.golovkin.dialogs.deletebranch.DeleteBranchDialogInputParser;
import com.golovkin.git.Git;
import com.golovkin.git.GitUtils;
import com.golovkin.git.commands.abort.AbortGitCommand;
import com.golovkin.git.commands.abort.AbortGitCommandInput;
import com.golovkin.git.commands.deletebranch.DeleteBranchGitCommand;
import com.golovkin.git.commands.deletebranch.DeleteBranchGitCommandInput;
import com.golovkin.git.exceptions.BranchNotFoundException;
import com.golovkin.git.exceptions.NoCherryPickToAbortException;
import com.golovkin.git.exceptions.NoMergeToAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.golovkin.common.ColorUtils.error;
import static com.golovkin.common.ColorUtils.warn;
import static com.golovkin.common.PrintUtils.printf;

public class AbortDialog extends AbstractDialog<AbortDialogInput, AbortDialogInputParser> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbortDialog.class);

    public AbortDialog(Git git, List<ProjectEntry> projectEntries) {
        super(git, projectEntries);
    }

    @Override
    public void start(AbortDialogInput input) {
        AbortGitCommand abortGitCommand = new AbortGitCommand(getGit());
        GitUtils gitUtils = new GitUtils(getGit());

        System.out.println("Произвожу отмену мержа/черри-пика");

        for (ProjectEntry projectEntry : getProjectEntries()) {
            String projectName = projectEntry.getName();
            String currentBranchName = gitUtils.getCurrentBranch(projectEntry.getDirectory());

            try {
                AbortGitCommandInput commandInput = new AbortGitCommandInput(projectEntry.getDirectory());
                abortGitCommand.execute(commandInput);
                printf("[%s] Мерж/черри-пик успешно отменен в [%s]", projectName, currentBranchName);
                LOGGER.info("[{}] Отмена мержа/черри-пика в ветке [{}]. Мерж/черри-пик успешно отменен. Команды - [{}]", projectName, currentBranchName, getGit().getLastExecutedCommandsAsString());
            } catch (NoMergeToAbortException | NoCherryPickToAbortException e) {
                printf(warn("[%s] Нет мержа/черри-пика для отмены в [%s]"), projectName, currentBranchName);
                LOGGER.warn("[{}] Отмена мержа/черри-пика в ветке [{}]. Нет мержа/черри-пика для отмены. Команды - [{}]", projectName, currentBranchName, getGit().getLastExecutedCommandsAsString());
            } catch (Exception e) {
                printf(error("[%s] Не удалось отменить мерж/черри-пик в [%s]"), projectName, currentBranchName);
                LOGGER.error("[{}] Отмена мержа/черри-пика в ветке [{}]. Не удалось отменить мерж/черри-пик. Причина ошибки - [{}]. Команды - [{}]", projectName, currentBranchName, e.getMessage(), getGit().getLastExecutedCommandsAsString());
            }
        }

        System.out.println("Отмена мержа/черри-пика завершена");
    }

    @Override
    public AbortDialogInputParser getInputParser() {
        return new AbortDialogInputParser();
    }
}
