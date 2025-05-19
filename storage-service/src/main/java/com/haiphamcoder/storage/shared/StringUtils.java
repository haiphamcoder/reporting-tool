package com.haiphamcoder.storage.shared;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotBlank(final CharSequence charSequence){
        return !isBlank(charSequence);
    }

    public static boolean isBlank(final CharSequence charSequence){
        final int length = charSequence.length();
        if (length == 0) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(charSequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
