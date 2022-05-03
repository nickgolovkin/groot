package com.golovkin.dialogs.newbranch;

import com.golovkin.dialogs.InputParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewBranchDialogInputParser implements InputParser<NewBranchDialogInput> {
    private static final Pattern INPUT_PATTERN = Pattern.compile("new branch (?<name>.+)");

    public NewBranchDialogInput parse(String input) {
        Matcher matcher = INPUT_PATTERN.matcher(input);
        matcher.find();

        String newBranchName = matcher.group("name");
        return new NewBranchDialogInput(newBranchName);
    }

    @Override
    public Pattern getInputPattern() {
        return INPUT_PATTERN;
    }
}
