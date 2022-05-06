package com.golovkin.dialogs.abort;

import com.golovkin.dialogs.DialogInputParser;
import com.golovkin.dialogs.deletebranch.DeleteBranchDialogInput;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbortDialogInputParser implements DialogInputParser<AbortDialogInput> {
    private static final Pattern INPUT_PATTERN = Pattern.compile("abort");

    public AbortDialogInput parse(String input) {
        return new AbortDialogInput();
    }

    @Override
    public Pattern getInputPattern() {
        return INPUT_PATTERN;
    }
}
