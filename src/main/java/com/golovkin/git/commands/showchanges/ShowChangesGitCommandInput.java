package com.golovkin.git.commands.showchanges;

import com.golovkin.git.commands.GitCommandInput;

public class ShowChangesGitCommandInput implements GitCommandInput {
    private final String branchName;
    private final String projectDirectoryPath;

    public ShowChangesGitCommandInput(String branchName, String projectDirectoryPath) {
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
