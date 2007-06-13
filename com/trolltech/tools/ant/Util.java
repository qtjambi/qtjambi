package com.trolltech.tools.ant;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;

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

    public static File LOCATE_EXEC(String name) {
        return LOCATE_EXEC(name, "", "");
    }

    public static File LOCATE_EXEC(String name, String prepend, String append) {
        String searchPath = "";
        if (prepend != null && !prepend.equals(""))
            searchPath += prepend + File.pathSeparator;
        searchPath += System.getenv("PATH");
        if (append != null && !append.equals(""))
            searchPath += File.pathSeparator + append;
        StringTokenizer tokenizer = new StringTokenizer(searchPath, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            File exec = new File(tokenizer.nextToken() + File.separator + name);
            if (exec.canExecute())
                return exec;
        }
        throw new BuildException("Could not find executable: " + name);
    }
}
