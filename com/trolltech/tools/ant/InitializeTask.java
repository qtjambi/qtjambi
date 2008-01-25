package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;

import com.trolltech.qt.internal.*;

public class InitializeTask extends Task {

    public enum Compiler {
        MSVC1998("msvc98"),
        MSVC2002("msvc2002"),
        MSVC2003("msvc2003"),
        MSVC2005("msvc2005"),
        MSVC2005_64("msvc2005x64"),
        MSVC2008("msvc2008"),
        MSVC2008_64("msvc2008_64"),
        MinGW("mingw"),
        OldGCC("gcc3.3"),
        GCC("gcc"),
	SUNCC("suncc"),
        Other("unknown");

        Compiler(String n) {
            this.name = n;
        }

        public String toString() { return name; }

        private String name;

        public static Compiler resolve(String name) {
            if (name.equals("msvc98")) return MSVC1998;
            if (name.equals("msvc2002")) return MSVC2002;
            if (name.equals("msvc2003")) return MSVC2003;
            if (name.equals("msvc2005")) return MSVC2005;
            if (name.equals("msvc2005x64")) return MSVC2005_64;
            if (name.equals("msvc2008")) return MSVC2008;
            if (name.equals("msvc2008x64")) return MSVC2008_64;
            if (name.equals("mingw")) return MinGW;
            if (name.equals("gcc3.3")) return OldGCC;
            if (name.equals("gcc")) return GCC;
            if (name.equals("suncc")) return SUNCC;
            return Other;
        }
    }

    public static final String OSNAME           = "qtjambi.osname";
    public static final String LIBSUBDIR        = "qtjambi.libsubdir";
    public static final String QTDIR            = "qtjambi.qtdir";
    public static final String QMAKESPEC        = "qtjambi.qmakespec";
    public static final String VERSION          = "qtjambi.version";
    public static final String COMPILER         = "qtjambi.compiler";
    public static final String CONFIGURATION    = "qtjambi.configuration";
    public static final String PHONON           = "qtjambi.phonon";
    public static final String WEBKIT           = "qtjambi.webkit";
    public static final String PHONON_DS9       = "qtjambi.phonon_ds9";
    public static final String PHONON_GSTREAMER = "qtjambi.phonon_gstreamer";
    public static final String PHONON_QT7       = "qtjambi.phonon_qt7";

