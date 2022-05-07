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

import static com.golovkin.common.ColorUtils.error;
import static com.golovkin.common.ColorUtils.warn;
import static com.golovkin.common.PrintUtils.printf;

public class DeleteBranchDialog extends AbstractDialog<DeleteBranchDialogInput, DeleteBranchDialogInputParser> {
    private final static Logger LOGGER = LoggerFactory.getLogger(DeleteBranchDialog.class);

    public DeleteBranchDialog(Git git,  List<ProjectEntry> projectEntries) {
        super(git, projectEntries);
    }

    @Override
    public void start(DeleteBranchDialogInput input) {
        DeleteBranchGitCommand deleteBranchGitCommand = new DeleteBranchGitCommand(getGit());

        String branchName = input.getName();

        printf("Удаляю ветку [%s]", branchName);

        for (ProjectEntry projectEntry : getProjectEntries()) {
            String projectName = projectEntry.getName();

            try {
                DeleteBranchGitCommandInput commandInput = new DeleteBranchGitCommandInput(branchName, projectEntry.getDirectory());
                deleteBranchGitCommand.execute(commandInput);
                printf("[%s] Ветка [%s] успешно удалена", projectName, branchName);
                LOGGER.info("[{}] Удаление ветки [{}]. Ветка успешно удалена. Команды - [{}]", projectName, branchName, getGit().getLastExecutedCommandsAsString());
            } catch (BranchNotFoundException e) {
                printf(warn("[%s] Ветка [%s] не существует"), projectName, branchName);
                LOGGER.warn("[{}] Удаление ветки [{}]. Ветка не существует. Команды - [{}]", projectName, branchName, getGit().getLastExecutedCommandsAsString());
            } catch (Exception e) {
                printf(error("[%s] Не удалось удалить ветку [%s]"), projectName, branchName);
                LOGGER.error("[{}] Удаление ветки [{}]. Не удалось удалить ветку. Причина ошибки - [{}]. Команды - [{}]", projectName, branchName, e.getMessage(), getGit().getLastExecutedCommandsAsString());
            }
        }

        printf("Удаление ветки [%s] завершено", branchName);
    }

    @Override
    public DeleteBranchDialogInputParser getInputParser() {
        return new DeleteBranchDialogInputParser();
    }
}
