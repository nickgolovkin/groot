package com.golovkin.git.commands;

import com.golovkin.git.Git;

public abstract class AbstractGitCommand<T extends CommandInput> {
    private final Git git;

    public AbstractGitCommand(Git git) {
        this.git = git;
    }

    public final void execute(T commandInput) {
        git.resetLastExecutedCommands();
        performCommand(commandInput);
    }

    protected Git getGit() {
        return git;
    }

    protected abstract void performCommand(T commandInput);
}
