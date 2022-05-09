package com.golovkin.git.commands.currentbranches;

import com.golovkin.git.commands.GitCommandOutput;

public class CurrentBranchesGitCommandOutput implements GitCommandOutput {
    private String currentBranchName;

    public CurrentBranchesGitCommandOutput(String currentBranchName) {
        this.currentBranchName = currentBranchName;
    }

    public String getCurrentBranchName() {
        return currentBranchName;
    }
}
