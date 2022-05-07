package com.golovkin.git.commands.renamebranch;

import com.golovkin.git.Git;
import com.golovkin.git.commands.AbstractGitCommand;
import com.golovkin.git.commands.EmptyGitCommandOutput;

public class RenameBranchGitCommand extends AbstractGitCommand<RenameBranchGitCommandInput, EmptyGitCommandOutput> {
    public RenameBranchGitCommand(Git git) {
        super(git);
    }

    @Override
    protected EmptyGitCommandOutput performCommand(RenameBranchGitCommandInput commandInput) {
        String projectDirectoryPath = commandInput.getProjectDirectoryPath();
        String name = commandInput.getBranchName();

        getGit().renameBranch(projectDirectoryPath, name);

        return new EmptyGitCommandOutput();
    }
}
