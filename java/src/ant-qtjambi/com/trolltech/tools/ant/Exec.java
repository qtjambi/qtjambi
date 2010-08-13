package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;
import java.util.*;

import com.trolltech.qt.internal.*;
import com.trolltech.qt.internal.OSInfo.OS;


class Exec {
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

        StreamConsumer stdoutReader = new StreamConsumer(p.getInputStream(), out);
        StreamConsumer stderrReader = new StreamConsumer(p.getErrorStream(), err);
        stdoutReader.start();
        stderrReader.start();
        p.waitFor();

        stdoutReader.join();
        stderrReader.join();
        out.close();
        err.close();

        return new String[] { outdata.toString(), errdata.toString() };
    }

    public static void exec(String command) {
        exec(command, null);
    }

    public static void exec(String command, File dir) throws BuildException {
        String directory = ((dir != null) ? "(" + makeCanonical(dir) + ")" : "");
        System.out.println("Running : " + directory + " " + command);
        try {
            Process process = Runtime.getRuntime().exec(command, null, dir);
            Util.redirectOutput(process);
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
            Util.redirectOutput(process);
            if (process.exitValue() != 0) {
                throw new BuildException("Running: '" + join(cmd) + "' failed.");
            }
        } catch (IOException e) {
            throw new BuildException("Running: '" + join(cmd) + "' failed.", e);
        }
    }
    
    public static void exec(String command, String[] args, File dir) throws BuildException {
      //  ProcessBuilder builder = new ProcessBuilder(command, );
    }

    public static String escape(String param) {
        OSInfo.os();
        if(OSInfo.os() == OS.Windows) {
            return "\"" + param + "\"";
        }
        return param;
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

}