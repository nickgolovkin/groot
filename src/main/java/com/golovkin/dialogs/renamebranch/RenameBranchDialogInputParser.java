package com.golovkin.dialogs.renamebranch;

import com.golovkin.dialogs.DialogInputParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameBranchDialogInputParser implements DialogInputParser<RenameBranchDialogInput> {
    private static final Pattern INPUT_PATTERN = Pattern.compile("rename branch (?<name>.+)");

    public RenameBranchDialogInput parse(String input) {
        Matcher matcher = INPUT_PATTERN.matcher(input);
        matcher.find();

        String newBranchName = matcher.group("name");
        return new RenameBranchDialogInput(newBranchName);
    }

    @Override
    public Pattern getInputPattern() {
        return INPUT_PATTERN;
    }
}
