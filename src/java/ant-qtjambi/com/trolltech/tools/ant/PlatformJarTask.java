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

import org.apache.tools.ant.*;

import com.trolltech.qt.internal.OSInfo;

import java.util.*;
import java.io.*;

//NOTE: remove this after removing support for 1.7
@SuppressWarnings ( "deprecation" )
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
    private boolean debugConfiguration      = false;
    private String javaLibDir               = "";

    private boolean rpath = true;

    private PropertyHelper props;

    public void addConfiguredLibrary ( LibraryEntry task ) {
        try {
            if ( !task.isIncluded() )
                return;
            task.perform();
            libs.add ( task );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new BuildException ( "Failed to add library entry....." );
        }
    }

    public void addConfiguredPlugin ( PluginPath path ) {
        try {
            path.perform();
            pluginPaths.add ( path );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new BuildException ( "Failed to add plugin path......." );
        }
    }

    public void setSyslibs ( String s ) {
        if ( OSInfo.os() == OSInfo.OS.Solaris )
            return;
        if ( s.equals ( SYSLIB_NONE ) || s.equals ( SYSLIB_AUTO ) )
            systemLibs = s;
        else
            throw new BuildException ( "Bad 'syslibs' parameter... Only 'auto' or 'none' available, was " + s );
    }

    public String getSyslibs() {
        return systemLibs;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey ( String cacheKey ) {
        this.cacheKey = cacheKey;
    }

    public File getOutdir() {
        return outdir;
    }


    public void setOutdir ( File outdir ) {
        this.outdir = outdir;
    }

    public void setRpathenabled ( boolean iname ) {
        rpath = iname;
    }

    public boolean getRpathenabled() {
        return rpath;
    }

    public void execute() throws BuildException {
        try {
            execute_internal();
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new BuildException ( "Failed to create native .jar", e );
        }
    }

    public void execute_internal() throws BuildException {
        props = PropertyHelper.getPropertyHelper ( getProject() );

        javaLibDir = ( String ) props.getProperty ( ( String ) null, InitializeTask.JAVALIBDIR );

        debugConfiguration = "debug".equals ( props.getProperty ( ( String ) null, InitializeTask.CONFIGURATION ) );

        if ( outdir == null ) {
            throw new BuildException ( "Missing required attribute 'outdir'. " +
            		"This directory is used for building the .jar file..." );
        }

        if ( outdir.exists() ) {
            outdir.delete();
        }

        outdir.mkdirs();

    for ( LibraryEntry e : libs ) {
            processLibraryEntry ( e );
        }

        if ( systemLibs.equals ( SYSLIB_AUTO ) )
            processSystemLibs();

        if ( rpath ) {
            if ( OSInfo.os() == OSInfo.OS.MacOS ) {
                processOSXInstallName();
            }
        }

        writeQtJambiDeployment();

    }

    private void writeQtJambiDeployment() {
        PrintWriter writer;
        try {
            writer = new PrintWriter ( new BufferedWriter (
            		new FileWriter ( new File ( outdir, "qtjambi-deployment.xml" ) ) ) );
        } catch ( IOException e ) {
            e.printStackTrace();
            throw new BuildException("Failed to open 'qtjambi-deployment.xml' for writing in '" + outdir + "'");
        }

        writer.println ( "<qtjambi-deploy"
                            + " system=\"" + props.getProperty ( ( String ) null,
                            		InitializeTask.OSNAME ).toString()
                            + "\">" );
        writer.println ( "\n  <cache key=\"" + cacheKey + "\" />" );

        // system libraries that must be loaded first of all...
        if ( systemLibs.equals ( SYSLIB_AUTO ) ) {
            if ( runtimeLibs.size() > 0 )
                writer.println ( "\n  <!-- Runtime libraries, loaded automatically -->" );
        for ( String rt : runtimeLibs ) {
                writer.println ( "  <library name=\"" + rt + "\" load=\"yes\" />" );
            }
        }

        writer.println ( "\n  <!-- Qt libraries -->" );
    for ( LibraryEntry e : libs ) {
            String libraryName = e.getName();
            String subdir = e.getSubdir();
            String load = e.getLoad();

            if ( !"".equals ( subdir ) ) {
                subdir = subdir + "/";
            } else {
                subdir = "";
            }

            writer.print ( "  <library name=\"" + e.output_directory + subdir + libraryName + "\"" );
            if ( !load.equals ( LibraryEntry.LOAD_DEFAULT ) )
                writer.print ( " load=\"" + load + "\"" );
            writer.println ( "/>" );
        }

        // Manifests and the like...
        if ( systemLibs.equals ( SYSLIB_AUTO ) ) {
            if ( unpackLibs.size() > 0 )
                writer.println ( "\n  <!-- Dependency libraries, not loaded... -->" );
        for ( String unpack : unpackLibs ) {
                writer.println ( "  <library name=\"" + unpack + "\" load=\"never\" />" );
            }
        }

        // plugins...
        if ( pluginPaths.size() > 0 ) {
            writer.println ( "\n  <!-- Plugins... -->" );
        for ( PluginPath p : pluginPaths ) {
                writer.println ( "  <plugin path=\"" + p.getPath() + "\" />" );
            }
        }

        writer.println ( "\n</qtjambi-deploy>" );

        writer.close();
    }

    private void processLibraryEntry ( LibraryEntry e ) {
        File rootPath = e.getRootpath();
        String libraryName = e.getName();
        String subdir = e.getSubdir();
        //System.out.println("nyaa " + rootPath.toString() + " " + subdir + " " + libraryName);

        if ( !"".equals ( subdir ) ) {
            subdir = subdir + "/";
        } else {
            subdir = "";
        }
        if ( !"".equals ( e.output_directory ) ) {
            new File ( e.output_directory ).mkdir();
        }
        File src = new File ( rootPath, subdir + libraryName );
        File dest = new File ( outdir, e.output_directory + subdir + libraryName );
        try {
            //System.out.println("Copying " + src + " to " + dest);
            Util.copy ( src, dest );
            libraryDir.add ( subdir );
        } catch ( IOException ex ) {
            ex.printStackTrace();
            throw new BuildException ( "Failed to copy library '" + libraryName + "'" );
        }
    }

    private void processSystemLibs() {
        String compiler = String.valueOf ( props.getProperty ( ( String ) null, InitializeTask.COMPILER ) );
        FindCompiler.Compiler c = FindCompiler.Compiler.resolve ( compiler );

        String vcnumber = "80";

        switch ( c ) {

            // The manifest based ones...
        case MSVC2008:
        case MSVC2008_64:
            vcnumber = "90";

        case MSVC2005:
        case MSVC2005_64:
            if ( debugConfiguration ) {
                printVisualStudioDebugRuntimeWarning();
                break;
            }

            File crt = new File ( props.getProperty ( ( String ) null, InitializeTask.VSREDISTDIR ).toString(),
                                    "Microsoft.VC" + vcnumber + ".CRT" );

            String files[] = new String[] { "Microsoft.VC" + vcnumber + ".CRT.manifest",
                                            "msvcm" + vcnumber + ".dll",
                                            "msvcp" + vcnumber + ".dll",
                                            "msvcr" + vcnumber + ".dll"
                                            };

        for ( String libDir : libraryDir ) {
            for ( String name : files ) {
            	String libdirstring;
            	if("".equals(libDir)) {
            		libdirstring = "lib/";
            	} else {
            		libdirstring = libDir + "/";
            	}
            	String lib = libdirstring + "Microsoft.VC" + vcnumber + ".CRT/" + name;
            	unpackLibs.add ( lib );

            	try {
            		Util.copy ( new File ( crt, name ), new File ( outdir, lib ) );
            	} catch ( Exception e ) {
            		e.printStackTrace();
            		throw new BuildException ( "Failed to copy VS CRT libraries", e );
            	}
            }
        }

            break;

        case MSVC1998:
            if ( debugConfiguration ) {
                printVisualStudioDebugRuntimeWarning();
                break;
            }
            copyRuntime ( "msvcr60.dll" );
            copyRuntime ( "msvcp60.dll" );
            break;

        case MSVC2002:
            if ( debugConfiguration ) {
                printVisualStudioDebugRuntimeWarning();
                break;
            }
            copyRuntime ( "msvcr70.dll" );
            copyRuntime ( "msvcp70.dll" );
            break;

        case MSVC2003:
            if ( debugConfiguration ) {
                printVisualStudioDebugRuntimeWarning();
                break;
            }
            copyRuntime ( "msvcr71.dll" );
            copyRuntime ( "msvcp71.dll" );
            break;

        case MinGW:
            copyRuntime ( "mingwm10.dll" );
            copyAdditionalMingwFiles();
            break;

        case GCC:
            if ( OSInfo.os() == OSInfo.OS.Linux ) copyRuntime ( "libstdc++.so.6" );
            break;

        case OldGCC:
            if ( OSInfo.os() == OSInfo.OS.Linux ) copyRuntime ( "libstdc++.so.5" );
            break;
        }

    }

    /**
     * Copy shared linking library for MinGW.
     * TODO: This whole class could be better factored...
     */
    private void copyAdditionalMingwFiles() {
        copyRuntime ( "libgcc_s_dw2-1.dll" );
        copyRuntime ( "libstdc++-6.dll" );
    }

    private void copyRuntime ( String name ) {
        File rt = Util.findInLibraryPath ( name, javaLibDir );
        if ( rt == null ) {
            throw new BuildException ( "Runtime library '" + name + "' was not found in library path..." );
        }

        try {
            //System.out.println("Copying " + rt.toString() + " to " + "lib/" + outdir + ", " + name);
            /*
                * "lib" is somewhat of a hack to specify where the files should be copied to.
                */
            Util.copy ( rt, new File ( outdir + "/lib",  name ) );
            runtimeLibs.add ( "lib/" + name );
        } catch ( IOException e ) {
            e.printStackTrace();
            throw new BuildException ( "Failed to copy runtime library...", e );
        }
    }

    private void printVisualStudioDebugRuntimeWarning() {
        System.out.println();
        System.out.println ( "************************************************************************" );
        System.out.println();
        System.out.println ( "                              WARNING" );
        System.out.println();
        System.out.println ( "The debug runtimes for Visual Studio are not available for" );
        System.out.println ( "redistribution by Microsoft, so it is not possible to create a" );
        System.out.println ( "platform archive that runs on other machines..." );
        System.out.println();
        System.out.println ( "************************************************************************" );
        System.out.println();
        System.out.println();

    }

    private void processOSXInstallName() {
        System.out.println ( "Processing Mac OS X install_name..." );

        String cmd[] = new String[] {
            "install_name_tool",
            "-change",
            null,       // Old install name
            null,       // new install name
            null        // library to update...
        };

    for ( LibraryEntry with : libs ) {

            if ( LibraryEntry.TYPE_PLUGIN.equals ( with.getType() ) )
                continue;

            System.out.println ( " - updating: " + with.getName() );

        for ( LibraryEntry change : libs ) {
                StringBuilder builder = createDotDots ( change.getSubdir() );
                builder.append ( with.output_directory);
                builder.append ( with.getSubdir() );
		builder.append ("/");
                builder.append ( with.getName() );
                builder.insert ( 0, "@loader_path/" );

                cmd[3] = builder.toString();
                cmd[4] = change.output_directory + change.getSubdir() + "/" + change.getName();//change.relativePath();

                // only name, when Qt is configured with -no-rpath
                cmd[2] = with.getName();

                Exec.exec ( cmd, outdir, false );

                // full path, when Qt is configured with rpath
                if ( "libqtjambi.jnilib".equals ( with.getName() ) )
                    cmd[2] = "libqtjambi.1.jnilib";
                else
                    cmd[2] = with.absoluteSourcePath();
                Exec.exec ( cmd, outdir, false );
            }
        }
    }

    /**
        * Add ../ for every / in path to StringBuilder and return it.
        * @param path Path path to parse
        * @return StringBuilder to return
        */
    private static StringBuilder createDotDots ( String path ) {
        int subdir = path.split ( "/" ).length;

        StringBuilder builder = new StringBuilder ( subdir * 3 );
        for ( int i = 0; i < subdir; ++i )
            builder.append ( "../" );
        return builder;
    }

}

