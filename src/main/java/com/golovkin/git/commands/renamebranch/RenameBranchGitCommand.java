package com.golovkin.git.commands.renamebranch;

import com.golovkin.git.Git;
import com.golovkin.git.commands.AbstractGitCommand;

public class RenameBranchGitCommand extends AbstractGitCommand<RenameBranchGitCommandInput> {
    public RenameBranchGitCommand(Git git) {
        super(git);
    }

    @Override
    protected void performCommand(RenameBranchGitCommandInput commandInput) {
        String projectDirectoryPath = commandInput.getProjectDirectoryPath();
        String name = commandInput.getBranchName();

        getGit().renameBranch(projectDirectoryPath, name);
    }
}
