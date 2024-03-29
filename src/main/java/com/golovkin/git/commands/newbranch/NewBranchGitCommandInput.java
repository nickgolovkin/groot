package com.golovkin.git.commands.newbranch;

import com.golovkin.git.commands.GitCommandInput;

public class NewBranchGitCommandInput implements GitCommandInput {
    private final String branchName;
    private final String projectDirectoryPath;

    public NewBranchGitCommandInput(String branchName, String projectDirectoryPath) {
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
