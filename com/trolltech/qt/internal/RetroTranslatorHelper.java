package com.trolltech.qt.internal;

import java.util.Stack;

// !!NOTE!! This class can have no dependencies on Qt
//          as it is used by the NativeLibraryManager

/**
 * This class is an internal binding layer between JNI and
 * Java 1.5 API to make it possible to retrotranslate Qt Jambi
 * and make it work with JRE 1.4.
 *
 * @exclude
 *
 */
public class RetroTranslatorHelper {

    /**
     * Calls getEnumConstants method on given class
     *
     * @param cls The class on which to call the method
     * @return The result of calling the method
     */
    public static Object[] getEnumConstants(Class<?> cls) {
        return cls.getEnumConstants();
    }

    /**
     * Determines whether the given class is a subclass of java.lang.Enum
     *
     * @param cls The class to check
     * @return true if the class is a subclass of Enum, otherwise false
     */
    public static boolean isEnumType(Class<?> cls) {
        return Enum.class.isAssignableFrom(cls);
    }

    /**
     * If the given object is an enum, returns the result of
     * calling ordinal() on the enum.
     */
    public static int enumOrdinal(Object _enum) {
        return ((Enum) _enum).ordinal();
    }

    /**
     * Same as calling split(string, token, 0)
     */
    public static String[] split(String string, String token) {
        return split(string, token, 0);
    }

    /**
     * Splits a string on a specified token. The token is not
     * a regular expression. Works around missing String.split() in some
     * runtime environments and is also a lot faster than using
     * a regular expression for splitting on regular string tokens.
     */
    public static String[] split(String string, String token, int limit) {
        Stack<String> parts = new Stack<String>();

        int pos = 0, nextPos = 0;
        do {
            // Break if limit is reached
            if (limit > 0 && parts.size() == limit-1) {
                parts.push(string.substring(pos));
                break;
            } else {
                nextPos = string.indexOf(token, pos);

                String part = nextPos >= 0 ? string.substring(pos, nextPos) : string.substring(pos);
                // System.err.println("nextPos: " + nextPos + " part: " + part);
                if (part.length() != 0
                    || parts.isEmpty()
                    || parts.peek().length() != 0) {
                    parts.push(part);
                }
                pos = nextPos + token.length();
                // Always progress at least by one to avoid infinite loops
                if (pos == nextPos)
                    ++pos;
            }

        } while (nextPos >= 0 && pos <= string.length());

        // No trailing empty elements if limit==0
        if (limit == 0 && parts.peek().length() == 0)
            parts.pop();

        return parts.toArray(new String[] {});
    }
}
