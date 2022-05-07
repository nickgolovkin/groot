package com.golovkin.dialogs.unshowchanges;

import com.golovkin.dialogs.DialogInputParser;

import java.util.regex.Pattern;

public class UnshowChangesDialogInputParser implements DialogInputParser<UnshowChangesDialogInput> {
    private static final Pattern INPUT_PATTERN = Pattern.compile("unshow changes");

    public UnshowChangesDialogInput parse(String input) {
        return new UnshowChangesDialogInput();
    }

    @Override
    public Pattern getInputPattern() {
        return INPUT_PATTERN;
    }
}
