package com.golovkin.common;

public class ColorUtils {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";

    public static String warn(String string) {
        return ANSI_YELLOW + string + ANSI_RESET;
    }

    public static String error(String string) {
        return ANSI_RED + string + ANSI_RESET;
    }
}
