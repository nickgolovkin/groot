package com.golovkin.git.commands.abort;

import com.golovkin.git.Git;
import com.golovkin.git.commands.AbstractGitCommand;
import com.golovkin.git.exceptions.GitException;
import com.golovkin.git.exceptions.NoMergeToAbortException;

public class AbortGitCommand extends AbstractGitCommand<AbortGitCommandInput> {
    public AbortGitCommand(Git git) {
        super(git);
    }

    @Override
    protected void performCommand(AbortGitCommandInput commandInput) {
        try {
            getGit().abortMerge(commandInput.getProjectDirectoryPath());
        } catch (NoMergeToAbortException e) {
            getGit().abortCherryPick(commandInput.getProjectDirectoryPath());
        }
    }
}
