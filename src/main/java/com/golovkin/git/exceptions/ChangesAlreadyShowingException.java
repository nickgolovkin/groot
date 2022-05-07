package com.golovkin.git.exceptions;

public class ChangesAlreadyShowingException extends GitException {
    public ChangesAlreadyShowingException() {
        super("Уже идет показ изменений");
    }
}
