package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;

public class InitializeTask extends Task {

    public enum Compiler {
        MSVC1998("vc98"),
        MSVC2002("vc2002"),
        MSVC2003("vc2003"),
        MSVC2005("vc2005"),
        MSVC2005_64("vc2005x64"),
        MSVC2008("vc2008"),
        MSVC2008_64("vc2008_64"),
        MinGW("mingw"),
        OldGCC("gcc3.3"),
        GCC("gcc"),
        Other("unknown");

        Compiler(String n) {
            this.name = n;
        }

        public String toString() { return name; }

        private String name;

        public static Compiler resolve(String name) {
            if (name.equals("vc98")) return MSVC1998;
            if (name.equals("vc2002")) return MSVC2002;
            if (name.equals("vc2003")) return MSVC2003;
            if (name.equals("vc2005")) return MSVC2005;
            if (name.equals("vc2005x64")) return MSVC2005_64;
            if (name.equals("vc2008")) return MSVC2008;
            if (name.equals("vc2008x64")) return MSVC2008_64;
            if (name.equals("mingw")) return MinGW;
            if (name.equals("gcc3.3")) return OldGCC;
            if (name.equals("gcc")) return GCC;
            return Other; 
        }
    }

    public static final String OSNAME = "qtjambi.osname";
    public static final String LIBSUBDIR = "qtjambi.libsubdir";
    public static final String QTDIR = "qtjambi.qtdir";
    public static final String QMAKESPEC = "qtjambi.qmakespec";
    public static final String VERSION = "qtjambi.version";

    public static final String COMPILER = "qtjambi.compiler";

    public static final String VSINSTALLDIR = "qtjambi.vsinstalldir";
    public static final String VSREDISTDIR = "qtjambi.vsredistdir";

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void execute() throws BuildException {
        props = PropertyHelper.getPropertyHelper(getProject());
        props.setNewProperty(null, OSNAME, decideOSName());
        props.setNewProperty(null, LIBSUBDIR, decideLibSubDir());
        props.setNewProperty(null, QTDIR, decideQtDir());
        props.setNewProperty(null, QMAKESPEC, decideQMakeSpec());

        // TODO: Find a better way to get a hold of version...
        props.setNewProperty(null, VERSION, "4.4.0_01");

        props.setNewProperty(null, COMPILER, decideCompiler());

        checkCompilerDetails();

        // Sanity checks...
        if (Util.OS() == Util.OS.WINDOWS) {
            boolean vmx64 = decideOSName().contains("64");
            boolean compiler64 = compiler == Compiler.MSVC2005_64 || compiler == Compiler.MSVC2008_64;
            if (vmx64 != compiler64) {
                if (vmx64)
                    throw new BuildException("Trying to mix 64-bit virtual machine with 32-bit MSVC compiler...");
                else
                    throw new BuildException("Trying to mix 32-bit virtual machine with 64-bit MSVC compiler...");
            }
        }
    }

    private void checkCompilerDetails() {
        switch (compiler) {
            case MSVC2005:
            case MSVC2005_64:
            case MSVC2008:
            case MSVC2008_64:
                String vcdir = System.getenv("VSINSTALLDIR");
                if (vcdir == null)
                    throw new BuildException("missing required environment variable 'VSINSTALLDIR' used to locate MSVC redistributables");
                props.setNewProperty(null, VSINSTALLDIR, vcdir);

                String redistDir;
                if (compiler == Compiler.MSVC2005_64 || compiler == Compiler.MSVC2008_64)
                    redistDir = vcdir + "/vc/redist/amd64";
                else
                    redistDir = vcdir + "/vc/redist/x86";
                if (!new File(redistDir).exists())
                    throw new BuildException("MSVC redistributables not found in '" + redistDir + "'");
                props.setNewProperty(null, VSREDISTDIR, redistDir);
                
                break;
        }
    }

    private String decideCompiler() {
        switch(Util.OS()) {
            case WINDOWS:
                String spec = props.getProperty(null, QMAKESPEC).toString();
                if (spec.contains("msvc"))
                    compiler = testForVisualStudio();
                else if (spec.contains("gcc"))
                    compiler = testForMinGW();
                break;
            case MAC:
                compiler = Compiler.GCC;
                break;
            case LINUX:
                compiler = Compiler.GCC;
                break;
        }

        if (verbose) System.out.println("qtjambi.compiler: " + compiler.toString());
        return compiler.toString();
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
            String output = Util.execute("cl.exe")[1];
            if (output.contains("12.0")) return Compiler.MSVC1998;
            else if (output.contains("13.00")) return Compiler.MSVC2002;
            else if (output.contains("13.10")) return Compiler.MSVC2003;
            else if (output.contains("14.00") && output.contains("x64")) return Compiler.MSVC2005_64;
            else if (output.contains("14.00")) return Compiler.MSVC2005;
            else if (output.contains("15.00") && output.contains("x64")) return Compiler.MSVC2008_64;
            else if (output.contains("15.00")) return Compiler.MSVC2008;
            else
                throw new BuildException("Failed to detect Visual Studio version\n  \"" + output + "\"");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        throw new BuildException("Failed to properly execute from 'cl' command");
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
    private PropertyHelper props;
}
