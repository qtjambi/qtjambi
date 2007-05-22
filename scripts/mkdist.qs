const packageDir = os_name() == OS_NAME_WINDOWS
                   ? "d:/tmp/package-builder"
                   : "/tmp/package-builder";
const version = figureVersion();
const javaDir = packageDir + "/qtjambi/" + version;
const javadocName = "qtjambi-javadoc-" + version + ".jar";
const jdocName = "qtjambi-jdoc-" + version + ".jar";

var command = new Object();
var option = new Object();

option.qtdir = findQtDir();
option.verbose = array_contains(args, "--verbose");
option.startDir = new Dir().absPath;
option.javadocHTTP = array_get_next_value(args, "--javadoc");
option.noJavadocDownload = array_contains(args, "--no-javadoc-download");
option.sourcePackages = !array_contains(args, "--no-source");
option.binaryPackages = !array_contains(args, "--no-binary");
option.evalPackages = !array_contains(args, "--no-eval");
option.qtEvalLocation = array_get_next_value(args, "--qt-eval");
option.qtCommercialLocation = array_get_next_value(args, "--qt-commercial");
option.qtGPLLocation = array_get_next_value(args, "--qt-gpl");
option.platformJar = !array_contains(args, "--no-platform-jar");

command.chmod = find_executable("chmod");
command.cp = find_executable("cp");
command.jar = find_executable("jar");
command.javac = find_executable("javac");
command.mv = find_executable("mv");
command.p4 = find_executable("p4");
command.rm = find_executable("rm");
command.tar = find_executable("tar");
command.zip = find_executable("zip");
command.generator = findGeneratorName();

if (os_name() == OS_NAME_MACOSX)
    command.curl = find_executable("curl");
else
    command.wget = find_executable("wget");

if (!option.javadocHTTP) {
    option.javadocHTTP = "http://anarki/~gunnar/packages";
}

option.javadocLocation = option.javadocHTTP + "/" + javadocName;
option.jdocLocation = option.javadocHTTP + "/" + jdocName;

if (array_contains(args, "-help") || array_contains(args, "-h")) {
    displayHelp();
} else {
    for (var i in option)
        if (option[i])
            verbose("'%1': %2".arg(i).arg(option[i]));
    verbose("");
    for (var i in command)
        verbose("using command '%1' in: %2".arg(i).arg(command[i]));
}

var packages = [];

verbose("Setting up packages...");

if (option.sourcePackages) {
    verbose(" - gpl source");
    packages.push(setupGPLSourcePackage());
    verbose(" - commercial source");
    packages.push(setupCommercialSourcePackage());
}

if (option.binaryPackages) {
    if (!option.qtCommercialLocation) {
        verbose("no --qt-commercial specified, using QTDIR at: " + option.qtdir);
        option.qtCommercialLocation = option.qtdir;
    }

    if (!option.qtCommercialLocation)
        throw "missing '--qt-commercial [location]' for location of binaries";

    verbose(" - gpl binary");
    packages.push(setupGPLBinaryPackage());

    verbose(" - commercial binary");
    packages.push(setupCommercialBinaryPackage());
}

if (option.evalPackages) {

    if (!option.qtEvalLocation)
        throw " missing '--qt-eval [location]' for location of binaries";

    verbose(" - evaluation");
    packages.push(setupEvalPackages());
}

for (var i=0; i<packages.length; ++i)
    createPackage(packages[i]);

/*******************************************************************************
 *
 * Creates and sets up the default package.
 *
 * The default package has the following properties:
 *   - removeDirs               - directories to be removed
 *   - removeFiles              - files to be removed.
 *   - mkdirs                   - Directories to be created
 *   - copyFiles                - Files to be copied. Each entry is either a
 *                                String which is copied to packageroot or an
 *                                array containing ["source", "targetdir"]
 *
 * The other setup functions are expected to add:
 *   - qt                       - [binary] The location of a prebuilt Qt
 *   - license                  - The license type, eval, commercial or gpl
 *   - licenseHeader            - The license header matching 'license'
 *   - packageName              - Name of the package, such as win32
 *   - name                     - the complete name of the package
 *   - binary                   - boolean indicating binary package
 *
 */
