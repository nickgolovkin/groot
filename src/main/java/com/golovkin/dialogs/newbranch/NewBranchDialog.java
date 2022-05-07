package com.golovkin.dialogs.newbranch;

import com.golovkin.config.ProjectEntry;
import com.golovkin.dialogs.AbstractDialog;
import com.golovkin.git.Git;
import com.golovkin.git.commands.newbranch.NewBranchGitCommandInput;
import com.golovkin.git.commands.newbranch.NewBranchGitCommand;
import com.golovkin.git.exceptions.BranchAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.golovkin.common.ColorUtils.error;
import static com.golovkin.common.ColorUtils.warn;
import static com.golovkin.common.PrintUtils.printf;

public class NewBranchDialog extends AbstractDialog<NewBranchDialogInput, NewBranchDialogInputParser> {
    private final static Logger LOGGER = LoggerFactory.getLogger(NewBranchDialog.class);

    public NewBranchDialog(Git git,  List<ProjectEntry> projectEntries) {
        super(git, projectEntries);
    }

    @Override
    public void start(NewBranchDialogInput input) {
        NewBranchGitCommand newBranchGitCommand = new NewBranchGitCommand(getGit());

        String newBranchName = input.getName();

        printf("Создаю ветку [%s]", newBranchName);

        for (ProjectEntry projectEntry : getProjectEntries()) {
            String projectName = projectEntry.getName();

            try {
                NewBranchGitCommandInput commandInput = new NewBranchGitCommandInput(newBranchName, projectEntry.getDirectory());
                newBranchGitCommand.execute(commandInput);
                printf("[%s] Ветка [%s] успешно создана", projectName, newBranchName);
                LOGGER.info("[{}] Создание ветки [{}]. Ветка успешно создана. Команды - [{}]", projectName, newBranchName, getGit().getLastExecutedCommandsAsString());
            } catch (BranchAlreadyExistsException e) {
                printf(warn("[%s] Ветка [%s] уже существует"), projectName, newBranchName);
                LOGGER.warn("[{}] Создание ветки [{}]. Ветка уже существует. Команды - [{}]", projectName, newBranchName, getGit().getLastExecutedCommandsAsString());
            } catch (Exception e) {
                printf(error("[%s] Не удалось создать ветку [%s]"), projectName, newBranchName);
                LOGGER.error("[{}] Создание ветки [{}]. Не удалось создать ветку. Причина ошибки - [{}]. Команды - [{}]", projectName, newBranchName, e.getMessage(), getGit().getLastExecutedCommandsAsString());
            }
        }

        printf("Создание ветки [%s] завершено", newBranchName);
    }

    @Override
    public NewBranchDialogInputParser getInputParser() {
        return new NewBranchDialogInputParser();
    }
}
