package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;
import java.util.*;

public class Util {

    enum OS {
        UNKNOWN, WINDOWS, LINUX, MAC
    }

    public static OS OS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("linux"))
            return OS.LINUX;
        if (os.contains("windows"))
            return OS.WINDOWS;
        if (os.contains("mac os x"))
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
            if (exec.isFile())
                return exec;
        }
        throw new BuildException("Could not find executable: " + name);
    }

    private static class StreamConsumer extends Thread {

        private StreamConsumer(InputStream in, PrintStream out) {
            this.in = in;
            this.out = out;
        }

        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            try {
                while ( (line = reader.readLine()) != null) {
                    out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private PrintStream out;
        private InputStream in;
    }


    public static void redirectOutput(Process proc, boolean silent) {
        try {
            new StreamConsumer(proc.getInputStream(), System.out).start();
            new StreamConsumer(proc.getErrorStream(), System.err).start();
            proc.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
