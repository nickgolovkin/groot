package com.golovkin.dialogs.commit;

import com.golovkin.dialogs.DialogInputParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitDialogInputParser implements DialogInputParser<CommitDialogInput> {
    private static final Pattern INPUT_PATTERN = Pattern.compile("commit \"(?<message>.+)\"");

    public CommitDialogInput parse(String input) {
        Matcher matcher = INPUT_PATTERN.matcher(input);
        matcher.find();

        String newBranchName = matcher.group("message");
        return new CommitDialogInput(newBranchName);
    }

    @Override
    public Pattern getInputPattern() {
        return INPUT_PATTERN;
    }
}