    // Windows specific vars...
    public static final String VSINSTALLDIR     = "qtjambi.vsinstalldir";
    public static final String VSREDISTDIR      = "qtjambi.vsredistdir";

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void execute() throws BuildException {
        props = PropertyHelper.getPropertyHelper(getProject());
        props.setNewProperty(null, OSNAME, decideOSName());
        props.setNewProperty(null, LIBSUBDIR, decideLibSubDir());
        props.setNewProperty(null, QTDIR, decideQtDir());

        // TODO: Find a better way to get a hold of version...
        props.setNewProperty(null, VERSION, Version.STRING);

        props.setNewProperty(null, COMPILER, decideCompiler());

        checkCompilerDetails();

        // Sanity checks...
        if (OSInfo.os() == OSInfo.OS.Windows) {
            boolean vmx64 = decideOSName().contains("64");
            boolean compiler64 = compiler == Compiler.MSVC2005_64 || compiler == Compiler.MSVC2008_64;
            if (vmx64 != compiler64) {
                if (vmx64)
                    throw new BuildException("Trying to mix 64-bit virtual machine with 32-bit MSVC compiler...");
                else
                    throw new BuildException("Trying to mix 32-bit virtual machine with 64-bit MSVC compiler...");
            }
        }

        props.setNewProperty(null, CONFIGURATION, decideConfiguration());

        // These depend on both qtdir, libsubdir and configration, so
        // run rather late...
        String phonon = decidePhonon();
        props.setNewProperty(null, PHONON, phonon);
        props.setNewProperty(null, WEBKIT, decideWebkit());
        if (Boolean.parseBoolean(phonon)) {
            switch (OSInfo.os()) {
            case Windows: props.setNewProperty(null, PHONON_DS9, true); break;
            case Linux: props.setNewProperty(null, PHONON_GSTREAMER, true); break;
            case MacOS: props.setNewProperty(null, PHONON_QT7, true); break;
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
        switch(OSInfo.os()) {
            case Windows:

                Compiler msvc = testForVisualStudio();
                Compiler mingw = testForMinGW();

                if (msvc != null && mingw != null) {
                    System.out.println("Both Visual C++ and MinGW compilers are available\n"
                                       + "Choosing based on environment variable QMAKESPEC");
                    String spec = System.getenv("QMAKESPEC");
                    if (spec == null) {
                        throw new BuildException("Environment variable QMAKESPEC is not set...");
                    } else if (spec.contains("msvc")) {
                        compiler = msvc;
                    } else if (spec.contains("g++")) {
                        compiler = mingw;
                    } else {
                        throw new BuildException("Invalid QMAKESPEC variable...");
                    }
                } else if (msvc != null) {
                    compiler = msvc;
                } else if (mingw != null) {
                    compiler = mingw;
                } else {
                    throw new BuildException("No compiler detected, please make sure MinGW or VisualC++ binaries are available in PATH");
                }

                break;
            case MacOS:
                compiler = Compiler.GCC;
                break;
            case Linux:
                compiler = testForGCC();
                break;
            case Solaris:
                 String spec = System.getenv("QMAKESPEC");
                 if (spec == null) {
                     System.out.println("QMAKESPEC environment variable not specified using SunCC compiler");
                     compiler = Compiler.SUNCC;
                 } else if (spec.contains("cc")) {
                     compiler = Compiler.SUNCC;
                 } else if (spec.contains("g++")) {
                     compiler = Compiler.GCC;
                 } else {
                     throw new BuildException("Invalid QMAKESPEC variable...");
                 }
                 break;
        }

        if (verbose) System.out.println("qtjambi.compiler: " + compiler.toString());
        return compiler.toString();
    }

    private Compiler testForGCC() {
        try {
            String output = Util.execute("gcc", "-dumpversion")[0];
            if (output.contains("3.3."))
                return Compiler.OldGCC;
            return Compiler.GCC;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            throw new BuildException("Failed to properly execute 'gcc' command");
        } catch (IOException ex) {
            return null;
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
        String osname = OSInfo.osArchName();
        if (verbose) System.out.println("qtjambi.osname: " + osname);
        return osname;
    }

    private String decideLibSubDir() {
        libSubDir = OSInfo.os() == OSInfo.OS.Windows ? "bin" : "lib";
        if (verbose) System.out.println("qtjambi.libsubdir: " + libSubDir);
        return libSubDir;
    }

    private String decideQtDir() {
        qtdir = System.getenv("QTDIR");
        if (qtdir == null)
            throw new BuildException("QTDIR environment variable missing");
        if (!new File(qtdir).exists())
            throw new BuildException("QTDIR environment variable points to non-existing directory");
        if (verbose) System.out.println("qtjambi.qtdir: " + qtdir);
        return qtdir;
    }

    private String decideConfiguration() {
        String result = null;

        debug = "debug".equals(configuration);
        result = debug ? "debug" : "release";

        if (verbose) System.out.println(CONFIGURATION + ": " + result);
        return result;
    }

    private boolean doesQtLibExist(String name, int version) {
        StringBuilder path = new StringBuilder();
        path.append(props.getProperty(null, QTDIR));
        path.append("/");
        path.append(props.getProperty(null, LIBSUBDIR));
        path.append("/");
        path.append(LibraryEntry.formatQtName(name, debug, version));
        return new File(path.toString()).exists();
    }

    private String decidePhonon() {
        String result = String.valueOf(doesQtLibExist("phonon", 5));
        if (verbose) System.out.println(PHONON + ": " + result);
        return result;
    }

    private String decideWebkit() {
        String result = String.valueOf(doesQtLibExist("QtWebKit", 4));
        if (verbose) System.out.println(WEBKIT + ": " + result);
        return result;
    }


    private Compiler compiler;
    private boolean verbose;
    private PropertyHelper props;
    private String configuration;
    private boolean debug;
    private String qmakespec;
    private String qtdir;
    private String libSubDir;
}
