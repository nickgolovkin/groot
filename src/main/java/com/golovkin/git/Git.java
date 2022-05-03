package com.golovkin.git;

import com.golovkin.git.exceptions.BranchAlreadyExistsException;
import com.golovkin.git.exceptions.BranchNotFoundException;
import com.golovkin.git.exceptions.GitException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Git {
    public static final String BRANCH_ALREADY_EXISTS_PATTERN = "fatal: A branch named '(.+)' already exists\\.";
    public static final String BRANCH_NOT_FOUND_PATTERN = "error: branch '(.+)' not found\\.";
    private Path gitBackendPath;
    private GitExec gitExec;

    public Git(Path gitBackendPath) {
        this.gitBackendPath = gitBackendPath;
        this.gitExec = new GitExec(gitBackendPath);
    }

    public void createBranch(String projectDirectoryPath, String name) {
        gitExec.run(String.format("--git-dir %s/.git branch %s", projectDirectoryPath, name));

        if (gitExec.getExitCode() != 0) {
            List<String> output = gitExec.getOutput();
            String firstOutputLine = output.get(0);

            if (firstOutputLine != null && firstOutputLine.matches(BRANCH_ALREADY_EXISTS_PATTERN)) {
                throw new BranchAlreadyExistsException(name);
            } else {
                throw new GitException(output);
            }
        }
    }

    public void deleteBranch(String projectDirectoryPath, String name) {
        gitExec.run(String.format("--git-dir %s/.git branch -D %s", projectDirectoryPath, name));

        if (gitExec.getExitCode() != 0) {
            List<String> output = gitExec.getOutput();
            String firstOutputLine = output.get(0);

            if (firstOutputLine != null && firstOutputLine.matches(BRANCH_NOT_FOUND_PATTERN)) {
                throw new BranchNotFoundException(name);
            } else {
                throw new GitException(output);
            }
        }
    }

    public void renameBranch(String projectDirectoryPath, String name) {
        gitExec.run(String.format("--git-dir %s/.git branch -M %s", projectDirectoryPath, name));

        if (gitExec.getExitCode() != 0) {
            List<String> output = gitExec.getOutput();

            throw new GitException(output);
        }
    }

    public String status(String projectDirectoryPath) {
        gitExec.run(String.format("--git-dir %s/.git status", projectDirectoryPath));

        List<String> output = gitExec.getOutput();
        if (gitExec.getExitCode() != 0) {
            throw new GitException(output);
        }

        return String.join(" ", output);
    }

    public void checkout(String projectDirectoryPath, String name) {
        gitExec.run(String.format("--git-dir %s/.git checkout %s", projectDirectoryPath, name));

        if (gitExec.getExitCode() != 0) {
            List<String> output = gitExec.getOutput();
            throw new GitException(output);
        }
    }

    public List<String> getLastExecutedCommands() {
        return gitExec.getLastExecutedCommands();
    }

    public String getLastExecutedCommandsAsString() {
        return String.join(";", getLastExecutedCommands());
    }

    public void resetLastExecutedCommands() {
        gitExec.resetLastExecutedCommands();
    }
}
