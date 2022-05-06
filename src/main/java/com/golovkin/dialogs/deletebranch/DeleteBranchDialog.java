package com.golovkin.dialogs.deletebranch;

import com.golovkin.config.ProjectEntry;
import com.golovkin.dialogs.AbstractDialog;
import com.golovkin.git.Git;
import com.golovkin.git.commands.deletebranch.DeleteBranchGitCommand;
import com.golovkin.git.commands.deletebranch.DeleteBranchGitCommandInput;
import com.golovkin.git.exceptions.BranchNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DeleteBranchDialog extends AbstractDialog<DeleteBranchDialogInput, DeleteBranchDialogInputParser> {
    private final static Logger LOGGER = LoggerFactory.getLogger(DeleteBranchDialog.class);

    public DeleteBranchDialog(Git git,  List<ProjectEntry> projectEntries) {
        super(git, projectEntries);
    }

    @Override
    public void start(DeleteBranchDialogInput input) {
        DeleteBranchGitCommand deleteBranchGitCommand = new DeleteBranchGitCommand(getGit());

        String branchName = input.getName();

        System.out.printf("Удаляю ветку [%s]\n", branchName);

        for (ProjectEntry projectEntry : getProjectEntries()) {
            String projectName = projectEntry.getName();

            try {
                DeleteBranchGitCommandInput commandInput = new DeleteBranchGitCommandInput(branchName, projectEntry.getDirectory());
                deleteBranchGitCommand.execute(commandInput);
                System.out.printf("[%s] Ветка [%s] успешно удалена\n", projectName, branchName);
                LOGGER.info("[{}] Удаление ветки [{}]. Ветка успешно удалена. Команды - [{}]", projectName, branchName, getGit().getLastExecutedCommandsAsString());
            } catch (BranchNotFoundException e) {
                System.out.printf("[%s] Ветка [%s] не существует\n", projectName, branchName);
                LOGGER.warn("[{}] Удаление ветки [{}]. Ветка не существует. Команды - [{}]", projectName, branchName, getGit().getLastExecutedCommandsAsString());
            } catch (Exception e) {
                System.out.printf("[%s] Не удалось удалить ветку [%s]\n", projectName, branchName);
                LOGGER.error("[{}] Удаление ветки [{}]. Не удалось удалить ветку. Причина ошибки - [{}]. Команды - [{}]", projectName, branchName, e.getMessage(), getGit().getLastExecutedCommandsAsString());
            }
        }

        System.out.printf("Удаление ветки [%s] завершено\n", branchName);
    }

    @Override
    public DeleteBranchDialogInputParser getInputParser() {
        return new DeleteBranchDialogInputParser();
    }
}
