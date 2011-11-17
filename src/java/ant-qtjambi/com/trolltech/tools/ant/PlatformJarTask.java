/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
**
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
**
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;

import com.trolltech.qt.osinfo.OSInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//NOTE: remove this after removing support for 1.7
@SuppressWarnings("deprecation")
public class PlatformJarTask extends Task {

    public static final String SYSLIB_AUTO = "auto";
    public static final String SYSLIB_NONE = "none";

    private String cacheKey = "default";
    private File outdir;
    private List<LibraryEntry> libs         = new ArrayList<LibraryEntry>();
    private Set<String> libraryDir          = new HashSet<String>();
    private List<String> unpackLibs         = new ArrayList<String>();
    private List<String> runtimeLibs        = new ArrayList<String>();
    private String systemLibs               = OSInfo.os() == OSInfo.OS.Solaris ? SYSLIB_NONE : SYSLIB_AUTO;
    private List<PluginPath> pluginPaths    = new ArrayList<PluginPath>();
    private List<PluginDesignerPath> pluginDesignerPaths = new ArrayList<PluginDesignerPath>();
    private boolean debugConfiguration      = false;
    private String javaLibDir               = "";

    private boolean rpath = true;

    private PropertyHelper propertyHelper;

    public void addConfiguredLibrary(LibraryEntry task) {
        try {
            if(!task.isIncluded())
                return;
            task.perform();
            libs.add(task);
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException("Failed to add library entry.....");
        }
    }

    public void addConfiguredPlugin(PluginPath path) {
        try {
            path.perform();
            pluginPaths.add(path);
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException("Failed to add plugin path.......");
        }
    }

    public void addConfiguredPluginDesigner(PluginDesignerPath path) {
        System.out.println("addConfiguredPluginDesigner() path="+path);
        try {
            path.perform();
            pluginDesignerPaths.add(path);
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException("Failed to add plugin-designer path.......");
        }
    }

    public void addConfiguredPlugin_Designer(PluginDesignerPath path) {
        System.out.println("addConfiguredPlugin_Designer() path="+path);
        addConfiguredPluginDesigner(path);
    }

    public void addConfigured(PluginDesignerPath path) {
        System.out.println("addConfigured() path="+path);
        addConfiguredPluginDesigner(path);
    }

    public void add(PluginDesignerPath path) {
        System.out.println("add() path="+path);
        addConfiguredPluginDesigner(path);
    }

    public void setSyslibs(String s) {
        if(OSInfo.os() == OSInfo.OS.Solaris)
            return;
        if(s.equals(SYSLIB_NONE) || s.equals(SYSLIB_AUTO))
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

    public void setRpathenabled(boolean iname) {
        rpath = iname;
    }

    public boolean getRpathenabled() {
        return rpath;
    }

    public void execute() throws BuildException {
        try {
            execute_internal();
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException("Failed to create native .jar", e);
        }
    }

    public void execute_internal() throws BuildException {
        propertyHelper = PropertyHelper.getPropertyHelper(getProject());

        javaLibDir =(String) propertyHelper.getProperty((String) null, InitializeTask.JAVALIBDIR);

        debugConfiguration = "debug".equals(propertyHelper.getProperty((String) null, InitializeTask.CONFIGURATION));

        if(outdir == null) {
            throw new BuildException("Missing required attribute 'outdir'. " +
                    "This directory is used for building the .jar file...");
        }

        if(outdir.exists()) {
            outdir.delete();
        }

        outdir.mkdirs();

        for(LibraryEntry e : libs) {
            processLibraryEntry(e);
        }

        if(systemLibs.equals(SYSLIB_AUTO))
            processSystemLibs();

        if(rpath) {
            if(OSInfo.os() == OSInfo.OS.MacOS) {
                processOSXInstallName();
            }
        }

        writeQtJambiDeployment();
    }

    private void writeQtJambiDeployment() {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new BufferedWriter(
                    new FileWriter(new File(outdir, "qtjambi-deployment.xml"))));
        } catch(IOException e) {
            e.printStackTrace();
            throw new BuildException("Failed to open 'qtjambi-deployment.xml' for writing in '" + outdir + "'");
        }

