package com.golovkin.git;

import com.golovkin.RegexUtils;
import com.golovkin.git.exceptions.*;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Почему бы не передавать в Git параметр - набор проектов, сделать метод runAll(projectName -> gitExec(...))
 * Тогда не придется каждый раз дублировать projectDirectoryPath и в дальнейшем могут появиться команды просто run
 */
public class Git {
    public static final Pattern BRANCH_ALREADY_EXISTS_PATTERN = Pattern.compile("fatal: A branch named '(.+)' already exists\\.");
    public static final Pattern BRANCH_NOT_FOUND_PATTERN = Pattern.compile("error: branch '(.+)' not found\\.");
    public static final Pattern NO_MERGE_TO_ABORT_PATTERN = Pattern.compile("fatal: There is no merge to abort \\(MERGE_HEAD missing\\)\\.");
    public static final Pattern NO_CHERRY_PICK_TO_ABORT_PATTERN = Pattern.compile("error: no cherry-pick or revert in progress");
    private Path gitBackendPath;
    private GitExec gitExec;

    public Git(Path gitBackendPath) {
        this.gitBackendPath = gitBackendPath;
        this.gitExec = new GitExec(gitBackendPath);
    }

    public void createBranch(String projectDirectoryPath, String name) {
        try {
            gitExec.run(String.format("--git-dir %s/.git branch %s", escapePath(projectDirectoryPath), name));
        } catch (GitException e) {
            if (RegexUtils.contains(e.getMessage(), BRANCH_ALREADY_EXISTS_PATTERN)) {
                throw new BranchAlreadyExistsException(name);
            } else {
                throw e;
            }
        }
    }

    public void deleteBranch(String projectDirectoryPath, String name) {
        try {
            gitExec.run(String.format("--git-dir %s/.git branch -D %s", escapePath(projectDirectoryPath), name));
        } catch (GitException e) {
            if (RegexUtils.contains(e.getMessage(), BRANCH_NOT_FOUND_PATTERN)) {
                throw new BranchNotFoundException(name);
            } else {
                throw e;
            }
        }
    }

    public void renameBranch(String projectDirectoryPath, String name) {
        gitExec.run(String.format("--git-dir %s/.git branch -M %s", escapePath(projectDirectoryPath), name));
    }

    public void abortMerge(String projectDirectoryPath) {
        try {
            gitExec.run(String.format("--git-dir %s/.git merge --abort", escapePath(projectDirectoryPath)));
        } catch (GitException e) {
            if (RegexUtils.contains(e.getMessage(), NO_MERGE_TO_ABORT_PATTERN)) {
                throw new NoMergeToAbortException();
            } else {
                throw e;
            }
        }
    }

    public void abortCherryPick(String projectDirectoryPath) {
        try {
            gitExec.run(String.format("--git-dir %s/.git cherry-pick --abort", escapePath(projectDirectoryPath)));
        } catch (GitException e) {
            if (RegexUtils.contains(e.getMessage(), NO_CHERRY_PICK_TO_ABORT_PATTERN)) {
                throw new NoCherryPickToAbortException();
            } else {
                throw e;
            }
        }
    }

    public String status(String projectDirectoryPath) {
        gitExec.run(String.format("--git-dir %s/.git status", escapePath(projectDirectoryPath)));

        return String.join(" ", gitExec.getOutput());
    }

    public void checkout(String projectDirectoryPath, String name) {
        gitExec.run(String.format("--git-dir %s/.git checkout %s", escapePath(projectDirectoryPath), name));
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

    private String escapePath(String path) {
        return path.replace(" ", "\\ ");
    }
}
