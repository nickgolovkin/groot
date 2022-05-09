package com.golovkin.git.commands.currentbranches;

import com.golovkin.git.commands.GitCommandInput;

public class CurrentBranchesGitCommandInput implements GitCommandInput {
    private final String projectDirectoryPath;

    public CurrentBranchesGitCommandInput(String projectDirectoryPath) {
        this.projectDirectoryPath = projectDirectoryPath;
    }

    public String getProjectDirectoryPath() {
        return projectDirectoryPath;
    }
}
