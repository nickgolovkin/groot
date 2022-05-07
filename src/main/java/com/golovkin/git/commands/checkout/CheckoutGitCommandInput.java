package com.golovkin.git.commands.checkout;

import com.golovkin.git.commands.GitCommandInput;

public class CheckoutGitCommandInput implements GitCommandInput {
    private final String branchName;
    private final String projectDirectoryPath;

    public CheckoutGitCommandInput(String branchName, String projectDirectoryPath) {
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
