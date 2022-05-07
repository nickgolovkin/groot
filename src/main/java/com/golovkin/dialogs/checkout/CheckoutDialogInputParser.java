package com.golovkin.dialogs.checkout;

import com.golovkin.dialogs.DialogInputParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckoutDialogInputParser implements DialogInputParser<CheckoutDialogInput> {
    private static final Pattern INPUT_PATTERN = Pattern.compile("checkout (?<name>.+)");

    public CheckoutDialogInput parse(String input) {
        Matcher matcher = INPUT_PATTERN.matcher(input);
        matcher.find();

        String newBranchName = matcher.group("name");
        return new CheckoutDialogInput(newBranchName);
    }

    @Override
    public Pattern getInputPattern() {
        return INPUT_PATTERN;
    }
}
