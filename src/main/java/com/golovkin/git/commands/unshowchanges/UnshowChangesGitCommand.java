package com.golovkin.git.commands.unshowchanges;

import com.golovkin.RegexUtils;
import com.golovkin.git.Git;
import com.golovkin.git.commands.AbstractGitCommand;
import com.golovkin.git.commands.EmptyGitCommandOutput;
import com.golovkin.git.commands.showchanges.ShowChangesUtils;
import com.golovkin.git.exceptions.GitException;
import com.golovkin.git.exceptions.NoActiveShowChangesException;

import java.util.List;
import java.util.regex.Pattern;

public class UnshowChangesGitCommand extends AbstractGitCommand<UnshowChangesGitCommandInput, EmptyGitCommandOutput> {
    public UnshowChangesGitCommand(Git git) {
        super(git);
    }

    @Override
    protected EmptyGitCommandOutput performCommand(UnshowChangesGitCommandInput commandInput) {
        String projectDirectoryPath = commandInput.getProjectDirectoryPath();
        String name = commandInput.getBranchName();

        List<String> reflog = getGit().reflog(projectDirectoryPath, name);

        // TODO попробовать вместо null использовать Optional?
        String checkpointHash = ShowChangesUtils.getCheckpointHash(reflog);
        if (checkpointHash == null) {
            throw new NoActiveShowChangesException();
        }

        getGit().hardReset(projectDirectoryPath, checkpointHash);
        getGit().softReset(projectDirectoryPath, "HEAD~1");

        return new EmptyGitCommandOutput();
    }
}
