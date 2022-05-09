package com.golovkin.dialogs.commit;

import com.golovkin.dialogs.DialogInput;

public class CommitDialogInput implements DialogInput {
    private String commitMessage;

    public CommitDialogInput(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public String getCommitMessage() {
        return commitMessage;
    }
}