        writer.println("<qtjambi-deploy" + " system=\""
                            + propertyHelper.getProperty((String) null,
                            InitializeTask.OSNAME).toString() + "\">");
        writer.println("\n  <cache key=\"" + cacheKey + "\" />");

        // system libraries that must be loaded first of all...
        if(systemLibs.equals(SYSLIB_AUTO)) {
            if(runtimeLibs.size() > 0)
                writer.println("\n  <!-- Runtime libraries, loaded automatically -->");
            for(String rt : runtimeLibs) {
                writer.println("  <library name=\"" + rt + "\" load=\"yes\" />");
            }
        }

        writer.println("\n  <!-- Qt libraries -->");
        for(LibraryEntry e : libs) {
            String libraryName = e.getName();
            String subdir = e.getSubdir();
            String destSubDir = e.getDestSubdir();
            String load = e.getLoad();

            writer.print("  <library name=\"" + pathCanon(new String[] { destSubDir, subdir, libraryName }) + "\"");
            if(!load.equals(LibraryEntry.LOAD_DEFAULT))
                writer.print(" load=\"" + load + "\"");
            writer.println("/>");
        }

        // Manifests and the like...
        if(systemLibs.equals(SYSLIB_AUTO)) {
            if(unpackLibs.size() > 0)
                writer.println("\n  <!-- Dependency libraries, not loaded... -->");
            for(String unpack : unpackLibs) {
                writer.println("  <library name=\"" + unpack + "\" load=\"never\" />");
            }
        }

        // plugins...
        if(pluginPaths.size() > 0) {
            writer.println("\n  <!-- Plugins... -->");
            for(PluginPath p : pluginPaths) {
                writer.println("  <plugin path=\"" + p.getPath() + "\" />");
            }
        }
        // designer plugins...
        if(pluginDesignerPaths.size() > 0) {
            writer.println("\n  <!-- Designer Plugins... -->");
            for(PluginDesignerPath p : pluginDesignerPaths) {
                writer.println("  <plugin-designer path=\"" + p.getPath() + "\" />");
            }
        }

        writer.println("\n</qtjambi-deploy>");

