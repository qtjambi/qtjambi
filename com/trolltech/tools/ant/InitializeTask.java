package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.File;

public class InitializeTask extends Task {

    public static final String OSNAME = "qtjambi.osname";
    public static final String LIBSUBDIR = "qtjambi.libsubdir";
    public static final String QTDIR = "qtjambi.qtdir";
    public static final String QMAKESPEC = "qtjambi.qmakespec";
    
    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    private String decideOSName() {
        String osname = null;
        switch (Util.OS()) {
            case WINDOWS:
                if (System.getProperty("os.name").equalsIgnoreCase("amd64")) osname = "win64";
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

    public void execute() throws BuildException {
        PropertyHelper h = PropertyHelper.getPropertyHelper(getProject());
        h.setNewProperty(null, OSNAME, decideOSName());
        h.setNewProperty(null, LIBSUBDIR, decideLibSubDir());
        h.setNewProperty(null, QTDIR, decideQtDir());
        h.setNewProperty(null, QMAKESPEC, decideQMakeSpec());
    }

    private boolean verbose;
}
