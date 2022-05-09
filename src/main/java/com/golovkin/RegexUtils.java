package com.golovkin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    private RegexUtils() {
    }

    public static String extractSubstring(String input, Pattern pattern, String groupName) {
        if (input == null) {
            return null;
        }

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(groupName);
        }

        return null;
    }

    public static String extractSubstring(String input, String pattern, String groupName) {
        return extractSubstring(input, Pattern.compile(pattern), groupName);
    }

    public static String extractFirstOccurrence(String input, Pattern pattern) {
        if (input == null) {
            return null;
        }

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(0);
        }

        return null;
    }

    public static String extractFirstSubstring(List<String> input, Pattern pattern, String groupName) {
        if (input == null) {
            return null;
        }

        for (String inputEntry : input) {
            Matcher matcher = pattern.matcher(inputEntry);
            if (matcher.find()) {
                return matcher.group(groupName);
            }
        }

        return null;
    }

    public static boolean contains(String input, Pattern pattern) {
        if (input == null) {
            return false;
        }

        return pattern.matcher(input).find();
    }
}