        writer.close();
    }

    private String pathCanon(String[] sA) {
        if(sA == null)
            return null;
        StringBuffer sb = new StringBuffer();
        for(String s : sA) {
            if(s == null)
                continue;
            // Split by "/" since this is a path expressed in XML
            String[] ssA = s.split("/");
            for(String ss : ssA) {
                if(ss.length() == 0)
                    continue;
                if(sb.length() > 0)
                    sb.append("/");
                sb.append(ss);
            }
        }
        return sb.toString();
    }

    private void processLibraryEntry(LibraryEntry e) {
        File rootPath = null;
        String libraryName = null;
        String subdir = null;
        String destSubdir = null;
        String outputPath = null;
        File srcDir = null;
        File destDir = null;
        File srcFile = null;
        File destFile = null;
        try {
            rootPath = e.getRootpath();
            if(rootPath == null)
                rootPath = new File(".");
            libraryName = e.getName();
            subdir = e.getSubdir();
            destSubdir = e.getDestSubdir();

            if(destSubdir != null) {
                if(destSubdir.startsWith("/")) {   // no subdir
                    outputPath = pathCanon(new String[] { destSubdir });
                } else {
                   outputPath = pathCanon(new String[] { destSubdir, subdir });
                }
            } else {
                outputPath = pathCanon(new String[] { subdir });
            }
            if(subdir != null)
                srcDir = new File(rootPath, subdir);
            else
                srcDir = rootPath;
            destDir = new File(outdir, outputPath);
            if(!destDir.exists()) {
                System.out.println("   mkdir " + destDir.getAbsolutePath());
                destDir.mkdir();
            }
            srcFile = new File(srcDir, libraryName);
            destFile = new File(destDir, libraryName);
            try {
                //System.out.println("Copying " + src + " to " + dest);
                Util.copy(srcFile, destFile);
                libraryDir.add(outputPath);
           } catch(IOException ex) {
                ex.printStackTrace();
                throw new BuildException("Failed to copy library '" + libraryName + "'");
            }
        } catch(Exception ex) {
            StringBuffer sb = new StringBuffer("DIAGNOSTIC");
            if(rootPath != null)
                sb.append("; rootPath=" + rootPath.getAbsolutePath());
            if(libraryName != null)
                sb.append("; libraryName=" + libraryName);
            if(subdir != null)
                sb.append("; subdir=" + subdir);
            if(destSubdir != null)
                sb.append("; destSubdir=" + destSubdir);
            if(outputPath != null)
                sb.append("; outputPath=" + outputPath);
            if(srcDir != null)
                sb.append("; srcDir=" + srcDir.getAbsolutePath());
            if(destDir != null)
                sb.append("; destDir=" + destDir.getAbsolutePath());
            if(srcFile != null)
                sb.append("; srcFile=" + srcFile.getAbsolutePath());
            if(destFile != null)
                sb.append("; destFile=" + destFile.getAbsolutePath());
             ex.printStackTrace();
            throw new BuildException(ex);
        }
    }

    private void processSystemLibs() {
        String compiler = String.valueOf(propertyHelper.getProperty((String) null, InitializeTask.COMPILER));
        FindCompiler.Compiler c = FindCompiler.Compiler.resolve(compiler);

        String vcnumber = null;

        switch(c) {
        // The manifest based ones...
        case MSVC2010:
        case MSVC2010_64:
            if(vcnumber == null)
                vcnumber = "100";
            // fall-thru
        case MSVC2008:
        case MSVC2008_64:
            if(vcnumber == null)
                vcnumber = "90";
            // fall-thru
        case MSVC2005:
        case MSVC2005_64:
            if(vcnumber == null)
                vcnumber = "80";

            if(debugConfiguration) {
                printVisualStudioDebugRuntimeWarning();
                break;
            }
            String vsredistdir = propertyHelper.getProperty((String) null, InitializeTask.VSREDISTDIR).toString();
            if(vsredistdir != null) {
                File crt = new File(vsredistdir, "Microsoft.VC" + vcnumber + ".CRT");

                String files[] = new String[] { "Microsoft.VC" + vcnumber + ".CRT.manifest",
                                            "msvcm" + vcnumber + ".dll",
                                            "msvcp" + vcnumber + ".dll",
                                            "msvcr" + vcnumber + ".dll"
                                            };

                for(String libDir : libraryDir) {
                    for(String name : files) {
                        String libdirstring;
                        if("".equals(libDir)) {
                            libdirstring = "lib/";
                        } else {
                            libdirstring = libDir;
                            if(!libdirstring.endsWith("/"))
                                libdirstring += "/";
                        }
                        String lib = libdirstring + "Microsoft.VC" + vcnumber + ".CRT/" + name;
                        unpackLibs.add(lib);

                        try {
                            Util.copy(new File(crt, name), new File(outdir, lib));
                        } catch(Exception e) {
                            e.printStackTrace();
                            throw new BuildException("Failed to copy VS CRT libraries", e);
                        }
                    }
                }
            } else {
                System.err.println("WARNING: " + InitializeTask.VSREDISTDIR + " property not set; skipping packaging of Visual C redistributable components.");
            }
            break;

        case MSVC1998:
            if(debugConfiguration) {
                printVisualStudioDebugRuntimeWarning();
                break;
            }
            copyRuntime("msvcr60.dll");
            copyRuntime("msvcp60.dll");
            break;

        case MSVC2002:
            if(debugConfiguration) {
                printVisualStudioDebugRuntimeWarning();
                break;
            }
            copyRuntime("msvcr70.dll");
            copyRuntime("msvcp70.dll");
            break;

        case MSVC2003:
            if(debugConfiguration) {
                printVisualStudioDebugRuntimeWarning();
                break;
            }
            copyRuntime("msvcr71.dll");
            copyRuntime("msvcp71.dll");
            break;

        case MinGW:
            // This is auto-detected and emitted in the descriptor now
            break;

        case GCC:
            // This is auto-detected and emitted in the descriptor now
            break;

        case OldGCC:
            // This is auto-detected and emitted in the descriptor now
            break;
        }

        // TODO: Make this an arbitrary list of files and provide helper options to
        //  populate with Unix libstdc++.so.5/libstdc++.so.6 values.  Allow each value
        //  to be a full-path to file, filename.
        String cplusplusRuntime = (String) propertyHelper.getProperty((String) null, InitializeTask.PACKAGING_DSO_CPLUSPLUSRUNTIME);
        if(cplusplusRuntime != null)
            copyRuntime(cplusplusRuntime);

    }

    private void copyRuntime(String name) {
        File rt = Util.findInLibraryPath(name, javaLibDir);
        if(rt == null) {
            throw new BuildException("Runtime library '" + name + "' was not found in library path...");
        }

        try {
            //System.out.println("Copying " + rt.toString() + " to " + "lib/" + outdir + ", " + name);
            /*
             * "lib" is somewhat of a hack to specify where the files should be copied to.
             */
            Util.copy(rt, new File(outdir + "/lib",  name));
            runtimeLibs.add("lib/" + name);
        } catch(IOException e) {
            e.printStackTrace();
            throw new BuildException("Failed to copy runtime library...", e);
        }
    }

    private void printVisualStudioDebugRuntimeWarning() {
        System.out.println();
        System.out.println("************************************************************************");
        System.out.println();
        System.out.println("                              WARNING");
        System.out.println();
        System.out.println("The debug runtimes for Visual Studio are not available for");
        System.out.println("redistribution by Microsoft, so it is not possible to create a");
        System.out.println("platform archive that runs on other machines...");
        System.out.println();
        System.out.println("************************************************************************");
        System.out.println();
        System.out.println();

    }

    private void processOSXInstallName() {
        System.out.println("Processing Mac OS X install_name...");

        String cmd[] = new String[] {
            "install_name_tool",
            "-change",
            null,       // Old install name
            null,       // new install name
            null        // library to update...
        };

        for(LibraryEntry with : libs) {
            if(LibraryEntry.TYPE_PLUGIN.equals(with.getType()))
                continue;

            System.out.println(" - updating: " + with.getName());

            for(LibraryEntry change : libs) {
                String changeSubdir = change.getSubdir();
                StringBuilder builder = createDotDots(changeSubdir);
                String destSubdir = with.getDestSubdir();
                if(destSubdir != null)
                    builder.append(destSubdir);
                String withSubdir = with.getSubdir();
                if(withSubdir == null)
                    withSubdir = "";
                builder.append(withSubdir);
                builder.append("/");
                builder.append(with.getName());
                builder.insert(0, "@loader_path/");

                cmd[3] = builder.toString();
                cmd[4] = pathCanon(new String[] { destSubdir, changeSubdir, change.getName() }); //change.relativePath();

                // only name, when Qt is configured with -no-rpath
                cmd[2] = with.getName();

                Exec.exec(cmd, outdir, getProject(), false);

                // CHECKME: Is this needed since we started to use soname.major when deploying ?
                // full path, when Qt is configured with rpath
                if("libqtjambi.jnilib".equals(with.getName()))
                    cmd[2] = "libqtjambi.1.jnilib";
                else
                    cmd[2] = with.absoluteSourcePath();
                Exec.exec(cmd, outdir, getProject(), false);
            }
        }
    }

    /**
      * Add ../ for every / in path to StringBuilder and return it.
      * @param path Path path to parse
      * @return StringBuilder to return
      */
    private static StringBuilder createDotDots(String path) {
        if(path == null)
            path = "";
        int subdir = path.split("/").length;

        StringBuilder builder = new StringBuilder(subdir * 3);
        for(int i = 0; i < subdir; ++i)
            builder.append("../");
        return builder;
    }
}
