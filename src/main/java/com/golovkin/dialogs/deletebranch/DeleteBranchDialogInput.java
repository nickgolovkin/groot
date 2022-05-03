package com.golovkin.dialogs.deletebranch;

import com.golovkin.dialogs.DialogInput;

public class DeleteBranchDialogInput implements DialogInput {
    private String name;

    public DeleteBranchDialogInput(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
