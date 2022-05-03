package com.golovkin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    private RegexUtils() {
    }

    public static String extractSubstring(String input, Pattern pattern, String groupName) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(groupName);
        }

        return null;
    }

    public static String extractSubstring(String input, String pattern, String groupName) {
        return extractSubstring(input, Pattern.compile(pattern), groupName);
    }
}
