package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.util.*;
import java.io.*;

public class PlatformJarTask extends Task {

    public static final String SYSLIB_AUTO = "auto";
    public static final String SYSLIB_NONE = "none";

    public void setSyslibs(String s) {
        if (s.equals(SYSLIB_NONE) || s.equals(SYSLIB_AUTO))
            systemLibs = s;
        else
            throw new BuildException("Bad 'syslibs' parameter... Only 'auto' or 'none' available, was " + s);
    }

    public String getSyslibs() {
        return systemLibs;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }


    public File getOutdir() {
        return outdir;
    }


    public void setOutdir(File outdir) {
        this.outdir = outdir;
    }


    public void execute() throws BuildException {
        try {
            execute_internal();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException("Failed to create native .jar", e);
        }
    }

    public void execute_internal() throws BuildException {
        props = PropertyHelper.getPropertyHelper(getProject());

        if (outdir == null)
            throw new BuildException("Missing required attribute 'outdir'. This directory is used for building the .jar file...");

        if (outdir.exists()) {
            outdir.delete();
        }

        outdir.mkdirs();

        for (LibraryEntry e : libs)
            processLibraryEntry(e);

        if (systemLibs.equals(SYSLIB_AUTO))
            processSystemLibs();

        writeQtJambiDeployment();

    }

    private void writeQtJambiDeployment() {
        // TODO: missing systemlibs...

        PrintWriter writer;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, "qtjambi-deployment.xml"))));
        } catch (IOException e) {
            e.printStackTrace();
            throw new BuildException("Failed to open 'qtjambi-deployment.xml' for writing in '" + outdir + "'");
        }

        writer.println("<qtjambi-deploy"
                       + " system=\"" + props.getProperty(null, InitializeTask.OSNAME).toString()
                       + "\">");
        writer.println("\n  <cache key=\"" + cacheKey + "\" />");

        // system libraries that must be loaded first of all...
        if (systemLibs.equals(SYSLIB_AUTO)) {
            if (runtimeLibs.size() > 0)
                writer.println("\n  <!-- Runtime libraries, automatically loaded... -->");
            for (String rt : runtimeLibs) {
                writer.println("  <library name=\"" + rt + "\" load=\"yes\" />");
            }
        }

        writer.println("\n  <!-- User specified libraries... -->");
        for (LibraryEntry e : libs) {
            String libraryName = e.getName();
            String subdir = e.getSubdir();
            String load = e.getLoad();

            writer.print("  <library name=\"" + subdir + "/" + libraryName + "\"");
            if (!load.equals(LibraryEntry.LOAD_DEFAULT))
                writer.print(" load=\"" + load + "\"");
            writer.println("/>");
        }

        // Manifests and the like...
        if (systemLibs.equals(SYSLIB_AUTO)) {
            if (unpackLibs.size() > 0)
                writer.println("\n  <!-- Dependency libraries, not loaded... -->");
            for (String unpack : unpackLibs) {
                writer.println("  <library name=\"" + unpack + "\" load=\"never\" />");
            }
        }

        // plugins...
        if (pluginPaths.size() > 0) {
            writer.println("\n  <!-- Plugins... -->");
            for (PluginPath p : pluginPaths) {
                writer.println("  <plugin path=\"" + p.getPath() + "\" />");
            }
        }

        writer.println("\n</qtjambi-deploy>");

        writer.close();
    }


    public void addConfiguredLibrary(LibraryEntry task) {
        try {
            task.perform();
            libs.add(task);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException("Failed to add library entry.....");
        }
    }

    public void addConfiguredPlugin(PluginPath path) {
        try {
            path.perform();
            pluginPaths.add(path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException("Failed to add plugin path.......");
        }
    }


    private void processLibraryEntry(LibraryEntry e) {
        File rootPath = e.getRootpath();
        String libraryName = e.getName();
        String subdir = e.getSubdir();

        File src = new File(rootPath, subdir + "/" + libraryName);
        File dest = new File(outdir, subdir + "/" + libraryName);
        try {
            Util.copy(src, dest);
            libraryDir.add(subdir);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BuildException("Failed to copy library '" + libraryName + "'");
        }
    }


    private void processSystemLibs() {
        String compiler = String.valueOf(props.getProperty(null, InitializeTask.COMPILER));
        InitializeTask.Compiler c = InitializeTask.Compiler.resolve(compiler);

        String vcnumber = "80";

        switch (c) {

            // The manifest based ones...
        case MSVC2008:
        case MSVC2008_64:
            vcnumber = "90";

        case MSVC2005:
        case MSVC2005_64:

            File crt = new File(props.getProperty(null, InitializeTask.VSREDISTDIR).toString(),
                                "Microsoft.VC" + vcnumber + ".CRT");

            String files[] = new String[] { "Microsoft.VC" + vcnumber + ".CRT.manifest",
                                            "msvcm" + vcnumber + ".dll",
                                            "msvcp" + vcnumber + ".dll",
                                            "msvcr" + vcnumber + ".dll"
            };

            for (String libDir : libraryDir) {
                for (String name : files) {
                    String lib = libDir + "/Microsoft.VC" + vcnumber + ".CRT/" + name;
                    unpackLibs.add(lib);

                    try {
                        Util.copy(new File(crt, name), new File(outdir, lib));
                    } catch(Exception e) {
                        e.printStackTrace();
                        throw new BuildException("Failed to copy VS CRT libraries", e);
                    }
                }
            }

            break;

        case MSVC1998:
            copyRuntime("msvcr60.dll");
            copyRuntime("msvcp60.dll");
            break;

        case MSVC2002:
            copyRuntime("msvcr70.dll");
            copyRuntime("msvcp70.dll");
            break;

        case MSVC2003:
            copyRuntime("msvcr71.dll");
            copyRuntime("msvcp71.dll");
            break;

        case MinGW:
            copyRuntime("mingwm10.dll");
            break;

        case GCC:
            if (Util.OS() == Util.OS.LINUX) copyRuntime("libstdc++.so.6");
            break;

        case OldGCC:
            if (Util.OS() == Util.OS.LINUX) copyRuntime("libstdC++.so.5");
            break;
        }

    }


    private void copyRuntime(String name) {
        File rt = Util.findInLibraryPath(name);
        if (rt == null) {
            throw new BuildException("Runtime library '" + name + "' was not found in library path...");
        }

        String libDir = props.getProperty(null, InitializeTask.LIBSUBDIR).toString();

        try {
            Util.copy(rt, new File(outdir, libDir + "/" + name));
            runtimeLibs.add(libDir + "/" + name);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BuildException("Failed to copy runtime library...", e);
        }
    }


    private String cacheKey = "default";
    private File outdir;
    private List<LibraryEntry> libs = new ArrayList<LibraryEntry>();
    private Set<String> libraryDir = new HashSet<String>();
    private List<String> unpackLibs = new ArrayList<String>();
    private List<String> runtimeLibs = new ArrayList<String>();
    private String systemLibs = SYSLIB_AUTO;
    private List<PluginPath> pluginPaths = new ArrayList<PluginPath>();

    private PropertyHelper props;
}

