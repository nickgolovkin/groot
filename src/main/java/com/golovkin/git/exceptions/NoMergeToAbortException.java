package com.golovkin.git.exceptions;

public class NoMergeToAbortException extends GitException {
    public NoMergeToAbortException() {
        super("Сейчас не происходит мерж");
    }
}
