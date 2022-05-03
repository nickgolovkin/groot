package com.golovkin;

import com.golovkin.git.Git;
import com.golovkin.git.exceptions.BranchAlreadyExistsException;

import java.util.List;

public class Branching {
    private final Git git;

    public Branching(Git git) {
        this.git = git;
    }

    /**
     * Команды будут основываться на уровне Git - т.е. самом низком уровне
     * Git представляет из себя лишь удобное API для запуска команд Git
     * Взаимодействие с пользователем происходит через диалоги.
     * При подаче на вход команды анализируется, какой диалог запустить, после чего диалог запускается
     * @param name
     */
    /**
     * Подумать может реализовать каждую команду отдельно как класс и тогда будет шаблонный метод, который обнуляет resetLastExecutedCommands
     * @param name
     */
    public void newBranch(String projectDirectoryPath, String name) {
        git.resetLastExecutedCommands();
        try {
            git.branch(projectDirectoryPath, name);
            git.checkout(projectDirectoryPath, name);
        } catch (BranchAlreadyExistsException e) {
            git.checkout(projectDirectoryPath, name);
            throw e;
        }
    }

    public String getLastExecutedCommandsAsString() {
        return git.getLastExecutedCommandsAsString();
    }
}
