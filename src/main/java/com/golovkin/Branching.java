package com.golovkin;

import com.golovkin.git.Git;

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
    public void newBranch(String name) {
//        git.checkout();
    }
}
