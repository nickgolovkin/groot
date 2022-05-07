package com.golovkin.git.commands.resettocommit;

import com.golovkin.git.Git;
import com.golovkin.git.commands.AbstractGitCommand;
import com.golovkin.git.commands.EmptyGitCommandOutput;

public class ResetToCommitGitCommand extends AbstractGitCommand<ResetToCommitGitCommandInput, EmptyGitCommandOutput> {
    public ResetToCommitGitCommand(Git git) {
        super(git);
    }

    @Override
    protected EmptyGitCommandOutput performCommand(ResetToCommitGitCommandInput commandInput) {
        String projectDirectoryPath = commandInput.getProjectDirectoryPath();

        getGit().hardReset(projectDirectoryPath, "HEAD");

        return new EmptyGitCommandOutput();
    }
}
