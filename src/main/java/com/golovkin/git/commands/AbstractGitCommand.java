package com.golovkin.git.commands;

import com.golovkin.git.Git;

public abstract class AbstractGitCommand<T extends GitCommandInput, E extends GitCommandOutput> {
    private final Git git;

    /**
     * Команды будут основываться на уровне Git - т.е. самом низком уровне
     * Git представляет из себя лишь удобное API для запуска команд Git
     * Взаимодействие с пользователем происходит через диалоги.
     * При подаче на вход команды анализируется, какой диалог запустить, после чего диалог запускается
     */
    public AbstractGitCommand(Git git) {
        this.git = git;
    }

    public final E execute(T commandInput) {
        git.resetLastExecutedCommands();
        return performCommand(commandInput);
    }

    protected Git getGit() {
        return git;
    }

    protected abstract E performCommand(T commandInput);
}
