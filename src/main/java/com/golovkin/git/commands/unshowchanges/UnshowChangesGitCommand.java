package com.golovkin.git.commands.unshowchanges;

import com.golovkin.RegexUtils;
import com.golovkin.git.Git;
import com.golovkin.git.commands.AbstractGitCommand;
import com.golovkin.git.commands.EmptyGitCommandOutput;
import com.golovkin.git.exceptions.GitException;

import java.util.List;
import java.util.regex.Pattern;

public class UnshowChangesGitCommand extends AbstractGitCommand<UnshowChangesGitCommandInput, EmptyGitCommandOutput> {

    public static final Pattern CHECKPOINT_COMMIT_PATTERN = Pattern.compile("(?<hash>[A-z|0-9]+).+commit: \\[GROOT\\] ~Show changes checkpoint~");
    public static final int EXPECTED_CHECKPOINT_COMMIT_INDEX_IN_REFLOG = 1;

    public UnshowChangesGitCommand(Git git) {
        super(git);
    }

    @Override
    protected EmptyGitCommandOutput performCommand(UnshowChangesGitCommandInput commandInput) {
        String projectDirectoryPath = commandInput.getProjectDirectoryPath();
        String name = commandInput.getBranchName();

        List<String> reflog = getGit().reflog(projectDirectoryPath, name);

        if (reflog.size() < EXPECTED_CHECKPOINT_COMMIT_INDEX_IN_REFLOG + 1) {
            throw new GitException("Не удалось найти контрольную точку");
        }

        // TODO попробовать вместо null использовать Optional?
        String checkpointHash = RegexUtils.extractSubstring(reflog.get(EXPECTED_CHECKPOINT_COMMIT_INDEX_IN_REFLOG), CHECKPOINT_COMMIT_PATTERN, "hash");
        if (checkpointHash == null) {
            throw new GitException("Не удалось найти контрольную точку");
        }

        getGit().hardReset(projectDirectoryPath, checkpointHash);
        getGit().softReset(projectDirectoryPath, "HEAD~1");

        return new EmptyGitCommandOutput();
    }
}
