package com.trolltech.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @todo Rewrite. This kind of API is PITA to use and maintain.
 */
class Exec {

    /**
     * Convenience method for exec(String, File).
     * @param command Command to be executed.
     */
    public static void exec(String command, Project project) {
        exec(command, null, project);
    }

    /**
     * Execute command command in directory dir.
     * @param command Command to be executed.
     * @param dir Directory where command should be executed.
     * @throws BuildException Thrown if process exit value is not zero or IOException has been occurred.
     */
    public static void exec(String command, File dir, Project project) throws BuildException {
        String directory = ((dir != null) ? "(" + Util.makeCanonical(dir) + ")" : "");
        System.out.println("Running : " + directory + " " + command);
        try {
            Process process = Runtime.getRuntime().exec(command, null, dir);
            Util.redirectOutput(process);
            if(process.exitValue() != 0) {
                String exitValueAsHex = String.format("0x%1$08x", new Object[] { process.exitValue() });
                throw new BuildException("Running: '" + command.toString() + "' failed.  exitStatus=" + process.exitValue() + " (" + exitValueAsHex + ")");
            }
        } catch(IOException e) {
            throw new BuildException("Running: " + command + " failed with error message: " + e.getMessage(), e);
        }
    }

    /**
     * TODO: this should be merged with above one, repeating code is not that wise.
     *
     * Executes process in more verbose manner.
     * @param cmd Array of command and its arguments to be executed.
     * @param dir Directory where should be executed.
     * @param verbose Whether to be verbose.
     * @throws BuildException Thrown if process exit value is not zero or IOException has been occurred.
     */
    public static void exec(String cmd[], File dir, Project project, boolean verbose) throws BuildException {
        if(verbose) {
            StringBuilder b = new StringBuilder();
            for(String s : cmd)
                b.append(s).append(' ');
            System.out.println("Running : " + ((dir!=null)? "(" + Util.makeCanonical(dir) + ")" : "") + " " + b);
        }

        try {
            Process process = Runtime.getRuntime().exec(cmd, null, dir);
            Util.redirectOutput(process);
            if(process.exitValue() != 0) {
                String exitValueAsHex = String.format("0x%1$08x", new Object[] { process.exitValue() });
                throw new BuildException("Running: '" + join(cmd) + "' failed.  exitStatus=" + process.exitValue() + " (" + exitValueAsHex + ")");
            }
        } catch(IOException e) {
            throw new BuildException("Running: '" + join(cmd) + "' failed.", e);
        }
    }

    public static void execute(List<String> command, File directory, Project project) throws BuildException {
        execute(command, directory, project, null);
    }

    private static void setupEnvironment(Map<String, String> env, PropertyHelper props, String ldpath) {
        String s;

        if(ldpath != null) {
            env.put("LD_LIBRARY_PATH", ldpath);	// FIXME: Should merge into existing value
        } else {
            s = (String) props.getProperty("qt.libdir");
            if(s != null)
                env.put("LD_LIBRARY_PATH", s);	// FIXME: Should merge into existing value
        }

        s = (String) props.getProperty("java.home.target");
        if(s != null)
            env.put("JAVA_HOME_TARGET", s);
        s = (String) props.getProperty("java.osarch.target");
        if(s != null)
            env.put("JAVA_OSARCH_TARGET", s);

        //something extra?
        s = (String) props.getProperty("qtjambi.phonon.includedir");
        if(s != null && s.length() > 0)
            env.put("PHONON_INCLUDEPATH", s);

        s = (String) props.getProperty("qtjambi.phonon.libdir");
        if(s != null && s.length() > 0)
            env.put("PHONON_LIBS", s);
    }

    public static void execute(List<String> command, File directory, Project project, String ldpath) throws BuildException {
        System.out.println("Executing: " + command.toString() + " in directory " + ((directory != null) ? directory.toString() : "<notset>"));
        ProcessBuilder builder = new ProcessBuilder(command);

        // NOTE: this is most likely very linux-specific system. For Windows one would use PATH instead,
        // but it should not be needed there in first place... Only if you want to have same kind of building
        // environment one can have for Linux.
        // it shouldn't affect to Windows environment though.
        Map<String, String> env = builder.environment();
        PropertyHelper props = PropertyHelper.getPropertyHelper(project);
        setupEnvironment(env, props, ldpath);

        if(directory != null)
            builder.directory(directory);
        try {
            Process process = builder.start();
            Util.redirectOutput(process);
            if(process.exitValue() != 0) {
                String exitValueAsHex = String.format("0x%1$08x", new Object[] { process.exitValue() });
                throw new BuildException("Running: '" + command.toString() + "' failed.  exitStatus=" + process.exitValue() + " (" + exitValueAsHex + ")");
            }
        } catch(IOException e) {
            throw new BuildException("Running: '" + command.toString() + "' failed.", e);
        }
    }

    public static String[] executeCaptureOutput(List<String> command, File directory, Project project, String ldpath) throws BuildException, InterruptedException, IOException {
        ProcessBuilder builder = new ProcessBuilder(command);

        // NOTE: this is most likely very linux-specific system. For Windows one would use PATH instead,
        // but it should not be needed there in first place... Only if you want to have same kind of building
        // environment one can have for Linux.
        // it shouldn't affect to Windows environment though.
        Map<String, String> env = builder.environment();
        PropertyHelper props = PropertyHelper.getPropertyHelper(project);
        setupEnvironment(env, props, ldpath);

        if(directory != null)
            builder.directory(directory);
        PrintStream out = null;
        PrintStream err = null;
        try {
            Process process = builder.start();
            
            ByteArrayOutputStream outdata = new ByteArrayOutputStream();
            out = new PrintStream(outdata);

            ByteArrayOutputStream errdata = new ByteArrayOutputStream();
            err = new PrintStream(errdata);

            StreamConsumer stdoutReader = new StreamConsumer(process.getInputStream(), out);
            StreamConsumer stderrReader = new StreamConsumer(process.getErrorStream(), err);
            stdoutReader.start();
            stderrReader.start();
            process.waitFor();

            stdoutReader.join();
            stderrReader.join();

            err.close();
            err = null;
            out.close();
            out = null;

            if(process.exitValue() != 0) {
                String exitValueAsHex = String.format("0x%1$08x", new Object[] { process.exitValue() });
                System.err.println("Running: '" + command.toString() + "' failed.  exitStatus=" + process.exitValue() + " (" + exitValueAsHex + ")");
            }

            return new String[] { outdata.toString(), errdata.toString() };
        } finally {
            if(err != null) {
                try {
                    err.close();
                } catch(Exception eat) {
                }
                err = null;
            }
            if(out != null) {
                try {
                    out.close();
                } catch(Exception eat) {
                }
                out = null;
            }
        }
    }

    /**
     * Internal helper of Exec.
     * @param ar What to join
     * @return array joined together to form "foo1, foo2, .."
     */
    private static String join(String ar[]) {
        String s = "";
        for(int i = 0; i<ar.length; ++i) {
            s += ar[i];
            if(i < ar.length - 1)
                s += " ";
        }
        return s;
    }
}