function setupDefaultPackage() {
    var pkg = {};

    pkg.removeDirs = [
                      "autotestlib",
                      "com/trolltech/autotests",
                      "com/trolltech/benchmarks",
                      "com/trolltech/extensions",
                      "com/trolltech/tests",
                      "cpp",
                      "dist",
                      "doc/config",
                      "doc/src",
                      "eclipse-integration",
                      "eclipse-stable",
                      "jawt",
                      "launcher_launcher",
                      "libbenchmark",
                      "scripts",
                      "tools",
                      "uic4",
                      "whitepaper"
    ];

    pkg.removeFiles = [
                       "rebuild.bat",
                       "rebuild.sh",
                       "build.xml"
    ];

    pkg.mkdirs = [
                  "include"
    ];

    pkg.copyFiles = [
                     // Include files...
                     ["qtjambi/qtjambi_core.h", "include"],
                     ["qtjambi/qtjambi_cache.h", "include"],
                     ["qtjambi/qtjambi_global.h", "include"],
                     ["qtjambi/qtjambilink.h", "include"],
                     ["qtjambi/qtjambifunctiontable.h", "include"],
                     ["qtjambi/qtjambitypemanager.h", "include"],

                     // text files for main directory...
                     "dist/KNOWN_ISSUES",
                     "dist/README",
                     "dist/changes-" + version
    ];

    pkg.version = version;
    pkg.make = make_from_qmakespec();
    pkg.qmakespec = System.getenv("QMAKESPEC");

    return pkg;
}




/*******************************************************************************
 *
 * Does the final steps of preparing the package...
 *
 */
function finalizeDefaultPackage(pkg) {
    pkg.name = packageName(pkg);
    pkg.qmake = pkg.qt + "/bin/qmake";
}


/*******************************************************************************
 *
 * Modifies a package with the content required for binary packages...
 *
 */
