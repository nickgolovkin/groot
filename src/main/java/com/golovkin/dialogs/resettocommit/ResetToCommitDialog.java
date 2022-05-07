package com.golovkin.dialogs.resettocommit;

import com.golovkin.config.ProjectEntry;
import com.golovkin.dialogs.AbstractDialog;
import com.golovkin.git.Git;
import com.golovkin.git.GitUtils;
import com.golovkin.git.commands.abort.AbortGitCommand;
import com.golovkin.git.commands.abort.AbortGitCommandInput;
import com.golovkin.git.commands.resettocommit.ResetToCommitGitCommand;
import com.golovkin.git.commands.resettocommit.ResetToCommitGitCommandInput;
import com.golovkin.git.exceptions.NoCherryPickToAbortException;
import com.golovkin.git.exceptions.NoMergeToAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.golovkin.common.ColorUtils.error;
import static com.golovkin.common.PrintUtils.printf;

public class ResetToCommitDialog extends AbstractDialog<ResetToCommitDialogInput, ResetToCommitDialogInputParser> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ResetToCommitDialog.class);

    public ResetToCommitDialog(Git git, List<ProjectEntry> projectEntries) {
        super(git, projectEntries);
    }

    @Override
    public void start(ResetToCommitDialogInput input) {
        ResetToCommitGitCommand resetToCommitGitCommand = new ResetToCommitGitCommand(getGit());
        GitUtils gitUtils = new GitUtils(getGit());

        System.out.println("Откатываюсь на текущий коммит");

        for (ProjectEntry projectEntry : getProjectEntries()) {
            String projectName = projectEntry.getName();
            String currentBranchName = gitUtils.getCurrentBranch(projectEntry.getDirectory());

            try {
                ResetToCommitGitCommandInput commandInput = new ResetToCommitGitCommandInput(projectEntry.getDirectory());
                resetToCommitGitCommand.execute(commandInput);
                printf("[%s] Откат на текущий коммит в ветке [%s] успешно завершен", projectName, currentBranchName);
                LOGGER.info("[{}] Откат на текущий коммит в ветке [{}]. Откат на текущий коммит успешно завершен. Команды - [{}]", projectName, currentBranchName, getGit().getLastExecutedCommandsAsString());
            } catch (Exception e) {
                printf(error("[%s] Не удалось откатиться на текущий коммит в ветке [%s]"), projectName, currentBranchName);
                LOGGER.error("[{}] Откат на текущий коммит в ветке [{}]. Не удалось откатиться на текущий коммит. Причина ошибки - [{}]. Команды - [{}]", projectName, currentBranchName, e.getMessage(), getGit().getLastExecutedCommandsAsString());
            }
        }

        System.out.println("Откат на текущий коммит завершен");
    }

    @Override
    public ResetToCommitDialogInputParser getInputParser() {
        return new ResetToCommitDialogInputParser();
    }
}
