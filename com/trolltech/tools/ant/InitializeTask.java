package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;

public class InitializeTask extends Task {

    public enum Compiler {
        MSVC1998,
        MSVC2002,
        MSVC2003,
        MSVC2005,
        MSVC2008,
        MinGW,
        GCC,
        Other
    }

    public static final String OSNAME = "qtjambi.osname";
    public static final String LIBSUBDIR = "qtjambi.libsubdir";
    public static final String QTDIR = "qtjambi.qtdir";
    public static final String QMAKESPEC = "qtjambi.qmakespec";
    public static final String VERSION = "qtjambi.version";
    
    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void execute() throws BuildException {
        PropertyHelper h = PropertyHelper.getPropertyHelper(getProject());
        h.setNewProperty(null, OSNAME, decideOSName());
        h.setNewProperty(null, LIBSUBDIR, decideLibSubDir());
        h.setNewProperty(null, QTDIR, decideQtDir());
        h.setNewProperty(null, QMAKESPEC, decideQMakeSpec());

        // TODO: Find a better way to get a hold of version...
        h.setNewProperty(null, VERSION, "4.4.0_01");

        decideCompiler();
    }


    private void decideCompiler() {
        switch(Util.OS()) {
            case WINDOWS:
                // Check visual studio first...
                compiler = testForVisualStudio();
                if (compiler == null)
                    compiler = testForMinGW();
        }
    }

    private Compiler testForMinGW() {
        try {
            String output = Util.execute("gcc", "--version")[0];
            if (output.contains("mingw") && output.contains("3."))
                return Compiler.MinGW;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            throw new BuildException("Failed to properly execute from 'gcc' command");
        } catch (IOException ex) {
            return null;
        }
        return null;
    }

    private Compiler testForVisualStudio() {
        try {
            String output = Util.execute("cl")[1];
            if (output.contains("12.0")) return Compiler.MSVC1998;
            else if (output.contains("13.00")) return Compiler.MSVC2002;
            else if (output.contains("13.10")) return Compiler.MSVC2003;
            else if (output.contains("14.00")) return Compiler.MSVC2005;
            else if (output.contains("15.00")) return Compiler.MSVC2008;
            else
                throw new BuildException("Failed to detect Visual Studio version\n  \"" + output + "\"");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            throw new BuildException("Failed to properly execute from 'cl' command");
        } catch (IOException ex) {
            return null;
        }
    }

    private String decideOSName() {
        String osname = null;
        switch (Util.OS()) {
            case WINDOWS:
                if (System.getProperty("os.arch").equalsIgnoreCase("amd64")) osname = "win64";
                else osname = "win32";
                break;
            case LINUX:
                osname = "linux32";
                break;
            case MAC:
                osname = "macosx";
                break;
        }
        if (verbose) System.out.println("qtjambi.osname: " + osname);
        return osname;
    }

    private String decideLibSubDir() {
        String dir = Util.OS() == Util.OS.WINDOWS ? "bin" : "lib";
        if (verbose) System.out.println("qtjambi.libsubdir: " + dir);
        return dir;
    }

    private String decideQtDir() {
        String qtdir = System.getenv("QTDIR");
        if (qtdir == null)
            throw new BuildException("QTDIR environment variable missing");
        if (!new File(qtdir).exists())
            throw new BuildException("QTDIR environment variable points to non-existing directory");
        if (verbose) System.out.println("qtjambi.qtdir: " + qtdir);
        return qtdir;
    }

    private String decideQMakeSpec() {
        String spec = System.getenv("QMAKESPEC");
        if (spec == null || spec.length() == 0)
            throw new BuildException("QMAKESPEC environment variable must be set to build Qt Jambi");
        if (verbose) System.out.println("qtjambi.qmakespec: " + spec);
        return spec;
    }

    private Compiler compiler;
    private boolean verbose;
}
