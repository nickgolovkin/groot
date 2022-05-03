package com.golovkin.dialogs.renamebranch;

import com.golovkin.dialogs.DialogInput;

public class RenameBranchDialogInput implements DialogInput {
    private String name;

    public RenameBranchDialogInput(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
