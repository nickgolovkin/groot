package com.golovkin.dialogs.showchanges;

import com.golovkin.config.ProjectEntry;
import com.golovkin.dialogs.AbstractDialog;
import com.golovkin.git.Git;
import com.golovkin.git.GitUtils;
import com.golovkin.git.commands.newbranch.NewBranchGitCommand;
import com.golovkin.git.commands.newbranch.NewBranchGitCommandInput;
import com.golovkin.git.commands.showchanges.ShowChangesGitCommand;
import com.golovkin.git.commands.showchanges.ShowChangesGitCommandInput;
import com.golovkin.git.exceptions.BranchAlreadyExistsException;
import com.golovkin.git.exceptions.ChangesAlreadyShowingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.golovkin.common.ColorUtils.error;
import static com.golovkin.common.ColorUtils.warn;
import static com.golovkin.common.PrintUtils.printf;

public class ShowChangesDialog extends AbstractDialog<ShowChangesDialogInput, ShowChangesDialogInputParser> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ShowChangesDialog.class);

    public ShowChangesDialog(Git git, List<ProjectEntry> projectEntries) {
        super(git, projectEntries);
    }

    @Override
    public void start(ShowChangesDialogInput input) {
        ShowChangesGitCommand showChangesGitCommand = new ShowChangesGitCommand(getGit());
        GitUtils gitUtils = new GitUtils(getGit());

        System.out.println("Показываю изменения");

        for (ProjectEntry projectEntry : getProjectEntries()) {
            String projectName = projectEntry.getName();
            String currentBranchName = gitUtils.getCurrentBranch(projectEntry.getDirectory());

            try {
                ShowChangesGitCommandInput commandInput = new ShowChangesGitCommandInput(currentBranchName, projectEntry.getDirectory());
                showChangesGitCommand.execute(commandInput);
                printf("[%s] Показываю изменения в ветке [%s]", projectName, currentBranchName);
                LOGGER.info("[{}] Показ изменений в ветке [{}]. Команды - [{}]", projectName, currentBranchName, getGit().getLastExecutedCommandsAsString());
            } catch (ChangesAlreadyShowingException e) {
                printf(warn("[%s] Вы уже просматриваете изменения в ветке [%s]"), projectName, currentBranchName);
                LOGGER.warn("[{}] Показ изменений в ветке [{}]. Уже идет показ изменений. Команды - [{}]", projectName, currentBranchName, getGit().getLastExecutedCommandsAsString());
            } catch (Exception e) {
                printf(error("[%s] Не удалось показать изменения в ветке [%s]"), projectName, currentBranchName);
                LOGGER.error("[{}] Показ изменений в ветке [{}]. Не удалось показать изменения. Причина ошибки - [{}]. Команды - [{}]", projectName, currentBranchName, e.getMessage(), getGit().getLastExecutedCommandsAsString());
            }
        }

        System.out.println("Показ изменений завершен");
    }

    @Override
    public ShowChangesDialogInputParser getInputParser() {
        return new ShowChangesDialogInputParser();
    }
}
