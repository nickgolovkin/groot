package com.golovkin.dialogs;

import java.util.regex.Pattern;

public interface InputParser<Input extends com.golovkin.dialogs.Input> {
    Input parse(String input);
    Pattern getInputPattern();
}
