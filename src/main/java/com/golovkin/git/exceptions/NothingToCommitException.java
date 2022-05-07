package com.golovkin.git.exceptions;

public class NothingToCommitException extends GitException {
    public NothingToCommitException() {
        super("Нечего сохранять в коммит");
    }
}
