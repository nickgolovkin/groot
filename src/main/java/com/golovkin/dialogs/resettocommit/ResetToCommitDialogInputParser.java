package com.golovkin.dialogs.resettocommit;

import com.golovkin.dialogs.DialogInputParser;

import java.util.regex.Pattern;

public class ResetToCommitDialogInputParser implements DialogInputParser<ResetToCommitDialogInput> {
    private static final Pattern INPUT_PATTERN = Pattern.compile("reset to commit");

    public ResetToCommitDialogInput parse(String input) {
        return new ResetToCommitDialogInput();
    }

    @Override
    public Pattern getInputPattern() {
        return INPUT_PATTERN;
    }
}
