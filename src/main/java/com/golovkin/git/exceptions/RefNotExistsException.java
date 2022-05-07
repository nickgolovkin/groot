package com.golovkin.git.exceptions;

public class RefNotExistsException extends GitException {
    private final String refName;

    public RefNotExistsException(String refName) {
        super(String.format("Ref %s не найден", refName));

        this.refName = refName;
    }

    public String getRefName() {
        return refName;
    }
}