package com.golovkin.git.commands.deletebranch;

import com.golovkin.git.Git;
import com.golovkin.git.commands.AbstractGitCommand;

public class DeleteBranchGitCommand extends AbstractGitCommand<DeleteBranchGitCommandInput> {
    public DeleteBranchGitCommand(Git git) {
        super(git);
    }

    @Override
    protected void performCommand(DeleteBranchGitCommandInput commandInput) {
        String projectDirectoryPath = commandInput.getProjectDirectoryPath();
        String name = commandInput.getBranchName();

        getGit().deleteBranch(projectDirectoryPath, name);
    }
}
