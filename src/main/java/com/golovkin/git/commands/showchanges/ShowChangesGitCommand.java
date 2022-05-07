package com.golovkin.git.commands.showchanges;

import com.golovkin.RegexUtils;
import com.golovkin.git.Git;
import com.golovkin.git.commands.AbstractGitCommand;
import com.golovkin.git.commands.EmptyGitCommandOutput;
import com.golovkin.git.exceptions.BranchAlreadyExistsException;
import com.golovkin.git.exceptions.GitException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShowChangesGitCommand extends AbstractGitCommand<ShowChangesGitCommandInput, EmptyGitCommandOutput> {
    public ShowChangesGitCommand(Git git) {
        super(git);
    }

    @Override
    protected EmptyGitCommandOutput performCommand(ShowChangesGitCommandInput commandInput) {
        String projectDirectoryPath = commandInput.getProjectDirectoryPath();
        String name = commandInput.getBranchName();

        String reflog = getGit().reflog(projectDirectoryPath, name);

        String branchStartHash = RegexUtils.extractSubstring(reflog, "(?<hash>[A-z|0-9]+).+branch: Created from", "hash");
        if (branchStartHash == null) {
            throw new GitException("Не удалось найти начало ветки");
        }

        String checkpointHash = getGit().commit(projectDirectoryPath, "[GROOT] ~Show changes checkpoint~", true);

        try {
            getGit().softReset(projectDirectoryPath, branchStartHash);
        } catch (GitException e) {
            try {
                getGit().hardReset(projectDirectoryPath, checkpointHash);
                getGit().softReset(projectDirectoryPath, "HEAD~1");
            } catch (GitException anotherException) {
                // Игнорируем
            }

            throw e;
        }

        return new EmptyGitCommandOutput();
    }
}