function setupBinaryPackage(pkg) {
    pkg.binary = true;
    pkg.maketool = make_from_qmakespec();
    pkg.preCompileStep = function(pkg) { }
    pkg.postCompileStep = function(pkg) { }

    pkg.removeDirs.push(
                   "com/trolltech/qt",
                   "com/trolltech/tools",
                   "common",
                   "designer-integration",
                   "generator",
                   "juic",
                   "qtjambi",
                   "qtjambi_core",
                   "qtjambi_designer",
                   "qtjambi_generator",
                   "qtjambi_gui",
                   "qtjambi_network",
                   "qtjambi_opengl",
                   "qtjambi_sql",
                   "qtjambi_svg",
                   "qtjambi_xml"
                   );

    pkg.removeFiles.push(
                         "Makefile",
                         "java.pro",
                         "java_files"
                         );



    pkg.copyFiles.push(
                       [command.generator, "bin"]
                       );

    // System libraries...
    if (os_name() == OS_NAME_WINDOWS) {
        try {
            pkg.copyFiles.push([find_executable("msvcr80.dll"), "bin"],
                               [find_executable("msvcp80.dll"), "bin"]);
        } catch (e) {
            pkg.copyFiles.push([find_executable("msvcr71.dll"), "bin"],
                               [find_executable("msvcp71.dll"), "bin"]);
        }
    } else if (os_name() == OS_NAME_LINUX) {
        var locs = ["/lib", "/usr/lib"];
        for (var i=0; i<locs.length; ++i) {
            var name = locs[i] + "/libstdc++.so.5";
            if (File.exists(name)) {
                pkg.copyFiles.push([name, "lib"]);
                break;
            }
        }
    }

    // Qt libraries...
    const qtLibraryNames = [
                            "QtAssistantClient",
                            "QtCore",
                            "QtDesigner",
                            "QtDesignerComponents",
                            "QtGui",
                            "QtNetwork",
                            "QtOpenGL",
                            "QtScript",
                            "QtSql",
                            "QtSvg",
                            "QtXml"
    ];
    const qtBinaryNames = ["designer", "linguist", "lrelease", "lupdate"];

    var isWindows = os_name() == OS_NAME_WINDOWS;

    var exe_extension = "";
    var location = "lib";

    var namePostfix = "";
    var namePrefix = "lib";

    if (os_name() == OS_NAME_WINDOWS) {
        exe_extension = ".exe";
        location = "bin";
        namePostfix = "4.dll";
        namePrefix = "";
    } else if (os_name() == OS_NAME_MACOSX) {
        namePostfix = ".4.dylib";
        exe_extension = ".app";
    } else {
        namePostfix = ".so.4";
    }

    for (var i=0; i<qtLibraryNames.length; ++i) {
        var libName = pkg.qt + "/" + location + "/"
                      + namePrefix + qtLibraryNames[i] + namePostfix;

        if (!File.exists(libName))
            throw "Library '%1' does not exist".arg(libName);

        pkg.copyFiles.push([libName, location]);
    }

    for (var i=0; i<qtBinaryNames.length; ++i) {
        var binName = pkg.qt + "/bin/" + qtBinaryNames[i] +  exe_extension;
        if (!File.exists(binName) && os_name() == OS_NAME_MACOSX)
            binName = pkg.qt + "/bin/" + qtBinaryNames[i];
        if (!File.exists(binName))
            throw "Binary file '%1' does not exist".arg(binName);
        pkg.copyFiles.push([binName, "bin"]);
    }

    var tmp = [];
    var cutLength = pkg.qt.length + 1;
    // Qt plugins...
    for_all_files(pkg.qt + "/plugins", function(name) {
        if (name.endsWith(".so") || name.endsWith(".dll") || name.endsWith(".dylib")) {
            tmp.push(name.mid(cutLength));
        }
    });
    for (var i=0; i<tmp.length; ++i) {
        var n = tmp[i];
        pkg.copyFiles.push([pkg.qt + "/" + n, n]);
        var p = n.split("/");
        p.pop();

        p = p.join("/");

        if (!array_contains(pkg.mkdirs, p)) {
            pkg.mkdirs.push(p);
        }
    }



    if (os_name() == OS_NAME_MACOSX) {
        pkg.copyFiles.push("dist/mac/qtjambi.sh");
        pkg.copyFiles.push(["dist/mac/generator_example.sh", "generator_example"]);
        pkg.copyFiles.push("dist/mac/designer.sh");
    } else if (os_name() == OS_NAME_WINDOWS) {
        pkg.copyFiles.push("dist/win/designer.bat");
        pkg.copyFiles.push("dist/win/qtjambi.exe");
        pkg.copyFiles.push(["dist/win/generator_example.bat", "generator_example"]);
    } else {
        pkg.copyFiles.push("dist/linux/designer.sh");
        pkg.copyFiles.push("dist/linux/qtjambi.sh");
        pkg.copyFiles.push(["dist/linux/generator_example.sh", "generator_example"]);
    }

    if (os_name() == OS_NAME_WINDOWS) {
        var arch = System.getenv("PROCESSOR_ARCHITEW6432");
        if (arch && arch.indexOf("64") >= 0)
            pkg.packageName = "win64";
        else
            pkg.packageName = "win32";
    } else if (os_name() == OS_NAME_LINUX) {
        pkg.packageName = "linux";
    } else {
        pkg.packageName = "mac";
    }
}



/*******************************************************************************
 *
 * Modifies the package with the content required for binary packages...
 *
 */
