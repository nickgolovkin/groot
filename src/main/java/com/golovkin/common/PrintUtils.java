package com.golovkin.common;

public class PrintUtils {
    public static void printf(String string, Object... args) {
        System.out.printf(string + "\n", args);
    }
}
