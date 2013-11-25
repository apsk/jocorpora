package com.github.apsk.jocorpora;

class StringUtil {
    public static String capitalize(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
}
