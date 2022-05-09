package com.golovkin.git.commands.commit;

import com.golovkin.git.commands.GitCommandOutput;

public class CommitGitCommandOutput implements GitCommandOutput {
    private final boolean cannotGetTaskNumberFromBranchName;
    private final String resolvedCommitMessage;

    public CommitGitCommandOutput(boolean cannotGetTaskNumberFromBranchName, String resolvedCommitMessage) {
        this.cannotGetTaskNumberFromBranchName = cannotGetTaskNumberFromBranchName;
        this.resolvedCommitMessage = resolvedCommitMessage;
    }

    public boolean isCannotGetTaskNumberFromBranchName() {
        return cannotGetTaskNumberFromBranchName;
    }

    public String getResolvedCommitMessage() {
        return resolvedCommitMessage;
    }
}
