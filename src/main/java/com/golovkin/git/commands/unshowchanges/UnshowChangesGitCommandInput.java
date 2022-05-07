package com.golovkin.git.commands.unshowchanges;

import com.golovkin.git.commands.GitCommandInput;

public class UnshowChangesGitCommandInput implements GitCommandInput {
    private final String branchName;
    private final String projectDirectoryPath;

    public UnshowChangesGitCommandInput(String branchName, String projectDirectoryPath) {
        this.branchName = branchName;
        this.projectDirectoryPath = projectDirectoryPath;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getProjectDirectoryPath() {
        return projectDirectoryPath;
    }
}
