package com.trolltech.tools.ant;

public class Util {
    
    enum OS {
        UNKNOWN, WINDOWS, LINUX, MAC
    }

    public static OS OS() {
        String os = System.getProperty("os.name");
        if (os.equalsIgnoreCase("linux"))
            return OS.LINUX;
        if (os.equalsIgnoreCase("windows"))
            return OS.WINDOWS;
        if (os.equalsIgnoreCase("mac"))
            return OS.MAC;
        return OS.UNKNOWN;
    }
}