function setupSourcePackage(pkg) {
    pkg.packageName = "source";
    pkg.binary = false;

    pkg.mkdirs.push(
                    "designer-integration/language/private"
                    );

    var uicPrefix = option.qtdir + "/src/tools/uic/";
    pkg.copyFiles.push(
                       "dist/BUILDING_SOURCE_PACKAGE",

                       // uic files
                       [uicPrefix + "customwidgetsinfo.cpp", "juic"],
                       [uicPrefix + "customwidgetsinfo.h", "juic"],
                       [uicPrefix + "databaseinfo.cpp", "juic"],
                       [uicPrefix + "databaseinfo.h", "juic"],
                       [uicPrefix + "driver.cpp", "juic"],
                       [uicPrefix + "driver.h", "juic"],
                       [uicPrefix + "globaldefs.h", "juic"],
                       [uicPrefix + "option.h", "juic"],
                       [uicPrefix + "treewalker.cpp", "juic"],
                       [uicPrefix + "treewalker.h", "juic"],
                       [uicPrefix + "ui4.cpp", "juic"],
                       [uicPrefix + "ui4.h", "juic"],
                       [uicPrefix + "uic.cpp", "juic"],
                       [uicPrefix + "uic.h", "juic"],
                       [uicPrefix + "uic.pri", "juic"],
                       [uicPrefix + "utils.h", "juic"],
                       [uicPrefix + "validator.cpp", "juic"],
                       [uicPrefix + "validator.h", "juic"],

                       // designer files...
                       [option.qtdir + "/tools/designer/src/lib/uilib/ui4_p.h",
                        "designer-integration/language/private/ui4_p.h"]
                       );

}



/*******************************************************************************
 *
 * Modifies the package with the content required for GPL packages...
 *
 */
function setupGPLPackage(pkg) {
    pkg.licenseHeader = File.read("../dist/gpl_header.txt");
    pkg.license = "gpl";
    pkg.copyFiles.push(
                       "dist/LICENSE.GPL"
                       );
}



/*******************************************************************************
 *
 * Modifies the package with the content required for commercial packages...
 *
 */
function setupCommercialPackage(pkg) {
    pkg.licenseHeader = File.read("../dist/commercial_header.txt");
    pkg.license = "commercial";
    pkg.copyFiles.push(
                       "dist/LICENSE"
                       );
}



/*******************************************************************************
 *
 * Creates and modifies the package with the content that is required for the
 * GPL Source packages
 *
 */
function setupGPLSourcePackage() {
    var pkg = setupDefaultPackage();
    setupSourcePackage(pkg);
    setupGPLPackage(pkg);
    finalizeDefaultPackage(pkg);
    return pkg;
}



/*******************************************************************************
 *
 * Creates and modifies the package with the content that is required for the
 * GPL Binary packages
 *
 */
function setupGPLBinaryPackage() {
    var pkg = setupDefaultPackage();
    pkg.qt = option.qtGPLLocation;
    setupBinaryPackage(pkg);
    setupGPLPackage(pkg);

    if (os_name() == OS_NAME_WINDOWS) {
	pkg.maketool = find_executable("mingw32-make");
	pkg.qmakespec = "win32-g++";
	pkg.originalPath = System.getenv("PATH");

	pkg.preCompileStep = function(pkg) {
	    setPathForMinGW(pkg);
	}

	pkg.postCompileStep = function(pkg) {
	    if (os_name() == OS_NAME_WINDOWS) {
		System.setenv("PATH", pkg.originalPath);
	    }
	}
    }

    finalizeDefaultPackage(pkg);
    return pkg;
}


function setPathForMinGW(pkg) {
    // setup mingw for compilation...
    if (os_name() == OS_NAME_WINDOWS) {
        // remove cygwin from path...
        var path = pkg.originalPath.split(";");
        var newPath = [pkg.qt + "/bin"];
        for (var i=0; i<path.length; ++i) {
            if (path[i].find("cygwin") < 0) {
                newPath.push(path[i]);
            }
        }
        System.setenv("PATH", newPath.join(";"));
    }
}




/*******************************************************************************
 *
 * Creates and modifies the package with the content that is required for the
 * Commercial Source packages
 *
 */
function setupCommercialSourcePackage() {
    var pkg = setupDefaultPackage();
    setupSourcePackage(pkg);
    setupCommercialPackage(pkg);
    finalizeDefaultPackage(pkg);
    return pkg;
}



