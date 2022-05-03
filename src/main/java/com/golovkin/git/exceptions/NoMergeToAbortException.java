package com.golovkin.git.exceptions;

public class NoMergeToAbortException extends GitException {
    public NoMergeToAbortException() {
        super(String.format("Сейчас не происходит мерж"));
    }
}
