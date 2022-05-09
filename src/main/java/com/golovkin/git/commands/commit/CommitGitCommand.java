package com.golovkin.git.commands.commit;

import com.golovkin.RegexUtils;
import com.golovkin.git.Git;
import com.golovkin.git.commands.AbstractGitCommand;
import com.golovkin.git.exceptions.TaskNumberFromBranchNameDoesNotMatchTaskNumberFromCommitMessageException;

import java.util.regex.Pattern;

public class CommitGitCommand extends AbstractGitCommand<CommitGitCommandInput, CommitGitCommandOutput> {
    public CommitGitCommand(Git git) {
        super(git);
    }

    @Override
    protected CommitGitCommandOutput performCommand(CommitGitCommandInput commandInput) {
        String taskNumberFromBranchName = getTaskNumberFromBranchName(commandInput.getBranchName(), commandInput.getBranchNamePattern());
        boolean cannotGetTaskNumberFromBranchName = false;
        String resolvedCommitMessage = commandInput.getCommitMessage();

        if (taskNumberFromBranchName == null) {
            cannotGetTaskNumberFromBranchName = true;
        } else {
            String taskNumberFromCommitMessage = getTaskNumberFromCommitMessage(commandInput.getCommitMessage(), commandInput.getTaskNumberPattern());

            if (taskNumberFromCommitMessage == null) {
                resolvedCommitMessage = getCommitMessageWithTaskNumber(commandInput.getCommitMessage(), taskNumberFromBranchName);
            } else {
                if (!taskNumberFromBranchName.equals(taskNumberFromCommitMessage)) {
                    throw new TaskNumberFromBranchNameDoesNotMatchTaskNumberFromCommitMessageException(taskNumberFromBranchName, taskNumberFromCommitMessage);
                }
            }
        }

        getGit().commit(commandInput.getProjectDirectoryPath(), resolvedCommitMessage, false);

        return new CommitGitCommandOutput(cannotGetTaskNumberFromBranchName, resolvedCommitMessage);
    }

    private String getTaskNumberFromBranchName(String branchName, Pattern branchNamePattern) {
        return RegexUtils.extractSubstring(branchName, branchNamePattern, "taskNumber");
    }

    private String getCommitMessageWithTaskNumber(String commitMessage, String taskNumberFromBranchName) {
        return String.format("%s %s", taskNumberFromBranchName, commitMessage);
    }

    private String getTaskNumberFromCommitMessage(String commitMessage, String taskNumberPattern) {
        Pattern pattern = Pattern.compile("^" + taskNumberPattern);
        return RegexUtils.extractSubstring(commitMessage, pattern, "taskNumber");
    }
}
