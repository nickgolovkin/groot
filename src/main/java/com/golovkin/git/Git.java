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
    public static final Pattern REF_NOT_EXISTS_PATTERN = Pattern.compile("fatal: Needed a single revision");
    public static final Pattern NOTHING_TO_COMMIT_PATTERN = Pattern.compile("nothing (added ){0,1}to commit");
    public static final Pattern COMMIT_HASH_PATTERN = Pattern.compile("\\[.+? (?<hash>[A-z|0-9]+)\\]");
    private Path gitBackendPath;
    private GitExec gitExec;

    public Git(Path gitBackendPath) {
        this.gitBackendPath = gitBackendPath;
        this.gitExec = new GitExec(gitBackendPath);
    }

    public void createBranch(String projectDirectoryPath, String name) {
        try {
            gitExec.run(String.format("-C \"%s\" branch %s", projectDirectoryPath, name));
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
            gitExec.run(String.format("-C \"%s\" branch -D %s", projectDirectoryPath, name));
        } catch (GitException e) {
            if (RegexUtils.contains(e.getMessage(), BRANCH_NOT_FOUND_PATTERN)) {
                throw new BranchNotFoundException(name);
            } else {
                throw e;
            }
        }
    }

    public void renameBranch(String projectDirectoryPath, String name) {
        gitExec.run(String.format("-C \"%s\" branch -M %s", projectDirectoryPath, name));
    }

    public void abortMerge(String projectDirectoryPath) {
        try {
            gitExec.run(String.format("-C \"%s\" merge --abort", projectDirectoryPath));
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
            gitExec.run(String.format("-C \"%s\" cherry-pick --abort", projectDirectoryPath));
        } catch (GitException e) {
            if (RegexUtils.contains(e.getMessage(), NO_CHERRY_PICK_TO_ABORT_PATTERN)) {
                throw new NoCherryPickToAbortException();
            } else {
                throw e;
            }
        }
    }

    public String status(String projectDirectoryPath) {
        gitExec.run(String.format("-C \"%s\" status", projectDirectoryPath));

        return String.join(" ", gitExec.getOutput());
    }

    public void checkout(String projectDirectoryPath, String name) {
        gitExec.run(String.format("-C \"%s\" checkout %s", projectDirectoryPath, name));
    }

    public void verifyRefExists(String projectDirectoryPath, String name) {
        try {
            gitExec.run(String.format("-C \"%s\" rev-parse --verify %s", projectDirectoryPath, name));
        } catch (GitException e) {
            if (RegexUtils.contains(e.getMessage(), REF_NOT_EXISTS_PATTERN)) {
                throw new RefNotExistsException(name);
            } else {
                throw e;
            }
        }
    }

    public String commit(String projectDirectoryPath, String message, boolean allowEmpty) {
        try {
            if (allowEmpty) {
                gitExec.run(String.format("-C \"%s\" commit --allow-empty -a -m \"%s\"", projectDirectoryPath, message));
            } else {
                gitExec.run(String.format("-C \"%s\" commit -a -m \"%s\"", projectDirectoryPath, message));
            }

            String output = String.join(" ", gitExec.getOutput());

            return RegexUtils.extractSubstring(output, COMMIT_HASH_PATTERN, "hash");
        } catch (GitException e) {
            if (RegexUtils.contains(e.getMessage(), NOTHING_TO_COMMIT_PATTERN)) {
                throw new NothingToCommitException();
            } else {
                throw e;
            }
        }
    }

    public List<String> log(String projectDirectoryPath, int entryCount, String prettyFormat) {
        gitExec.run(String.format("-C \"%s\" log -%d \"--pretty=%s\"", projectDirectoryPath, entryCount, prettyFormat));
        return gitExec.getOutput();
    }

    public void softReset(String projectDirectoryPath, String refName) {
        gitExec.run(String.format("-C \"%s\" reset --soft %s", projectDirectoryPath, refName));
    }

    public void hardReset(String projectDirectoryPath, String refName) {
        gitExec.run(String.format("-C \"%s\" reset --hard %s", projectDirectoryPath, refName));
    }

    public List<String> reflog(String projectDirectoryPath, String branchName) {
        gitExec.run(String.format("-C \"%s\" --no-pager reflog show --no-abbrev %s", projectDirectoryPath, branchName));
        return gitExec.getOutput();
    }

    public String getCurrentBranch(String projectDirectoryPath) {
        gitExec.run(String.format("-C \"%s\" rev-parse --abbrev-ref HEAD", projectDirectoryPath));
        return String.join(" ", gitExec.getOutput());
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
