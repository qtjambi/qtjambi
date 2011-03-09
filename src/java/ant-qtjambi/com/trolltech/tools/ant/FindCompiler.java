package com.trolltech.tools.ant;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;

import com.trolltech.qt.internal.OSInfo;

//NOTE: remove this after removing support for 1.7
@SuppressWarnings("deprecation")
public class FindCompiler {

    private Compiler compiler;
    private boolean verbose = false;
	private PropertyHelper props;
	
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

	public FindCompiler(PropertyHelper props) {
		this.props = props;
	}

	void checkCompilerDetails() {
	        switch (compiler) {
	            case MSVC2005:
	            case MSVC2005_64:
	            case MSVC2008:
	            case MSVC2008_64:
	                String vcdir = System.getenv("VSINSTALLDIR");
	                if (vcdir == null) {
	                    throw new BuildException("missing required environment variable " +
	                    		"'VSINSTALLDIR' used to locate MSVC redistributables");
	                }
	                props.setNewProperty((String) null, InitializeTask.VSINSTALLDIR, vcdir);

	                String redistDir;
	                if (compiler == Compiler.MSVC2005_64 || compiler == Compiler.MSVC2008_64)
	                    redistDir = vcdir + "/vc/redist/amd64";
	                else
	                    redistDir = vcdir + "/vc/redist/x86";
	                
	                if (!new File(redistDir).exists()) {
	                    throw new BuildException("MSVC redistributables not found in '" + redistDir + "'");
	                }
	                
	                props.setNewProperty((String) null, InitializeTask.VSREDISTDIR, redistDir);
	                break;
	        }
	        checkCompilerBits();
	    }
	    
	    /**
	     * check if trying to mix 32 bit vm with 64 bit compiler and other way around
	     */
	    void checkCompilerBits() {
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
	    }
	    
	    private void checkWindowsCompilers() {
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
	            throw new BuildException("No compiler detected, please make sure " +
	            		"MinGW or VisualC++ binaries are available in PATH");
	        }
	    }
	    
	    private void checkSolarisCompiler() {
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
	    }

	    String decideCompiler() {
	        switch(OSInfo.os()) {
	            case Windows:
	            	checkWindowsCompilers();
	                break;
	            case MacOS:
	                compiler = Compiler.GCC;
	                break;
	            case Linux:
	            case FreeBSD:
	                compiler = testForGCC();
	                break;
	            case Solaris:
	            	checkSolarisCompiler();
	                break;
	        }

	        if (verbose) System.out.println("qtjambi.compiler: " + compiler.toString());
	        return compiler.toString();
	    }

	    private Compiler testForGCC() {
	        try {
	            String output = Exec.execute("gcc", "-dumpversion")[0];
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

	    /**
	     * Takes output from gcc and if its platform target corresponds to mingw, returns 
	     * Compiler.Mingw.
	     * @return Compiler.MinGW if successful, null otherwise.
	     */
	    private Compiler testForMinGW() {
	        try {
	            String output = Exec.execute("gcc", "-v")[1];
	            if (output.contains("mingw"))
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
	            String output = Exec.execute("cl.exe")[1];
	            if (output.contains("12.0"))
	                return Compiler.MSVC1998;
	            if (output.contains("13.00"))
	                return Compiler.MSVC2002;
	            if (output.contains("13.10"))
	                return Compiler.MSVC2003;
	            if (output.contains("14.00")) {
	                if (output.contains("x64"))
	                    return Compiler.MSVC2005_64;
	                return Compiler.MSVC2005;
	            }
	            if (output.contains("15.00")) {
	                if (output.contains("x64"))
	                    return Compiler.MSVC2008_64;
	                return Compiler.MSVC2008;
	            }
	            throw new BuildException("Failed to detect Visual Studio version\n  \"" + output + "\"");
	        } catch (InterruptedException ex) {
	        } catch (IOException ex) {
	        }
	        return null;
	    }

	    String decideOSName() {
	        String osname = OSInfo.osArchName();
	        if (verbose) System.out.println("qtjambi.osname: " + osname);
	        return osname;
	    }
}
