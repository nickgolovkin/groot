package com.golovkin.dialogs.renamebranch;

import com.golovkin.config.ProjectEntry;
import com.golovkin.dialogs.AbstractDialog;
import com.golovkin.git.Git;
import com.golovkin.git.GitUtils;
import com.golovkin.git.commands.renamebranch.RenameBranchGitCommand;
import com.golovkin.git.commands.renamebranch.RenameBranchGitCommandInput;
import com.golovkin.git.exceptions.BranchNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RenameBranchDialog extends AbstractDialog<RenameBranchDialogInput, RenameBranchDialogInputParser> {
    private final static Logger LOGGER = LoggerFactory.getLogger(RenameBranchDialog.class);

    public RenameBranchDialog(Git git,  List<ProjectEntry> projectEntries) {
        super(git, projectEntries);
    }

    @Override
    public void start(RenameBranchDialogInput input) {
        RenameBranchGitCommand renameBranchGitCommand = new RenameBranchGitCommand(getGit());
        GitUtils gitUtils = new GitUtils(getGit());

        String branchName = input.getName();

        System.out.printf("Переименовываю ветку в [%s]\n", branchName);

        for (ProjectEntry projectEntry : getProjectEntries()) {
            String projectName = projectEntry.getName();
            String currentBranchName = gitUtils.getCurrentBranch(projectEntry.getDirectory());

            try {
                RenameBranchGitCommandInput commandInput = new RenameBranchGitCommandInput(branchName, projectEntry.getDirectory());
                renameBranchGitCommand.execute(commandInput);
                System.out.printf("[%s] Ветка [%s] успешно переименована в [%s]\n", projectName, currentBranchName, branchName);
                LOGGER.info("[{}] Переименование ветки [{}] в [{}]. Ветка успешно переименована. Команды - [{}]", projectName, currentBranchName, branchName, getGit().getLastExecutedCommandsAsString());
            } catch (Exception e) {
                System.out.printf("[%s] Не удалось переименовать ветку [%s] в [%s]\n", projectName, currentBranchName, branchName);
                LOGGER.error("[{}] Переименование ветки [{}] в [{}]. Не удалось переименовать ветку. Причина ошибки - [{}]. Команды - [{}]", projectName, currentBranchName, branchName, e.getMessage(), getGit().getLastExecutedCommandsAsString());
            }
        }

        System.out.printf("Переименовывание ветки в [%s] завершено\n", branchName);
    }

    @Override
    public RenameBranchDialogInputParser getInputParser() {
        return new RenameBranchDialogInputParser();
    }
}