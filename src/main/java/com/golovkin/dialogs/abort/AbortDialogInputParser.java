package com.golovkin.dialogs.deletebranch;

import com.golovkin.dialogs.DialogInputParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeleteBranchDialogInputParser implements DialogInputParser<DeleteBranchDialogInput> {
    private static final Pattern INPUT_PATTERN = Pattern.compile("delete branch (?<name>.+)");

    public DeleteBranchDialogInput parse(String input) {
        Matcher matcher = INPUT_PATTERN.matcher(input);
        matcher.find();

        String newBranchName = matcher.group("name");
        return new DeleteBranchDialogInput(newBranchName);
    }

    @Override
    public Pattern getInputPattern() {
        return INPUT_PATTERN;
    }
}
