package com.golovkin.git.commands.deletebranch;

import com.golovkin.git.Git;
import com.golovkin.git.commands.AbstractGitCommand;
import com.golovkin.git.commands.EmptyGitCommandOutput;

public class DeleteBranchGitCommand extends AbstractGitCommand<DeleteBranchGitCommandInput, EmptyGitCommandOutput> {
    public DeleteBranchGitCommand(Git git) {
        super(git);
    }

    @Override
    protected EmptyGitCommandOutput performCommand(DeleteBranchGitCommandInput commandInput) {
        String projectDirectoryPath = commandInput.getProjectDirectoryPath();
        String name = commandInput.getBranchName();

        getGit().deleteBranch(projectDirectoryPath, name);

        return new EmptyGitCommandOutput();
    }
}
