package com.golovkin.git.commands.resettocommit;

import com.golovkin.git.commands.GitCommandInput;

public class ResetToCommitGitCommandInput implements GitCommandInput {
    private final String projectDirectoryPath;

    public ResetToCommitGitCommandInput(String projectDirectoryPath) {
        this.projectDirectoryPath = projectDirectoryPath;
    }

    public String getProjectDirectoryPath() {
        return projectDirectoryPath;
    }
}
