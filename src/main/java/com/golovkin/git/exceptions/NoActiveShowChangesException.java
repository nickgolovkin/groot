package com.golovkin.git.exceptions;

public class NoActiveShowChangesException extends GitException {
    public NoActiveShowChangesException() {
        super("Сейчас не производится показ изменений");
    }
}
