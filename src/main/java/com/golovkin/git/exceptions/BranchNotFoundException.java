package com.golovkin.git.exceptions;

public class BranchNotFoundException extends GitException {
    private final String branchName;

    public BranchNotFoundException(String branchName) {
        super(String.format("Ветка %s не найдена", branchName));

        this.branchName = branchName;
    }

    public String getBranchName() {
        return branchName;
    }
}
