package com.golovkin.git.commands.commit;

import com.golovkin.git.commands.GitCommandInput;

import java.util.regex.Pattern;

public class CommitGitCommandInput implements GitCommandInput {
    private final String commitMessage;
    private final String branchName;
    private final Pattern branchNamePattern;
    private final String taskNumberPattern;
    private final String projectDirectoryPath;

    public CommitGitCommandInput(String commitMessage, String branchName, Pattern branchNamePattern, String taskNumberPattern, String projectDirectoryPath) {
        this.commitMessage = commitMessage;
        this.branchName = branchName;
        this.branchNamePattern = branchNamePattern;
        this.taskNumberPattern = taskNumberPattern;
        this.projectDirectoryPath = projectDirectoryPath;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public String getBranchName() {
        return branchName;
    }

    public Pattern getBranchNamePattern() {
        return branchNamePattern;
    }

    public String getTaskNumberPattern() {
        return taskNumberPattern;
    }

    public String getProjectDirectoryPath() {
        return projectDirectoryPath;
    }
}
