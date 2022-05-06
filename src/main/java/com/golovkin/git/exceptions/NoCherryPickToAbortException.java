package com.golovkin.git.exceptions;

public class NoCherryPickToAbortException extends GitException {
    public NoCherryPickToAbortException() {
        super(String.format("Сейчас не происходит мерж"));
    }
}
