package com.golovkin.dialogs.currentbranches;

import com.golovkin.config.ProjectEntry;
import com.golovkin.dialogs.AbstractDialog;
import com.golovkin.git.Git;
import com.golovkin.git.commands.currentbranches.CurrentBranchesGitCommand;
import com.golovkin.git.commands.currentbranches.CurrentBranchesGitCommandInput;
import com.golovkin.git.commands.currentbranches.CurrentBranchesGitCommandOutput;
import com.golovkin.git.commands.deletebranch.DeleteBranchGitCommand;
import com.golovkin.git.commands.deletebranch.DeleteBranchGitCommandInput;
import com.golovkin.git.exceptions.BranchNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.golovkin.common.ColorUtils.error;
import static com.golovkin.common.ColorUtils.warn;
import static com.golovkin.common.PrintUtils.printf;

public class CurrentBranchesDialog extends AbstractDialog<CurrentBranchesDialogInput, CurrentBranchesDialogInputParser> {
    private final static Logger LOGGER = LoggerFactory.getLogger(CurrentBranchesDialog.class);

    public CurrentBranchesDialog(Git git, List<ProjectEntry> projectEntries) {
        super(git, projectEntries);
    }

    @Override
    public void start(CurrentBranchesDialogInput input) {
        CurrentBranchesGitCommand currentBranchesGitCommand = new CurrentBranchesGitCommand(getGit());

        System.out.println("Получаю названия веток");

        for (ProjectEntry projectEntry : getProjectEntries()) {
            String projectName = projectEntry.getName();

            try {
                CurrentBranchesGitCommandInput commandInput = new CurrentBranchesGitCommandInput(projectEntry.getDirectory());
                CurrentBranchesGitCommandOutput commandOutput = currentBranchesGitCommand.execute(commandInput);
                printf("[%s] %s", projectName, commandOutput.getCurrentBranchName());
                LOGGER.info("[{}] Получение названия текущей ветки. Название - [{}]. Команды - [{}]", projectName, commandOutput.getCurrentBranchName(), getGit().getLastExecutedCommandsAsString());
            } catch (Exception e) {
                printf(error("[%s] Не удалось получить название ветки"), projectName);
                LOGGER.error("[{}] Получение названия текущей ветки. Не удалось получить название. Причина ошибки - [{}]. Команды - [{}]", projectName, e.getMessage(), getGit().getLastExecutedCommandsAsString());
            }
        }

        System.out.println("Получение названия веток завершено");
    }

    @Override
    public CurrentBranchesDialogInputParser getInputParser() {
        return new CurrentBranchesDialogInputParser();
    }
}
