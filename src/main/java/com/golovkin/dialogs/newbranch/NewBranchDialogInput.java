package com.golovkin.dialogs.newbranch;

import com.golovkin.dialogs.DialogInput;

public class NewBranchDialogInput implements DialogInput {
    private String name;

    public NewBranchDialogInput(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
