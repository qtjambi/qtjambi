package com.trolltech.qt.internal;

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

}