function setupCommercialBinaryPackage() {
    var pkg = setupDefaultPackage();
    pkg.qt = option.qtCommercialLocation;
    setupCommercialPackage(pkg);
    setupBinaryPackage(pkg);
    finalizeDefaultPackage(pkg);
    return pkg;
}



function setupEvalPackages() {
    var pkg = setupDefaultPackage();
    pkg.qt = option.qtEvalLocation;
    setupBinaryPackage(pkg);
    finalizeDefaultPackage(pkg);
    return pkg;
}


function createPackage(pkg) {

    verbose("Creating package: " + pkg.name);
    prepareSourceTree();

    var dir = new Dir(javaDir);
    dir.setCurrent();

    if (pkg.binary) {
        verbose(" - pre compile step...");
        pkg.preCompileStep(pkg);

        verbose(" - compiling and running generator...");
        compileAndRunGenerator(pkg);

        verbose(" - compiling native libraries...");
        compileNativeLibraries(pkg);

        verbose(" - compiling java files...");
        compileJavaFiles(pkg);

        verbose(" - creating qtjambi.jar");
        createJarFile(pkg);

        verbose(" - documentation...");
        createDocs(pkg);

        if (os_name() == OS_NAME_MACOSX) {
            verbose(" - OSX install_name foo...");
            fixInstallName();
        }

        verbose(" - post compile step...");
        pkg.postCompileStep(pkg);
    }

    verbose(" - moving files around...");
    moveFiles(pkg);

    verbose(" - removing unwanted content...");
    removeFiles(pkg);

    verbose(" - expanding macroes...");
    expandMacros(pkg.licenseHeader);

    if (pkg.binary && option.platformJar) {
        verbose(" - creating platform .jar...");
        createPlatformJar(pkg);
    }

    verbose(" - bundling package...");
    createBundle(pkg);


    verbose(" - done!");
}

/*******************************************************************************
 * Check the compiler as we can only do the .net 2003 on windows
 */
function checkCompiler() {
    if (os_name() == OS_NAME_WINDOWS) {
        execute([find_executable("cl"), "/?"]);
        if (Process.stderr.indexOf("13.10") < 0)
            throw "checkCompiler(): bad compiler version, only MSVC.net2003 is supported";
    } else {
        print("TODO: check compiler for non-windows...");
    }
}


/*******************************************************************************
 * Syncs perforce and sets up the source tree containing the java stuff...
 */

function prepareSourceTree()
{
    deletePackageDir();

    var client = "package-builder" + (os_name() == OS_NAME_WINDOWS ? "" : "-linux");

    System.setenv("P4PORT", "p4.troll.no:866");
    System.setenv("P4CLIENT", client);

    verbose(" - creating directory");
    var dir = new Dir(packageDir);
    dir.mkdirs(packageDir);
    dir.setCurrent();

    verbose(" - sync'ing source tree");
    execute([command.p4, "sync", "-f",
             "//depot/qtjambi/" + version + "/...",
             "//depot/research/main/uic4/..."]);

    execute([command.chmod, "-R", "u+w", "."]);
}


/*******************************************************************************
 * Does an attempt to find QTDIR
 */
function findQtDir() {
    var qtdir = array_get_next_value(args, "--qt");
    if (!qtdir)
        qtdir = System.getenv("QTDIR");
    if (!File.exists(qtdir))
        throw "Qt library '%1' does not exist".arg(option.qtdir);
    return qtdir;
}

/*******************************************************************************
 * Finds the generator name
 */
function findGeneratorName() {
    var name = "";
    if (os_name() == OS_NAME_WINDOWS)
        name = "release/generator.exe";
    else
        name = "generator";
    return javaDir  + "/generator/" + name;
}


/*******************************************************************************
 * Compiles and runs the generator
 */
function compileAndRunGenerator(pkg) {
    var dir = new Dir(javaDir);
    dir.cd("generator");
    dir.setCurrent();

    System.setenv("QTDIR", pkg.qt);

    verbose("   - running qmake");
    execute([pkg.qmake, "-spec", pkg.qmakespec, "-config", "release"]);

    verbose("   - building");
    var make = [pkg.maketool];
    execute(make);

    verbose("   - running");
    execute([command.generator, "--jdoc-enabled", "--jdoc-dir", "../doc/jdoc"]);

    dir.cdUp();
    dir.setCurrent();
}


