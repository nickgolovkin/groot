package com.golovkin.git.exceptions;

public class NoCherryPickToAbortException extends GitException {
    public NoCherryPickToAbortException() {
        super("Сейчас не происходит мерж");
    }
}
