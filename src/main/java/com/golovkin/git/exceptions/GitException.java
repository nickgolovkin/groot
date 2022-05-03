package com.golovkin.git.exceptions;

import java.util.List;

public class GitException extends RuntimeException {
    public GitException(String message) {
        super(message);
    }

    public GitException(List<String> messages) {
        super(String.join(" ", messages));
    }
}
