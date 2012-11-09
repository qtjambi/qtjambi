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
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;

import com.trolltech.qt.osinfo.OSInfo;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
    private List<Directory> directoryList   = new ArrayList<Directory>();
    private Boolean debug                   = null;
    private String javaLibDir               = "";

    private boolean rpath = true;
    private String execStrip;

    private PropertyHelper propertyHelper;

    public void addConfiguredLibrary(LibraryEntry task) {
        try {
            task.setDebug(debug);
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
            path.setDebug(debug);
            path.perform();
            pluginPaths.add(path);
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException("Failed to add plugin path.......");
        }
    }

    public void addConfigured(PluginDesignerPath path) {
        try {
            path.setDebug(debug);
            path.perform();
            pluginDesignerPaths.add(path);
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException("Failed to add plugin-designer path.......");
        }
    }

    public void addConfigured(Directory directory) {
        try {
            directory.perform();
            directoryList.add(directory);
        } catch(Exception e) {
            e.printStackTrace();
            throw new BuildException("Failed to add directory sub-element.......");
        }
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

    public Boolean getDebug() {
        return debug;
    }
    public void setDebug(Boolean debug) {
        this.debug = debug;
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

        javaLibDir = AntUtil.getPropertyAsString(propertyHelper, Constants.JAVALIBDIR);

        if(debug == null) {
            if(Constants.CONFIG_DEBUG.equals(AntUtil.getPropertyAsString(propertyHelper, Constants.CONFIGURATION)))
                debug = Boolean.TRUE;
            else
                debug = Boolean.FALSE;
        }

        execStrip = AntUtil.getPropertyAsString(propertyHelper, Constants.EXEC_STRIP);

        if(outdir == null) {
            throw new BuildException("Missing required attribute 'outdir'. " +
                    "This directory is used for building the .jar file...");
        }

        if(outdir.exists()) {
            outdir.delete();
        }

        outdir.mkdirs();

        Iterator<Directory> it = directoryList.iterator();
        while(it.hasNext()) {
            Directory d = it.next();
            if(processDirectory(d) == false)
                it.remove();    // was found to be inhibited
        }

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

    // Not sure we needed pathFragment, since it is only used by toplevel and it is always null for that.  Ah yes for indent now.
    private void writeDirectoryElementRecurse(PrintWriter writer, Directory root, String pathFragment, Dirent dirent) {
        if(dirent == null)
            return;

        int indentCount = 0;
        {   // Pretty XML
            if(pathFragment != null) {
                String[] pfA = pathFragment.split("/");
                if(pfA != null)
                    indentCount += pfA.length;
            }
        }
        for(int i = indentCount; i > 0; i--)
            writer.print("  ");
        if(dirent.isDirectory()) {
            if(root == dirent)  // Top  level include full path from getDestSubDir()
                writer.println("  <directory name=\"" + xmlEscape(Util.pathCanon(new String[] { root.getDestSubdir(), pathFragment, dirent.getName() }, "/")) + "\">");
            else
                writer.println("  <directory name=\"" + xmlEscape(Util.pathCanon(new String[] { dirent.getName() }, "/")) + "\">");
        } else {
            writer.println("  <file name=\"" + xmlEscape(Util.pathCanon(new String[] { dirent.getName() }, "/")) + "\"/>");
        }

        List<Dirent> list = dirent.getChildList();
        if(list != null) {
            String subPathFragment = pathFragment;
            if(subPathFragment != null && subPathFragment.length() > 0)
                subPathFragment += "/";
            if(subPathFragment == null)
                subPathFragment = "";   // stops += appending "null" first
            subPathFragment += dirent.getName();

            for(Dirent d : list)
                writeDirectoryElementRecurse(writer, root, subPathFragment, d);
        }

        if(dirent.isDirectory()) {
            for(int i = indentCount; i > 0; i--)
                writer.print("  ");
            writer.println("  </directory>");
        }
    }

    private void writeDirectoryElementTree(PrintWriter writer, List<Directory> list) {
        if(list.size() > 0) {
            writer.println();
            writer.println("  <!-- Directory -->");
        }
        for(Directory root : list)
            writeDirectoryElementRecurse(writer, root, null, root);
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
                            + xmlEscape(AntUtil.getPropertyAsString(propertyHelper, Constants.OSNAME).toString()) + "\">");
        writer.println();
        writer.println("  <cache key=\"" + xmlEscape(cacheKey) + "\"/>");

        writeDirectoryElementTree(writer, directoryList);

        // system libraries that must be loaded first of all...
        if(systemLibs.equals(SYSLIB_AUTO)) {
            if(runtimeLibs.size() > 0) {
                writer.println();
                writer.println("  <!-- Runtime libraries, loaded automatically -->");
            }
            for(String rt : runtimeLibs) {
                writer.println("  <library name=\"" + xmlEscape(rt) + "\" load=\"yes\"/>");
            }
        }

        writer.println();
        writer.println("  <!-- Qt libraries -->");
        for(LibraryEntry e : libs) {
            String resolvedName = e.getResolvedName();
            String subdir = e.getSubdir();
            String destSubdir = e.getDestSubdir();
            String load = e.getLoad();

            writer.print("  <library name=\"" + xmlEscape(Util.pathCanon(new String[] { destSubdir, subdir, resolvedName }, "/")) + "\"");
            if(!load.equals(LibraryEntry.LOAD_DEFAULT))
                writer.print(" load=\"" + xmlEscape(load) + "\"");
            writer.println("/>");
        }

        // Manifests and the like...
        if(systemLibs.equals(SYSLIB_AUTO)) {
            if(unpackLibs.size() > 0) {
                writer.println();
                writer.println("  <!-- Dependency libraries, not loaded... -->");
            }
            for(String unpack : unpackLibs) {
                writer.println("  <library name=\"" + xmlEscape(unpack) + "\" load=\"never\"/>");
            }
        }

        // plugins...
        if(pluginPaths.size() > 0) {
            writer.println();
            writer.println("  <!-- Plugins... -->");
            for(PluginPath p : pluginPaths) {
                writer.println("  <plugin path=\"" + xmlEscape(p.getPath()) + "\"/>");
            }
        }
        // designer plugins...
        if(pluginDesignerPaths.size() > 0) {
            writer.println();
            writer.println("  <!-- Designer Plugins... -->");
            for(PluginDesignerPath p : pluginDesignerPaths) {
                writer.println("  <plugin-designer path=\"" + xmlEscape(p.getPath()) + "\"/>");
            }
        }

        writer.println();
        writer.println("</qtjambi-deploy>");

        writer.close();
    }

    // This copies one whole directory but does not recurse
    private List<String> processDirectoryOne(File srcDir, File destDir, Set<String> skipSet) throws IOException {
        List<String> dirNameList = new ArrayList<String>();
        // find files (ignore those specified in childList)
        File[] fileA = srcDir.listFiles();
        for(File f : fileA) {
            String name = f.getName();
            if(skipSet != null && skipSet.contains(name))
                continue;
            File thisSrcFile = new File(srcDir, name);
            File thisDestFile = new File(destDir, name);
            if(f.isDirectory())
                dirNameList.add(name);
            else
                Util.copy(thisSrcFile, thisDestFile);
        }
        for(String name : dirNameList) {
            File thisDestFile = new File(destDir, name);
            if(thisDestFile.exists() == false)
                thisDestFile.mkdir();
        }
        return dirNameList;
    }

    // recurseDepth = -1 (infinite), 0 only files of given directory, 1 recurse once
    private void processDirectoryInternal(File srcDir, File destDir, int recurseDepth, Set<String> skipSet) throws IOException {
        if(recurseDepth > 0)
            recurseDepth--;

        List<String> dirNameList = processDirectoryOne(srcDir, destDir, skipSet);

        if(recurseDepth == 0)
            return;
        for(String name : dirNameList) {
            File thisSrcFile = new File(srcDir, name);
            File thisDestFile = new File(destDir, name);
            processDirectoryInternal(thisSrcFile, thisDestFile, recurseDepth, null);
        }
    }

    // recurseDepth = -1 (infinite), 0 only files of given directory, 1 recurse once
    private void processDirentInternal(File srcDir, File destDir, int recurseDepth, Directory parent) throws IOException {
        if(recurseDepth > 0)
            recurseDepth--;

        // FIXME: Manage Dirent parent and refactor main code below to use this
        List<String> dirNameList = processDirectoryOne(srcDir, destDir, null);

        if(recurseDepth == 0)
            return;
        for(String name : dirNameList) {
            File thisSrcFile = new File(srcDir, name);
            File thisDestFile = new File(destDir, name);
            processDirectoryInternal(thisSrcFile, thisDestFile, recurseDepth, null);
        }
    }

    private Directory buildDirentDirectory(Directory parent, String rootPath, String name) {
        String thisRootPath = rootPath + "/" + name;
        Directory direntDirectory = new Directory(thisRootPath, name);
        if(parent.getChild(name) != null)
            throw new RuntimeException("duplicate name=" + name);   // should never happen
        parent.getChildList().add(direntDirectory);
        return direntDirectory;
    }

    private com.trolltech.tools.ant.File buildDirentFile(Directory parent, String name) {
        com.trolltech.tools.ant.File direntFile = new com.trolltech.tools.ant.File(name);
        if(parent.getChild(name) != null)
            throw new RuntimeException("duplicate name=" + name);   // should never happen
        parent.getChildList().add(direntFile);
        return direntFile;
    }

    private void buildDirentDirectoryRecurse(Directory parent, File dirParent, String rootPath, String name) {
        Directory subDirectory = buildDirentDirectory(parent, rootPath, name);

        File[] subFileA = dirParent.listFiles();
        for(File f : subFileA) {
            if(f.isDirectory()) {
                File x = new File(dirParent, f.getName());
                buildDirentDirectoryRecurse(subDirectory, x, rootPath, f.getName());   // recursive
            } else {
                com.trolltech.tools.ant.File direntFile = buildDirentFile(subDirectory, f.getName());
            }
        }
    }

    private boolean processDirectory(Directory d) {
        boolean rv = false;
        File rootPathFile = null;
        String rootPath = null;
        String toplevelName = null;
        String subdir = null;
        String destSubdir = null;
        String outputPath = null;
        boolean recursive = false;
        File srcDir = null;
        File destDir = null;
        File srcTarget = null;
        File destTarget = null;
        try {
            rootPath = d.getRootPath();
            toplevelName = d.getName();
            if(rootPath == null) {
                if(toplevelName == null || toplevelName.length() == 0)
                    throw new IllegalArgumentException("name must be set, when rootPath is not set; name=" + toplevelName);
                rootPathFile = new File(".");
            } else if(rootPath.length() == 0) {
                return rv;  // skip (and have caller remove)
            } else {
                rootPathFile = new File(rootPath);
            }
            if(toplevelName == null || toplevelName.length() == 0) {
                toplevelName = rootPathFile.getName();
                rootPathFile = new File(rootPathFile.getAbsolutePath()).getParentFile();  // converts relative to absolute
                rootPath = rootPathFile.getAbsolutePath();

                // FIXUP the top level so qtjambi-descriptor.xml is emitted correctly
                d.setRootPath(rootPath);
                d.setName(toplevelName);
            }
            //getProject().log(this, "   rootPath " + rootPath, Project.MSG_VERBOSE);
            //getProject().log(this, "       name " + toplevelName, Project.MSG_VERBOSE);
            subdir = d.getSubdir();
            destSubdir = d.getDestSubdir();
            recursive = d.getRecursive();

            if(destSubdir != null) {
                if(destSubdir.startsWith("/")) {   // no subdir
                    outputPath = Util.pathCanon(new String[] { destSubdir }, "/");
                } else {
                    outputPath = Util.pathCanon(new String[] { destSubdir, subdir }, "/");
                }
            } else {
                outputPath = Util.pathCanon(new String[] { subdir }, "/");
            }
            if(subdir != null)
                srcDir = new File(rootPathFile, subdir);
            else
                srcDir = rootPathFile;
            destDir = new File(outdir, outputPath);
            if(!destDir.exists()) {
                //getProject().log(this, "   mkdir " + destDir.getAbsolutePath(), Project.MSG_VERBOSE);
                destDir.mkdir();
            }
            srcTarget = new File(srcDir, toplevelName);
            destTarget = new File(destDir, toplevelName);
            if(srcTarget.isDirectory()) {
                if(!destTarget.exists()) {
                    //getProject().log(this, "   mkdir " + destTarget.getAbsolutePath(), Project.MSG_VERBOSE);
                    destTarget.mkdir();
                }

                List<Directory> direntList = new ArrayList<Directory>();
                List<String> filenameList = new ArrayList<String>();
                List<String> dirnameList = new ArrayList<String>();

                File[] fileA = srcTarget.listFiles();
                for(File f : fileA) {
                    // do files
                    String name = f.getName();
                    Dirent child = d.getChild(name);
                    if(child == null || child.isDirectory() != f.isDirectory()) {
                        File thisSrcFile = new File(srcTarget, name);
                        File thisDestFile = new File(destTarget, name);
                        if(thisSrcFile.isDirectory()) {
                            if(thisDestFile.exists() == false) {
                                //getProject().log(this, "   mkdir " + thisDestFile.getAbsolutePath(), Project.MSG_VERBOSE);
                                thisDestFile.mkdir();
                            }
                            if(recursive)
                                dirnameList.add(name);
                        } else {
                            //getProject().log(this, "Copying " + thisSrcFile + " to " + thisDestFile, Project.MSG_VERBOSE);
                            Util.copy(thisSrcFile, thisDestFile);
                            if(recursive)
                                filenameList.add(name);
                        }
                    } else {
                        if(child.isDirectory()) {
                            File thisDestFile = new File(destTarget, name);
                            if(thisDestFile.exists() == false) {
                                //getProject().log(this, "   mkdir " + thisDestFile.getAbsolutePath(), Project.MSG_VERBOSE);
                                thisDestFile.mkdir();
                            }
                            if(recursive)
                                direntList.add((Directory)child);
                        } else {
                            File thisSrcFile = new File(srcTarget, name);
                            File thisDestFile = new File(destTarget, name);
                            //getProject().log(this, "Copying " + thisSrcFile + " to " + thisDestFile, Project.MSG_VERBOSE);
                            Util.copy(thisSrcFile, thisDestFile);
                        }
                    }
                }
                for(Directory directory : direntList) {
                    String name = directory.getName();
                    File thisSrcFile = new File(srcTarget, name);
                    File thisDestFile = new File(destTarget, name);
                    processDirentInternal(thisSrcFile, thisDestFile, -1, directory);
                }
                for(String name : dirnameList) {
                    File thisSrcFile = new File(srcTarget, name);
                    File thisDestFile = new File(destTarget, name);
                    processDirectoryInternal(thisSrcFile, thisDestFile, -1, null);
                    // We need to make a Dirent for all these nodes
                    buildDirentDirectoryRecurse(d, thisSrcFile, rootPath, name);
                }
                for(String name : filenameList) {
                    com.trolltech.tools.ant.File direntFile = buildDirentFile(d, name);
                }
            } else {
                try {
                    //getProject().log(this, "Copying " + srcTarget + " to " + destTarget, Project.MSG_VERBOSE);
                    Util.copy(srcTarget, destTarget);
                } catch(IOException ex) {
                    ex.printStackTrace();
                    throw new BuildException("Failed to copy file '" + toplevelName + "'");
                }
            }
            rv = true;
        } catch(Exception ex) {
            StringBuilder sb = new StringBuilder("DIAGNOSTIC");
            if(rootPathFile != null)
                sb.append("; rootPathFile=" + rootPathFile.getAbsolutePath());
            if(toplevelName != null)
                sb.append("; toplevelName=" + toplevelName);
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
            if(srcTarget != null)
                sb.append("; srcTarget=" + srcTarget.getAbsolutePath());
            if(destTarget != null)
                sb.append("; destTarget=" + destTarget.getAbsolutePath());
            ex.printStackTrace();
            throw new BuildException(ex);
        }
        return rv;
    }

    private void runBinaryStrip(File file) {
        List<String> list = Util.splitStringTokenizer(execStrip);
        list.add(file.getAbsolutePath());
        String[] cmd = list.toArray(new String[list.size()]);
        getProject().log("Stripping binary: " + Arrays.toString(cmd), Project.MSG_VERBOSE);
        Exec.exec(cmd, null, getProject(), false);
    }

    private void processLibraryEntry(LibraryEntry e) {
        File rootPath = null;
        String libraryName = null;
        String absolutePath = null;
        String resolvedName = null;
        String subdir = null;
        String destSubdir = null;
        String outputPath = null;
        File srcDir = null;
        File destDir = null;
        File srcFile = null;
        File destFile = null;
        try {
            rootPath = e.getRootPath();
            if(rootPath == null)
                rootPath = new File(".");
            libraryName = e.getName();
            absolutePath = e.getAbsolutePath();
            resolvedName = e.getResolvedName();
            subdir = e.getSubdir();
            destSubdir = e.getDestSubdir();

            if(destSubdir != null) {
                if(destSubdir.startsWith("/")) {   // no subdir
                    outputPath = Util.pathCanon(new String[] { destSubdir }, "/");
                } else {
                    outputPath = Util.pathCanon(new String[] { destSubdir, subdir }, "/");
                }
            } else {
                outputPath = Util.pathCanon(new String[] { subdir }, "/");
            }
            if(subdir != null)
                srcDir = new File(rootPath, subdir);
            else
                srcDir = rootPath;
            destDir = new File(outdir, outputPath);
            if(!destDir.exists()) {
                //getProject().log(this, "   mkdir " + destDir.getAbsolutePath(), Project.MSG_VERBOSE);
                destDir.mkdir();
            }
            if(absolutePath == null)
                srcFile = new File(srcDir, libraryName);
            else
                srcFile = new File(absolutePath);
            destFile = new File(destDir, resolvedName);
            try {
                //getProject().log(this, "Copying " + src + " to " + dest, Project.MSG_VERBOSE);
                Util.copy(srcFile, destFile);

                boolean doStrip = true;
                if(e.getType().equals(LibraryEntry.TYPE_QT))
                    doStrip = false;
                else if(e.getType().equals(LibraryEntry.TYPE_PLUGIN))
                    doStrip = false;
                else if(e.getType().equals(LibraryEntry.TYPE_DSO))
                    doStrip = false;
                else if(e.getType().equals(LibraryEntry.TYPE_SYSTEM))
                    doStrip = false;
                else if(e.getType().equals(LibraryEntry.TYPE_UNVERSIONED_PLUGIN))
                    doStrip = false;
                if(doStrip && execStrip != null)
                    runBinaryStrip(destFile);

                libraryDir.add(outputPath);
           } catch(IOException ex) {
                ex.printStackTrace();
                throw new BuildException("Failed to copy library '" + libraryName + "'");
            }
        } catch(Exception ex) {
            StringBuilder sb = new StringBuilder("DIAGNOSTIC");
            if(rootPath != null)
                sb.append("; rootPath=" + rootPath.getAbsolutePath());
            if(libraryName != null)
                sb.append("; libraryName=" + libraryName);
            if(absolutePath != null)
                sb.append("; absolutePath=" + absolutePath);
            if(resolvedName != null)
                sb.append("; resolvedName=" + resolvedName);
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
        String compiler = AntUtil.getPropertyAsString(propertyHelper, Constants.COMPILER);
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

            if(debug) {
                printVisualStudioDebugRuntimeWarning();
                break;
            }
            Object vsredistdirObject = AntUtil.getPropertyAsString(propertyHelper, Constants.VSREDISTDIR);
            if(vsredistdirObject != null) {
                String vsredistdir = vsredistdirObject.toString();
                File crt = new File(vsredistdir, "Microsoft.VC" + vcnumber + ".CRT");

                String files[] = new String[] {
                    "Microsoft.VC" + vcnumber + ".CRT.manifest",
                    "msvcm" + vcnumber + ".dll",    // This is reported to not exist since MSVC2010
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
                        File srcFile = new File(crt, name);
                        if(srcFile.isFile()) {
                            String lib = libdirstring + "Microsoft.VC" + vcnumber + ".CRT/" + name;
                            File destFile = new File(outdir, lib);

                            unpackLibs.add(lib);

                            try {
                                Util.copy(srcFile, destFile);
                            } catch(Exception e) {
                                e.printStackTrace();
                                throw new BuildException("Failed to copy VS CRT libraries", e);
                            }
                        } else {
                            getProject().log(this, "WARNING: " + Constants.VSREDISTDIR + " " + srcFile.getAbsolutePath() + " does not exist; skipping", Project.MSG_WARN);
                        }
                    }
                }
            } else {
                getProject().log(this, "WARNING: " + Constants.VSREDISTDIR + " property not set; skipping packaging of Visual C redistributable components.", Project.MSG_WARN);
            }
            break;

        case MSVC1998:
            if(debug) {
                printVisualStudioDebugRuntimeWarning();
                break;
            }
            copyRuntime("msvcr60.dll");
            copyRuntime("msvcp60.dll");
            break;

        case MSVC2002:
            if(debug) {
                printVisualStudioDebugRuntimeWarning();
                break;
            }
            copyRuntime("msvcr70.dll");
            copyRuntime("msvcp70.dll");
            break;

        case MSVC2003:
            if(debug) {
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
        String cplusplusRuntime = AntUtil.getPropertyAsString(propertyHelper, Constants.PACKAGING_DSO_CPLUSPLUSRUNTIME);
        if(cplusplusRuntime != null)
            copyRuntime(cplusplusRuntime);

    }

    private void copyRuntime(String name) {
        File rt = Util.findInLibraryPath(name, javaLibDir);
        if(rt == null) {
            throw new BuildException("Runtime library '" + name + "' was not found in library path...");
        }

        try {
            //getProject().log(this, "Copying " + rt.toString() + " to " + "lib/" + outdir + ", " + name, Project.MSG_VERBOSE);
            /*
             * "lib" is somewhat of a hack to specify where the files should be copied to.
             */
            Util.copy(rt, new File(outdir + File.separator + "lib", name));
            runtimeLibs.add("lib/" + name);
        } catch(IOException e) {
            e.printStackTrace();
            throw new BuildException("Failed to copy runtime library...", e);
        }
    }

    private void printVisualStudioDebugRuntimeWarning() {
        getProject().log(this, "", Project.MSG_INFO);
        getProject().log(this, "************************************************************************", Project.MSG_INFO);
        getProject().log(this, "", Project.MSG_INFO);
        getProject().log(this, "                              WARNING", Project.MSG_INFO);
        getProject().log(this, "", Project.MSG_INFO);
        getProject().log(this, "The debug runtimes for Visual Studio are not available for", Project.MSG_INFO);
        getProject().log(this, "redistribution by Microsoft, so it is not possible to create a", Project.MSG_INFO);
        getProject().log(this, "platform archive that runs on other machines...", Project.MSG_INFO);
        getProject().log(this, "", Project.MSG_INFO);
        getProject().log(this, "************************************************************************", Project.MSG_INFO);
        getProject().log(this, "", Project.MSG_INFO);
        getProject().log(this, "", Project.MSG_INFO);

    }

    private void processOSXInstallName() {
        getProject().log(this, "Processing Mac OS X install_name...", Project.MSG_INFO);

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

            getProject().log(this, " - updating: " + with.getName(), Project.MSG_INFO);

            for(LibraryEntry change : libs) {
                String changeDestSubdir = change.getDestSubdir();
                String changeSubdir = change.getSubdir();
                StringBuilder builder = createDotDots(Util.pathCanon(new String[] { changeDestSubdir, changeSubdir }, "/"));
                String withDestSubdir = with.getDestSubdir();
                if(withDestSubdir != null)
                    builder.append(withDestSubdir);
                String withSubdir = with.getSubdir();
                if(withSubdir == null)
                    withSubdir = "";
if(false) {
getProject().log(this, " change.Name       =  " + change.getName(), Project.MSG_VERBOSE);
getProject().log(this, " change.Subdir     =  " + changeSubdir, Project.MSG_VERBOSE);
getProject().log(this, " change.DestSubdir =  " + change.getDestSubdir(), Project.MSG_VERBOSE);
getProject().log(this, " with.destSubdir   =  " + withDestSubdir, Project.MSG_VERBOSE);
getProject().log(this, " with.Subdir       =  " + withSubdir, Project.MSG_VERBOSE);
getProject().log(this, " with.Name         =  " + with.getName(), Project.MSG_VERBOSE);
}
                File withTarget = new File(withSubdir, with.getName());
                String targetPath = Util.pathCanon(new String[] { changeDestSubdir, changeSubdir, change.getName() }, "/"); //change.relativePath();
                String resolvedWithSubdir = resolveWithSubdir(builder.toString(), targetPath);
                if(resolvedWithSubdir != null)
                    builder = new StringBuilder(resolvedWithSubdir);
                if(builder.length() > 0)
                    builder.append("/");
                builder.append(with.getName());
                builder.insert(0, "@loader_path/");

                cmd[3] = builder.toString();
                cmd[4] = targetPath;

                // only name, when Qt is configured with -no-rpath
                cmd[2] = with.getName();

//getProject().log(this, " exec " + Arrays.toString(cmd) + " in " + outdir, Project.MSG_VERBOSE);
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
     * Convert: "../lib", "lib/libfoo.dylib" into ""
     * Convert: "../../lib", "/tmp/dir/qtjambi-community/build/platform-output/lib/libfoo.dylib" into ".."
     * @param withSubdir  must be a directory name (do not include any filename part on the end)
     * @param withTarget  the target we are resolving it to
     */
    private static String resolveWithSubdir(String withSubdir, String withTarget) {
        // remove trailing / character
        int len = withSubdir.length();
        int testCharAt = len - 1;
        while(testCharAt >= 0 && withSubdir.charAt(testCharAt) == '/')
            testCharAt--;
        withSubdir = withSubdir.substring(0, testCharAt + 1);   // truncate
//getProject().log(this, " resolveWithSubdir withSubdir=" + withSubdir + " truncated", Project.MSG_VERBOSE);

        String[] withTargetA = withTarget.split("/");
        List<String> withTargetParts = Arrays.asList(withTargetA);
        String[] withSubdirA = withSubdir.split("/");
        List<String> pathParts = Arrays.asList(withSubdirA);
        // Find the last ".." part (one at a time) and try to remove it and its counterpart directory
        int maxIndex = -1;
        while(true) {
            int index = 0;
            int foundDownIndex = -1;
            int upIndex = -1;
            for(String s : pathParts) {
                if(maxIndex >= 0 && maxIndex == index)
                    break;
                if(s.length() > 0) {
                    if(s.equals(".")) {
                        pathParts.set(index, "");  // zap it
                    } else if(s.equals("..")) {
                        foundDownIndex = index;
                        upIndex = -1;
                    } else if(foundDownIndex >= 0 && upIndex < 0) {
                        upIndex = index;
                    }
                }
                index++;
            }
            if(foundDownIndex >= 0 && upIndex >= 0) {
//getProject().log(this, " resolveWithSubdir foundDownIndex=" + foundDownIndex, Project.MSG_VERBOSE);
                String upDir = pathParts.get(upIndex);
//getProject().log(this, " resolveWithSubdir upDir=" + upDir, Project.MSG_VERBOSE);
                int distance = pathParts.size() - upIndex;
                int targetUpIndex = withTargetParts.size() - distance - 1; // -1 due to filename removal
                if(targetUpIndex >= 0) {
//getProject().log(this, " resolveWithSubdir targetUpIndex=" + targetUpIndex, Project.MSG_VERBOSE);
                    String targetUpDir = withTargetParts.get(targetUpIndex);
//getProject().log(this, " resolveWithSubdir targetUpDir=" + targetUpDir, Project.MSG_VERBOSE);

                    if(targetUpDir.equals(upDir)) {
//getProject().log(this, " resolveWithSubdir ZAPPING " + foundDownIndex + " " + upIndex, Project.MSG_VERBOSE);
                        // we do it this way to the indexes don't change throughout
                        //  then remove empty parts before returning at the end
                        pathParts.set(foundDownIndex, "");  // zero-length
                        pathParts.set(upIndex, "");  // zero-length
                    }
                }

                maxIndex = foundDownIndex;   // limit the next pass
            } else {
                break;
            }
        }

        StringBuilder sb = new StringBuilder();
        for(String s : pathParts) {
            if(s.length() == 0)  // removed part
                continue;
            if(sb.length() > 0)
                sb.append("/");
            sb.append(s);
        }
        return sb.toString();
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

    private String xmlEscape(String s) {
        if(s == null)
            return s;
        StringBuilder sb = new StringBuilder();
        final int len = s.length();
        for(int i = 0; i < len; i++) {
            char c = s.charAt(i);
            switch(c) {
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '&':
                sb.append("&amp;");
                break;
            default:
                sb.append(c);
                break;
            }
        }
        return sb.toString();
    }
}
