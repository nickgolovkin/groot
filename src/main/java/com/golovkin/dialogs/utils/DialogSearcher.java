package com.golovkin.dialogs.utils;

import com.golovkin.dialogs.AbstractDialog;

import java.util.Map;
import java.util.regex.Pattern;

public class DialogSearcher {
    private Map<Class<? extends AbstractDialog>, AbstractDialog> dialogs;

    public DialogSearcher(Map<Class<? extends AbstractDialog>, AbstractDialog> dialogs) {
        this.dialogs = dialogs;
    }

    public AbstractDialog searchDialog(String input) {
        for (AbstractDialog dialog : dialogs.values()) {
            Pattern inputPattern = dialog.getInputParser().getInputPattern();
            if (input.matches(inputPattern.pattern())) {
                return dialog;
            }
        }

        throw new IllegalArgumentException(String.format("Не удалось обработать команду [%s]", input));
    }
}
