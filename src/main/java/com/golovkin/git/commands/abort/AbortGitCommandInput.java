package com.golovkin.git.commands.abort;

import com.golovkin.git.commands.GitCommandInput;

public class AbortGitCommandInput implements GitCommandInput {
    private final String projectDirectoryPath;

    public AbortGitCommandInput(String projectDirectoryPath) {
        this.projectDirectoryPath = projectDirectoryPath;
    }

    public String getProjectDirectoryPath() {
        return projectDirectoryPath;
    }
}