/*******************************************************************************
 * Compiles the native libraries
 */
function compileJavaFiles() {
    verbose("   - running juic");
    execute(javaDir + "/bin/juic -cp .");

    verbose("   - building");
    execute([command.javac, "-J-Xmx1024m", "-target", "1.5", "@java_files"]);

    try {
        execute([command.javac, "-J-Xmx1024m", "-target", "1.5", "com/trolltech/demos/HelloGL.java"]);
    } catch (e) {
        print("warning: failed to compile HelloGL demo, see 'hellogl_error.log' for details\n");
        File.write("hellogl_error.log", e);
    }
}


/*******************************************************************************
 * Compiles the native libraries
 */
function compileNativeLibraries(pkg) {
    verbose("   - running qmake");
    execute([pkg.qmake, "-spec", pkg.qmakespec, "-r", "CONFIG+=release", "-after", "CONFIG-=debug debug_and_release"]);

    verbose("   - running make");
    var make = [pkg.maketool];
    execute(make);
}


/*******************************************************************************
 * prepare package dir..
 */
function deletePackageDir()
{
  // Delete old package dir if present..
  var dir = new Dir(packageDir);
  if (dir.exists) {
    Process.execute(command.rm + " -rf " + packageDir);
  }

  dir.mkdirs();
}


function expandMacros(header) {

    var extensions = ["cpp", "h", "java", "html", "ui"];

    var this_year = new Date().getYear();


    function replace(a, b) {
        for_all_files(packageDir, function(name) {
            multireplace_in_file(name, a, b);
        }, extensions);
    }

    var from = [];
    var to = [];
    from.push("\\$THISYEAR\\$");        to.push(this_year);
    from.push("\\$TROLLTECH\\$");       to.push("Trolltech ASA");
    from.push("\\$PRODUCT\\$");         to.push("Qt Jambi");
    from.push("\\$LICENSE\\$");         to.push(header);
    from.push("\\$JAVA_LICENSE\\$");    to.push(header);
    from.push("\\$CPP_LICENSE\\$");     to.push(header);
    replace(from, to);
}


/*******************************************************************************
 * Copies the Qt libraries into the bin/libs directories according to platform
 */
function copyQtBinaries() {
}


/*******************************************************************************
 * Creates the jar file
 */
function createJarFile() {
    var tmpfile = "jar_file.tmp";
    var fileList = [];

    // Add all the class files in com.trolltech.qt + core, gui, sql, opengl
    var cutPoint = javaDir.length + 1;
    for_all_files(javaDir, function(name) {
        if (name.indexOf("trolltech/qt/") >= 0 && name.endsWith(".class"))
            fileList.push(name.substring(cutPoint));
        else if (name.indexOf("trolltech/tools/designer/") >= 0 && name.endsWith(".class"))
            fileList.push(name.substring(cutPoint));
    });

    // Write the content file.
    File.write(tmpfile, fileList.join("\n"));
    execute([command.jar, "-cf", "qtjambi.jar", "@" + tmpfile]);
    execute([command.rm, tmpfile]);
}


/*******************************************************************************
 * Copy Qt docs and run qdoc-conf on the main bundle...
 */
function createDocs() {
    var dir = new Dir(javaDir);
    dir.setCurrent();

    execute([command.rm, "-rf", "doc"]);

    var docDir = new Dir(javaDir + "/doc/html");
    docDir.mkdirs();
    docDir.setCurrent();

    if (!option.noJavadocDownload) {
        if (os_name() == OS_NAME_MACOSX)
            execute([command.curl, "-O", option.javadocLocation]);
        else
            execute([command.wget, option.javadocLocation]);
    } else {
        execute([command.cp, option.startDir + "/" + javadocName, docDir.absPath]);
    }

    execute([command.jar, "-xf", javadocName]);
    execute([command.rm, javadocName]);

    // Restore to old directory...
    dir.setCurrent();
}


