package com.golovkin.dialogs.newbranch;

import com.golovkin.Branching;
import com.golovkin.config.ProjectEntry;
import com.golovkin.dialogs.AbstractDialog;
import com.golovkin.git.exceptions.BranchAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NewBranchDialog extends AbstractDialog<NewBranchDialogInput, NewBranchDialogInputParser> {
    private final static Logger LOGGER = LoggerFactory.getLogger(NewBranchDialog.class);

    private final Branching branching;

    public NewBranchDialog(Branching branching, List<ProjectEntry> projectEntries) {
        super(projectEntries);
        this.branching = branching;
    }

    @Override
    public void start(NewBranchDialogInput input) {
        String newBranchName = input.getName();

        System.out.printf("Создаю ветку [%s]\n", newBranchName);

        for (ProjectEntry projectEntry : getProjectEntries()) {
            String projectName = projectEntry.getName();

            try {
                branching.newBranch(projectEntry.getDirectory(), newBranchName);
                System.out.printf("[%s] Ветка [%s] успешно создана\n", projectName, newBranchName);
                LOGGER.info("[{}] Создание ветки [{}]. Ветка успешно создана. Команды - [{}]", projectName, newBranchName, branching.getLastExecutedCommandsAsString());
            } catch (BranchAlreadyExistsException e) {
                System.out.printf("[%s] Ветка [%s] уже существует\n", projectName, newBranchName);
                LOGGER.warn("[{}] Создание ветки [{}]. Ветка уже существует. Команды - [{}]", projectName, newBranchName, branching.getLastExecutedCommandsAsString());
            } catch (Exception e) {
                System.out.printf("[%s] Не удалось создать ветку [%s]\n", projectName, newBranchName);
                LOGGER.error("[{}] Создание ветки [{}]. Не удалось создать ветку. Причина ошибки - [{}]. Команды - [{}]", projectName, newBranchName, e.getMessage(), branching.getLastExecutedCommandsAsString());
            }
        }

        System.out.printf("Создание ветки [%s] завершено\n", newBranchName);
    }

    @Override
    public NewBranchDialogInputParser getInputParser() {
        return new NewBranchDialogInputParser();
    }
}
