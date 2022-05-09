package com.golovkin.git.commands.currentbranches;

import com.golovkin.git.Git;
import com.golovkin.git.commands.AbstractGitCommand;
import com.golovkin.git.commands.EmptyGitCommandOutput;

public class CurrentBranchesGitCommand extends AbstractGitCommand<CurrentBranchesGitCommandInput, CurrentBranchesGitCommandOutput> {
    public CurrentBranchesGitCommand(Git git) {
        super(git);
    }

    @Override
    protected CurrentBranchesGitCommandOutput performCommand(CurrentBranchesGitCommandInput commandInput) {
        String projectDirectoryPath = commandInput.getProjectDirectoryPath();

        String currentBranchName = getGit().getCurrentBranch(projectDirectoryPath);

        return new CurrentBranchesGitCommandOutput(currentBranchName);
    }
}
