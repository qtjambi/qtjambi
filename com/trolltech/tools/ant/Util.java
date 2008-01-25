package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;
import java.util.*;

import com.trolltech.qt.internal.*;

class Util {

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
                return makeCanonical(exec);
        }
        throw new BuildException("Could not find executable: " + name);
    }

    private static class StreamConsumer extends Thread {

        private StreamConsumer(InputStream in, PrintStream out) {
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            try {
                while ( (line = reader.readLine()) != null) {
                    if (out != null)
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
            StreamConsumer std = new StreamConsumer(proc.getInputStream(), System.out);
            StreamConsumer err = new StreamConsumer(proc.getErrorStream(), System.err);
            std.start();
            err.start();
            proc.waitFor();
            std.join();
            err.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes the command specified by cmd and returns the printed output
     * from the process' stdout and stderr in the array on position 0 and 1 respectivly.
     * @param cmd The command to execute
     * @return An array of length 2, containing the [stdout, stderr] output.
     * @throws IOException If an error occurs
     * @throws InterruptedException If an error occurs...
     */
    public static String[] execute(String ... cmd) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(cmd);

        ByteArrayOutputStream outdata = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outdata);

        ByteArrayOutputStream errdata = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(errdata);

        new StreamConsumer(p.getInputStream(), out).start();
        new StreamConsumer(p.getErrorStream(), err).start();
        p.waitFor();
        out.close();
        err.close();
        return new String[] { outdata.toString(), errdata.toString() };
    }


    public static void copy(File src, File dst) throws IOException {
        File destDir = dst.getParentFile();
        if (!destDir.exists())
            destDir.mkdirs();

        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte buffer[] = new byte[1024 * 64];
        while (in.available() > 0) {
            int read = in.read(buffer);
            out.write(buffer, 0, read);
        }
        in.close();
        out.close();
    }


    public static void copyRecursive(File src, File target) throws IOException {
        if (src.isDirectory()) {
            File entries[] = src.listFiles();
            for (File e : entries) {
                copyRecursive(e, new File(target, e.getName()));
            }
        } else {
            copy(src, target);
        }

    }

    public static File findInPath(String name) {
        String PATH[] = System.getenv("PATH").split(File.pathSeparator);
        for (String p : PATH) {
            File f = new File(p, name);
            if (f.exists())
                return f;
        }
        return null;
    }

    public static File findInLibraryPath(String name) {
        String libraryPath = System.getProperty("java.library.path");

	    // Make /usr/lib an implicit part of library path
	    if (OSInfo.os() == OSInfo.OS.Linux || OSInfo.os() == OSInfo.OS.Solaris)
    	    libraryPath += File.pathSeparator + "/usr/lib";

        String PATH[] = libraryPath.split(File.pathSeparator);
        for (String p : PATH) {
            File f = new File(p, name);
            if (f.exists())
                return f;
        }
        return null;
    }

    public static void exec(String command) {
        exec(command, null);
    }

    public static void exec(String command, File dir) throws BuildException {
        System.out.println("Running : " + ((dir!=null)? "(" + makeCanonical(dir) + ")" : "") + " " + command);
        try {
            Process process = Runtime.getRuntime().exec(command, null, dir);
            Util.redirectOutput(process, true);
            if (process.exitValue() != 0) {
                throw new BuildException("Running: " + command + " failed with exit code: " + process.exitValue());
            }
        } catch (IOException e) {
            throw new BuildException("Running: " + command + " failed with error message: " + e.getMessage(), e);
        }
    }

    public static void exec(String cmd[], File dir, boolean verbose) throws BuildException {
        if (verbose) {
            StringBuilder b = new StringBuilder();
            for (String s : cmd)
                b.append(s).append(' ');
            System.out.println("Running : " + ((dir!=null)? "(" + makeCanonical(dir) + ")" : "") + " " + b);
        }

        try {
            Process process = Runtime.getRuntime().exec(cmd, null, dir);
            Util.redirectOutput(process, true);
            if (process.exitValue() != 0) {
                throw new BuildException("Running: " + cmd + " failed.");
            }
        } catch (IOException e) {
            throw new BuildException("Running: " + cmd + " failed.", e);
        }
    }


    public static File makeCanonical(String file) throws BuildException {
        return makeCanonical(new File(file));
    }

    public static File makeCanonical(File file) throws BuildException {
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            throw new BuildException("Path : " + file.getAbsolutePath() + " failed to create canonical form.", e);
        }
    }
}