/*******************************************************************************
 * Moves required headers into include, the generator into bin and etc...
 */
function moveFiles(pkg) {

    var dir = new Dir(javaDir);
    dir.setCurrent();

    for (var i=0; i<pkg.mkdirs.length; ++i) {
        new Dir(pkg.mkdirs[i]).mkdirs();
    }

    for (var i=0; i<pkg.copyFiles.length; ++i) {
        var source = "";
        var target = "";

        if (pkg.copyFiles[i] is Array) {
            source = pkg.copyFiles[i][0];
            target = pkg.copyFiles[i][1];
        } else {
            source = pkg.copyFiles[i];
            target = ".";
        }

        if (source.endsWith(".sh"))
            execute([command.chmod, "u+x", source]);
        else if (source.endsWith(".cpp")
                 || source.endsWith(".java")
                 || source.endsWith(".h")) {
            execute([command.chmod, "u+rw", source]);
        }

	try {
	    new Dir(source).setCurrent(); // check if its a directory?
	    execute([command.cp, "-R", source, target]);
	    dir.setCurrent();
	} catch (e) {
	    execute([command.cp, source, target]); // straight copy as -R only does symbolic links...
	}

    }
}


/*******************************************************************************
 * Removes all the parts that should not be used
 */
function removeFiles(pkg) {

    for (var i=0; i<pkg.removeDirs.length; ++i)
        execute([command.rm, "-rf", pkg.removeDirs[i]]);

    for (var i=0; i<pkg.removeFiles.length; ++i)
        execute([command.rm, pkg.removeFiles[i]]);

    // Some files that were generated along the way...
    var files = [];
    for_all_files(javaDir, function(name) {
        if (name.endsWith(".ilk")
            || name.endsWith(".pdb")
            || name.endsWith(".manifest")
            || name.endsWith(".exp")
            || name.endsWith(".log"))
            files.push(name);
        else if (name.indexOf("/lib/") >=0 && name.endsWith(".dll"))
            files.push(name);
        else if (name.indexOf("com_trolltech_") >= 0 && name.endsWith(".lib"))
            files.push(name);
        else if (name.indexOf("/plugins/") >= 0 && name.endsWith(".lib"))
            files.push(name);
	else if (name.endsWith(".debug"))
	    files.push(name);
    });
    for (var i=0; i<files.length; ++i)
        execute([command.rm, files[i]]);
}

/*******************************************************************************
 *
 * Creates the platform bundle...
 *
 */
function createPlatformJar(pkg) {
    var dir = new Dir(javaDir);
    dir.setCurrent();

    var name = option.startDir + "/" + pkg.name + ".jar";

    var libDir = os_name() == OS_NAME_WINDOWS ? "bin" : "lib";
    var postfix;

    // plugins...
    execute([command.jar, "-cf", name, "plugins"]);

    // libraries...
    for_all_files(libDir, function(f) {
        if (f.endsWith(".dll")
            || f.indexOf(".so") >= 0
            || f.indexOf(".dylib") >=0 || f.endsWith(".jnilib") >= 0) {

	    // Avoid symlinks on linux...
	    if (os_name() == OS_NAME_LINUX 
		&& (f.indexOf("libqtjambi") >= 0 || f.indexOf("libcom_trolltech") >= 0)
		&& !f.endsWith(".so"))
		return;
	    // Skip util libs...
	    if (f.indexOf("Assistant") >= 0
		|| f.indexOf("Designer") >= 0
		|| f.indexOf("Script") >= 0)
		return;
	    verbose("   - adding: " + f.split("/").pop());
            execute([command.jar, "-uf", name, "-C", libDir, f.split("/").pop()]);
        }
        });

    // qt_system_libs file...
    if (os_name() != OS_NAME_MACOSX) {
        var file = new File("qt_system_libs");
        file.open(File.WriteOnly);
        if (os_name() == OS_NAME_WINDOWS) {
            if (pkg.packageName == "win32") {
                file.writeLine("msvcr71.dll");
                file.writeLine("msvcp71.dll");
            } else {
                file.writeLine("msvcr80.dll");
                file.writeLine("msvcp80.dll");
            }
        } else if (os_name() == OS_NAME_LINUX) {
            file.writeLine("libstdc++.so.5");
        }
        file.close();
        execute([command.jar, "-uf", name, "qt_system_libs"]);
	File.remove("qt_system_libs");
    }
}


