package com.golovkin.git.commands.newbranch;

import com.golovkin.git.Git;
import com.golovkin.git.commands.AbstractGitCommand;
import com.golovkin.git.commands.EmptyGitCommandOutput;
import com.golovkin.git.exceptions.BranchAlreadyExistsException;

public class NewBranchGitCommand extends AbstractGitCommand<NewBranchGitCommandInput, EmptyGitCommandOutput> {
    public NewBranchGitCommand(Git git) {
        super(git);
    }

    @Override
    protected EmptyGitCommandOutput performCommand(NewBranchGitCommandInput commandInput) {
        String projectDirectoryPath = commandInput.getProjectDirectoryPath();
        String name = commandInput.getBranchName();

        try {
            getGit().createBranch(projectDirectoryPath, name);
            getGit().checkout(projectDirectoryPath, name);
        } catch (BranchAlreadyExistsException e) {
            getGit().checkout(projectDirectoryPath, name);
            throw e;
        }

        return new EmptyGitCommandOutput();
    }
}
