package com.golovkin.git.commands;

import com.golovkin.git.Git;
import com.golovkin.git.exceptions.BranchAlreadyExistsException;

public class NewBranchGitCommand extends AbstractGitCommand<NewBranchCommandInput> {
    public NewBranchGitCommand(Git git) {
        super(git);
    }

    @Override
    protected void performCommand(NewBranchCommandInput commandInput) {
        String projectDirectoryPath = commandInput.getProjectDirectoryPath();
        String name = commandInput.getNewBranchName();

        try {
            getGit().branch(projectDirectoryPath, name);
            getGit().checkout(projectDirectoryPath, name);
        } catch (BranchAlreadyExistsException e) {
            getGit().checkout(projectDirectoryPath, name);
            throw e;
        }
    }
}
