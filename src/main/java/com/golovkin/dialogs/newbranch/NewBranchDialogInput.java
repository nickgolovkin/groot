package com.golovkin.dialogs.newbranch;

import com.golovkin.dialogs.Input;

public class NewBranchDialogInput implements Input {
    private String name;

    public NewBranchDialogInput(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
