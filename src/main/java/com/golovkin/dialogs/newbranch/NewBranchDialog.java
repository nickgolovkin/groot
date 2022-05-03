package com.golovkin.dialogs.newbranch;

import com.golovkin.Branching;
import com.golovkin.config.ProjectEntry;
import com.golovkin.dialogs.AbstractDialog;

import java.util.List;

public class NewBranchDialog extends AbstractDialog<NewBranchDialogInput, NewBranchDialogInputParser> {
    private final Branching branching;

    public NewBranchDialog(Branching branching, List<ProjectEntry> projectEntries) {
        super(projectEntries);
        this.branching = branching;
    }

    @Override
    public void start(NewBranchDialogInput input) {
        String newBranchName = input.getName();

        System.out.printf("Создаю новую ветку [%s]\n", newBranchName);

        for (ProjectEntry projectEntry : getProjectEntries()) {
            try {
                branching.newBranch(newBranchName);
                System.out.printf("[%s] Ветка [%s] успешно создана\n", projectEntry.getName(), newBranchName);
            } catch (Exception e) {
                System.err.printf("[%s] Не удалось создать ветку [%s]\n", projectEntry.getName(), newBranchName);
            }
        }

        System.out.printf("Создание ветки [%s] завершено\n", newBranchName);
    }

    @Override
    public NewBranchDialogInputParser getInputParser() {
        return new NewBranchDialogInputParser();
    }
}