/*******************************************************************************
 * Creates the bundle...
 */
function createBundle(pkg) {
    var dir = new Dir(javaDir);
    dir.cdUp();
    dir.setCurrent();

    execute([command.mv, version, pkg.name]);

    var packageFile = option.startDir + "/" + pkg.name;
    if (os_name() == OS_NAME_WINDOWS)
        packageFile += ".zip";
    else
        packageFile += ".tar.gz";

    if (os_name() == OS_NAME_WINDOWS) {
        try  { execute([command.rm, packageFile]); } catch (e) { }
        execute([command.zip, "-rq", packageFile, pkg.name]);
    } else {
        execute([command.tar, "-czf", packageFile, pkg.name]);
    }
}

/* mac specific stuff */

function fixInstallName() {
    verbose(" - fixing install name");

    var dir = new Dir(javaDir);
    dir.cd("lib");
    dir.setCurrent();

    var files = dir.entryList("lib*");
    for (var i=0; i<files.length; ++i) {
        var file = files[i];
        for (var j=0; j<files.length; ++j) {
            Process.execute(["install_name_tool", "-change", file, "@loader_path/" + file, files[j]]);
        }
    }
}

/*******************************************************************************
 *
 * Looks in the file com/trolltech/qt/QtJambi.java for the version numbers
 * and extracts them into the version string
 *
 */
function figureVersion() {
    var content = File.read("../com/trolltech/qt/QtJambi.java");

    var regexp_major = /MAJOR_VERSION += +(\d) *;/;
    var regexp_minor = /MINOR_VERSION += +(\d) *;/;
    var regexp_patch = /PATCH_VERSION += +(\d) *;/;
    var regexp_build = /BUILD_NUMBER += +(\d) *;/;

    if (regexp_major.search(content) < 0) throw "failed to locate MAJOR_VERSION in QtJambi.java";
    if (regexp_minor.search(content) < 0) throw "failed to locate MINOR_VERSION in QtJambi.java";
    if (regexp_patch.search(content) < 0) throw "failed to locate PATCH_VERSION in QtJambi.java";
    if (regexp_build.search(content) < 0) throw "failed to locate BUILD_NUMBER in QtJambi.java";

    var str = regexp_major.cap(1)
              + "." + regexp_minor.cap(1)
              + "." + regexp_patch.cap(1) + "_";

    var build = regexp_build.cap(1);
    for (var i=2 - build.length; i>0; --i)
        str += "0";
    return str + build;
}


function packageName(pkg) {
    var name = "qtjambi";

    if (pkg.binary)
        name += "-" + pkg.packageName;

    name += "-" + pkg.license;
    name += "-" + pkg.version;

    if (!pkg.binary)
        name += "-src";

    return name;
}


/*******************************************************************************
 * Display the help options
 */
function displayHelp() {
    print("Options:" +
          "\n  --help, -help, -h    This help" +
          "\n  --no-cpp-build       Don't rebuild the cpp sources" +
          "\n  --no-generator       Don't build and run the generator" +
          "\n  --no-java-build      Don't build the java sources" +
          "\n  --no-sync            Don't sync the source tree" +
          "\n  --package-only       Don't sync/build, just put together the package" +
          "\n  --no-compiler-check  Don't check the compiler version on windows (requires 13.10)" +
          "\n  --qt [dir]           Location of the qt dir used for inclusion in th package" +
          "\n  --verbose            Verbose output" +
          "\n");
}


function verbose(s) {
    if (option.verbose)
        print(s);
}



