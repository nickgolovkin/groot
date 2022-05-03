package com.golovkin.git.exceptions;

public class BranchAlreadyExistsException extends GitException {
    private final String branchName;

    public BranchAlreadyExistsException(String branchName) {
        super(String.format("Ветка %s уже существует", branchName));

        this.branchName = branchName;
    }

    public String getBranchName() {
        return branchName;
    }
}
