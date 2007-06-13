package com.trolltech.tools.ant;

public class Util {
    
    static final int UNKNOWN = 0;
    static final int WINDOWS = 1;
    static final int LINUX = 2;
    static final int MAC = 3;
    
    public static int OS() {
        String os = System.getProperty("os.name");
        if (os.equalsIgnoreCase("linux"))
            return LINUX;
        if (os.equalsIgnoreCase("windows"))
            return WINDOWS;
        if (os.equalsIgnoreCase("mac"))
            return MAC;
        return UNKNOWN;
    }
}
