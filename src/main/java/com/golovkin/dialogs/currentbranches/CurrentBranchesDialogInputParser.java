package com.golovkin.dialogs.currentbranches;

import com.golovkin.dialogs.DialogInputParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrentBranchesDialogInputParser implements DialogInputParser<CurrentBranchesDialogInput> {
    private static final Pattern INPUT_PATTERN = Pattern.compile("current branches");

    public CurrentBranchesDialogInput parse(String input) {
        return new CurrentBranchesDialogInput();
    }

    @Override
    public Pattern getInputPattern() {
        return INPUT_PATTERN;
    }
}
