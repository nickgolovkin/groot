package com.golovkin.git.commands.checkout;

import com.golovkin.git.commands.GitCommandOutput;

public class CheckoutGitCommandOutput implements GitCommandOutput {
    private final boolean isNothingToCommit;

    public CheckoutGitCommandOutput(boolean isNothingToCommit) {
        this.isNothingToCommit = isNothingToCommit;
    }

    public boolean isNothingToCommit() {
        return isNothingToCommit;
    }
}
