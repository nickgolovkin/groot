package com.golovkin.git;

import com.golovkin.git.exceptions.BranchAlreadyExistsException;
import com.golovkin.git.exceptions.GitException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Git {
    public static final String BRANCH_ALREADY_EXISTS_PATTERN = "fatal: A branch named '(.+)' already exists\\.";
    private Path gitBackendPath;
    private GitExec gitExec;

    public Git(Path gitBackendPath) {
        this.gitBackendPath = gitBackendPath;
        this.gitExec = new GitExec(gitBackendPath);
    }

    public void branch(String projectDirectoryPath, String name) {
        gitExec.run(String.format("--git-dir \"%s/.git\" branch %s", projectDirectoryPath, name));

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

    public void checkout(String projectDirectoryPath, String name) {
        gitExec.run(String.format("--git-dir \"%s/.git\" checkout %s", projectDirectoryPath, name));

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
