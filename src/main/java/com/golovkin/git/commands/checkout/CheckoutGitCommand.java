package com.golovkin.git.commands.checkout;

import com.golovkin.git.Git;
import com.golovkin.git.commands.AbstractGitCommand;
import com.golovkin.git.exceptions.NothingToCommitException;

import java.util.List;

public class CheckoutGitCommand extends AbstractGitCommand<CheckoutGitCommandInput, CheckoutGitCommandOutput> {

    public static final String CHECKPOINT_COMMIT_MESSAGE = "[GROOT] ~Checkpoint~";
    public static final String LOG_FORMAT = "%H %B";
    public static final String PREVIOUS_COMMIT_REF_NAME = "HEAD~1";

    public CheckoutGitCommand(Git git) {
        super(git);
    }

    @Override
    protected CheckoutGitCommandOutput performCommand(CheckoutGitCommandInput commandInput) {
        String projectDirectoryPath = commandInput.getProjectDirectoryPath();
        String name = commandInput.getBranchName();

        boolean isNothingToCommit = false;
        // TODO посмотреть на токенизацию и как она здесь отработает в GitExec
        // Попробовать потом как-нибудь реализовать null-безопасность на аннотациях
        getGit().verifyRefExists(projectDirectoryPath, name);

        try {
            getGit().commit(projectDirectoryPath, CHECKPOINT_COMMIT_MESSAGE, false);
        } catch (NothingToCommitException e) {
            isNothingToCommit = true;
        }

        getGit().checkout(projectDirectoryPath, name);
        List<String> logs = getGit().log(projectDirectoryPath, 1, LOG_FORMAT);

        if (isLastCommitCheckpoint(logs)) {
            getGit().softReset(projectDirectoryPath, PREVIOUS_COMMIT_REF_NAME);
        }

        return new CheckoutGitCommandOutput(isNothingToCommit);
    }

    private boolean isLastCommitCheckpoint(List<String> logs) {
        return logs.stream().anyMatch(x -> x.contains(CHECKPOINT_COMMIT_MESSAGE));
    }
}
