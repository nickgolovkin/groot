package com.golovkin.dialogs.checkout;

import com.golovkin.dialogs.DialogInput;

public class CheckoutDialogInput implements DialogInput {
    private String branchName;

    public CheckoutDialogInput(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchName() {
        return branchName;
    }
}
