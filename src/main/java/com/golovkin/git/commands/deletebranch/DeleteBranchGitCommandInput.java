package com.golovkin.git.commands.deletebranch;

import com.golovkin.git.commands.GitCommandInput;

public class DeleteBranchGitCommandInput implements GitCommandInput {
    private final String branchName;
    private final String projectDirectoryPath;

    public DeleteBranchGitCommandInput(String branchName, String projectDirectoryPath) {
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
