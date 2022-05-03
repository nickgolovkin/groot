package com.golovkin.git.commands;

import java.nio.file.Path;

public class NewBranchCommandInput implements CommandInput {
    private final String newBranchName;
    private final String projectDirectoryPath;

    public NewBranchCommandInput(String newBranchName, String projectDirectoryPath) {
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
