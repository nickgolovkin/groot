package com.golovkin.dialogs;

import java.util.regex.Pattern;

public interface DialogInputParser<Input extends DialogInput> {
    Input parse(String input);
    Pattern getInputPattern();
}
