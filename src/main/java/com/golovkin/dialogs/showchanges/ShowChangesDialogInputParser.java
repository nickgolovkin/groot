package com.golovkin.dialogs.showchanges;

import com.golovkin.dialogs.DialogInputParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShowChangesDialogInputParser implements DialogInputParser<ShowChangesDialogInput> {
    private static final Pattern INPUT_PATTERN = Pattern.compile("show changes");

    public ShowChangesDialogInput parse(String input) {
        return new ShowChangesDialogInput();
    }

    @Override
    public Pattern getInputPattern() {
        return INPUT_PATTERN;
    }
}
