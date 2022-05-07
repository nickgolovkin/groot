package com.golovkin.dialogs.unshowchanges;

import com.golovkin.config.ProjectEntry;
import com.golovkin.dialogs.AbstractDialog;
import com.golovkin.git.Git;
import com.golovkin.git.GitUtils;
import com.golovkin.git.commands.showchanges.ShowChangesGitCommand;
import com.golovkin.git.commands.showchanges.ShowChangesGitCommandInput;
import com.golovkin.git.commands.unshowchanges.UnshowChangesGitCommand;
import com.golovkin.git.commands.unshowchanges.UnshowChangesGitCommandInput;
import com.golovkin.git.exceptions.NoActiveShowChangesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UnshowChangesDialog extends AbstractDialog<UnshowChangesDialogInput, UnshowChangesDialogInputParser> {
    private final static Logger LOGGER = LoggerFactory.getLogger(UnshowChangesDialog.class);

    public UnshowChangesDialog(Git git, List<ProjectEntry> projectEntries) {
        super(git, projectEntries);
    }

    // TODO исключения, которые специальным образом в коде не обрабатываются заменить на GitException с сообщением
    @Override
    public void start(UnshowChangesDialogInput input) {
        UnshowChangesGitCommand unshowChangesGitCommand = new UnshowChangesGitCommand(getGit());
        GitUtils gitUtils = new GitUtils(getGit());

        System.out.println("Откатываю показ изменений");

        for (ProjectEntry projectEntry : getProjectEntries()) {
            String projectName = projectEntry.getName();
            String currentBranchName = gitUtils.getCurrentBranch(projectEntry.getDirectory());

            try {
                UnshowChangesGitCommandInput commandInput = new UnshowChangesGitCommandInput(currentBranchName, projectEntry.getDirectory());
                unshowChangesGitCommand.execute(commandInput);
                System.out.printf("[%s] Откат показа изменений в ветке [%s] успешно завершен\n", projectName, currentBranchName);
                LOGGER.info("[{}] Откат показа изменений в ветке [{}]. Команды - [{}]", projectName, currentBranchName, getGit().getLastExecutedCommandsAsString());
            } catch (NoActiveShowChangesException e) {
                System.out.printf("[%s] Сейчас не производится показ изменений в ветке [%s]\n", projectName, currentBranchName);
                LOGGER.warn("[{}] Откат показа изменений в ветке [{}]. Сейчас не производится показ изменений. Команды - [{}]", projectName, currentBranchName, getGit().getLastExecutedCommandsAsString());
            } catch (Exception e) {
                System.out.printf("[%s] Не удалось отменить показ изменений в ветке [%s]\n", projectName, currentBranchName);
                LOGGER.error("[{}] Откат показа изменений в ветке [{}]. Не удалось отменить показ изменений. Причина ошибки - [{}]. Команды - [{}]", projectName, currentBranchName, e.getMessage(), getGit().getLastExecutedCommandsAsString());
            }
        }

        System.out.println("Откат показа изменений завершен");
    }

    @Override
    public UnshowChangesDialogInputParser getInputParser() {
        return new UnshowChangesDialogInputParser();
    }
}
