package com.trolltech.qt;

public class QtJambi {

    public static final int MAJOR_VERSION = 4;
    public static final int MINOR_VERSION = 3;
    public static final int PATCH_VERSION = 0;

    public static final int BUILD_NUMBER = 1;

    public static final String VERSION_STRING = String.format("%1$d.%2$d.%3$d_%4$02d",
            MAJOR_VERSION, MINOR_VERSION, PATCH_VERSION, BUILD_NUMBER);
}
