package com.golovkin.git.commands.newbranch;

import com.golovkin.git.commands.GitCommandInput;

public class NewBranchGitCommandInput implements GitCommandInput {
    private final String newBranchName;
    private final String projectDirectoryPath;

    public NewBranchGitCommandInput(String newBranchName, String projectDirectoryPath) {
        this.newBranchName = newBranchName;
        this.projectDirectoryPath = projectDirectoryPath;
    }

    public String getNewBranchName() {
        return newBranchName;
    }

    public String getProjectDirectoryPath() {
        return projectDirectoryPath;
    }
}
