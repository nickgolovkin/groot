package com.golovkin.git.commands.renamebranch;

import com.golovkin.git.commands.GitCommandInput;

public class RenameBranchGitCommandInput implements GitCommandInput {
    private final String branchName;
    private final String projectDirectoryPath;

    public RenameBranchGitCommandInput(String branchName, String projectDirectoryPath) {
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
