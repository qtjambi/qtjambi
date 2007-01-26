const packageDir = os_name() == OS_NAME_WINDOWS
                   ? "d:/tmp/package-builder"
                   : "/tmp/package-builder";
const version = "1.0.0-beta";
const javaDir = packageDir + "/qtjambi/" + version;
const javadocName = "qtjambi-javadoc-" + version + ".jar";
const jdocName = "qtjambi-jdoc-" + version + ".jar";

const regexp_mainfunction = /void *main *\( *String *\w* *\[ *\] *\)/

var command = new Object();
var option = new Object();

option.nocppbuild = array_contains(args, "--no-cpp-build");
option.nogenerator = array_contains(args, "--no-generator");
option.nojavabuild = array_contains(args, "--no-java-build");
option.nosync = array_contains(args, "--no-sync");
option.packageonly = array_contains(args, "--package-only");
option.qtdir = findQtDir();
option.verbose = array_contains(args, "--verbose");
option.nocompilercheck = array_contains(args, "--no-compiler-check");
option.teambuilder = array_contains(args, "--teambuilder");
option.startDir = new Dir().absPath;
option.javadocHTTP = array_get_next_value(args, "--javadoc");

var packageMap = new Object();
packageMap[OS_NAME_WINDOWS] = "win";
packageMap[OS_NAME_LINUX] = "linux";
packageMap[OS_NAME_MACOSX] = "mac";
option.packageName = packageMap[os_name()];

command.chmod = find_executable("chmod");
command.cp = find_executable("cp");
command.jar = find_executable("jar");
command.javac = find_executable("javac");
command.make = make_from_qmakespec();
command.mv = find_executable("mv");
command.p4 = find_executable("p4");
command.qmake = find_executable("qmake");
command.rm = find_executable("rm");
command.tar = find_executable("tar");
command.zip = find_executable("zip");
command.generator = findGeneratorName();

if (os_name() == OS_NAME_MACOSX)
    command.curl = find_executable("curl");
else
    command.wget = find_executable("wget");


const packageTreeReuse = option.nosync
                         || option.nogenerator
                         || option.nocppbuild
                         || option.nojavabuild
                         || option.packageonly;

if (option.packageonly) {
    option.nocppbuild = true;
    option.nogenerator = true;
    option.nojavabuild = true;
    option.nosync = true;
}

if (!option.javadocHTTP) {
    option.javadocHTTP = "http://anarki/~gunnar/packages";
}

option.javadocLocation = option.javadocHTTP + "/" + javadocName;
option.jdocLocation = option.javadocHTTP + "/" + jdocName;

const qtLibraryNames = ["QtCore", "QtGui", "QtOpenGL", "QtSql", "QtXml", "QtSvg", "QtDesigner", "QtDesignerComponents", "QtNetwork", "QtAssistantClient"];
const qtBinaryNames = ["designer", "linguist"];
var qtReleaseLibraries = [];
var qtDebugLibraries = [];
var qtBinaries = [];

var license_header = File.read("../dist/preview_header.txt");

if (array_contains(args, "-help") || array_contains(args, "-h")) {
    displayHelp();
} else {
    for (var i in option)
        if (option[i])
            verbose("'%1': %2".arg(i).arg(option[i]));
    verbose("reusing package tree: " + (packageTreeReuse ? "yes" : "no"));
    verbose("");
    for (var i in command)
        verbose("using command '%1' in: %2".arg(i).arg(command[i]));

    verbose("");

    if (!option.nocompilercheck)
        checkCompiler();

    if (!packageTreeReuse)
        deletePackageDir();

    findQtLibraries();
    prepareSourceTree();

    unpackJDocFiles();

    var dir = new Dir(javaDir);
    dir.setCurrent();

    if (!option.nogenerator) compileAndRunGenerator();
    if (!option.nocppbuild) compileNativeLibraries();
    if (!option.nojavabuild) compileJavaFiles();

    createPackage();
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
    verbose("Preparing source tree:");

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
 * Checks that the necesary configuration is in place. This is primarly the
 * Qt version used for linking.
 */
function findQtLibraries() {

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
        var libName = option.qtdir + "/" + location + "/"
                      + namePrefix + qtLibraryNames[i] + namePostfix;

        if (!File.exists(libName))
            throw "Library '%1' does not exist".arg(libName);

        qtReleaseLibraries.push(libName);
    }

    for (var i=0; i<qtBinaryNames.length; ++i) {
        var binName = option.qtdir + "/bin/" + qtBinaryNames[i] +  exe_extension;
        if (!File.exists(binName))
            throw "Binary file '%1' does not exist".arg(binName);
        qtBinaries.push(binName);
    }

    if (os_name() == OS_NAME_LINUX) {
        var locs = ["/lib", "/usr/lib"];
        for (var i=0; i<locs.length; ++i) {
            var name = locs[i] + "/libstdc++.so.5";
            if (File.exists(name)) {
                qtReleaseLibraries.push(name);
                break;
            }
        }
        print("libs are: \n" + qtReleaseLibraries.join("\n"));
    }
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
function compileAndRunGenerator() {
    verbose("Generator:");

    var dir = new Dir(javaDir);
    dir.cd("generator");
    dir.setCurrent();

    verbose(" - running qmake");
    execute(command.qmake + " -config release");

    verbose(" - building");

    var make = [command.make];
    if (option.teambuilder)
        make.push("-j20");
    execute(make);

    verbose(" - running");
    execute([command.generator, "--jdoc-enabled", "--jdoc-dir", "../doc/jdoc"]);

    dir.cdUp();
    dir.setCurrent();
}


/*******************************************************************************
 * Compiles the native libraries
 */
function compileJavaFiles() {
    verbose("Java files:");

    verbose(" - running juic");
    execute(javaDir + "/bin/juic -cp .");

    verbose(" - building");
    execute([command.javac, "@java_files"]);

    try {
        execute([command.javac, "com/trolltech/demos/HelloGL.java"]);
    } catch (e) {
        print("warning: failed to compile HelloGL demo, see 'hellogl_error.log' for details\n");
        File.write("hellogl_error.log", e);
    }
}


/*******************************************************************************
 * Compiles the native libraries
 */
function compileNativeLibraries() {
    verbose("Native libraries:");

    verbose(" - running qmake");
    execute([command.qmake, "-r", "CONFIG+=release", "-after", "CONFIG-=debug debug_and_release"]);

    verbose(" - running make");
    var make = [command.make];
    if (option.teambuilder)
        make.push("-j20");

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


/*******************************************************************************
 * Creates the package...
 */
function createPackage() {
    verbose("Creating package:");

    verbose(" - copying qt binaries");
    copyQtBinaries();

    verbose(" - creating qtjambi.jar");
    createJarFile();

    verbose(" - documentation");
    createDocs();

    verbose(" - expand macros");
    expandMacros();

    verbose(" - moving files around");
    moveFiles();

    verbose(" - removing files");
    removeFiles();

    verbose(" - bundling");
    createBundle();

//     verbose(" - create platform archive");
//     createPlatformArchive();
}


function expandMacros() {

    var extensions = ["cpp", "h", "java", "html", "ui"];

    var this_year = new Date().getYear();

    function replace(a, b) {
        for_all_files(packageDir, function(name) {
            replace_in_file(name, a, b);
        }, extensions);
    }

    replace("\\$THISYEAR\\$", this_year);
    replace("\\$TROLLTECH\\$", "Trolltech ASA");
    replace("\\$PRODUCT\\$", "Qt Jambi");
    replace("\\$LICENSE\\$", license_header);
}


/*******************************************************************************
 * Copies the Qt libraries into the bin/libs directories according to platform
 */
function copyQtBinaries() {
    var targetLibDir = javaDir + "/" + (os_name() == OS_NAME_WINDOWS ? "bin" : "lib");
    var targetBinDir = javaDir + "/bin";

    var allLibs = qtReleaseLibraries.concat(qtDebugLibraries);
    for (var i=0; i<allLibs.length; ++i)
        execute([command.cp, allLibs[i], targetLibDir]);

    for (var i=0; i<qtBinaries.length; ++i) {
        execute([command.cp, "-R", qtBinaries[i], targetBinDir]);
    }

    print(" - image format plugins");

    // Qt image format plugins
    execute([command.cp, "-R", option.qtdir + "/plugins", "."]);
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


function unpackJDocFiles() {
    print(" - Downloading .jdoc files: " + option.jdocLocation);

    var dir = new Dir(javaDir + "/doc/jdoc");
    dir.mkdirs();
    dir.setCurrent();

    if (os_name() == OS_NAME_MACOSX) {
        execute([command.curl, "-O", option.jdocLocation]);
    } else {
        execute([command.wget, option.jdocLocation]);
    }

    // Unpack the jdocs...
    execute([command.jar, "-xf", jdocName]);

    dir.cdUp();
    dir.cdUp();
    dir.setCurrent();
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

    if (os_name() == OS_NAME_MACOSX)
        execute([command.curl, "-O", option.javadocLocation]);
    else
        execute([command.wget, option.javadocLocation]);

    execute([command.jar, "-xf", javadocName]);
    execute([command.rm, javadocName]);

    // Restore to old directory...
    dir.setCurrent();
}


/*******************************************************************************
 * Moves required headers into include, the generator into bin and etc...
 */
function moveFiles() {
    execute([command.cp, command.generator, "bin"]);

    // Include files
    new Dir(javaDir + "/include").mkdirs();
    var includeFiles = ["qtjambi_core.h", "qtjambi_cache.h", "qtjambi_global.h", "qtjambilink.h", "qtjambifunctiontable.h", "qtjambitypemanager.h"];

    for (var i=0; i<includeFiles.length; ++i)
        execute([command.cp, "qtjambi/" + includeFiles[i], "include"]);


    if (os_name() == OS_NAME_WINDOWS) {
        try {
            var runtimes = [
                            find_executable("msvcr71.dll"),
                            find_executable("msvcp71.dll")
            ];
            for (var i=0; i<runtimes.length; ++i)
                execute([command.cp, runtimes[i], "bin"]);
        } catch (e) {
            print("moveFiles: failed to copy MSVC.net 2003 runtimes\n" +
                  "           executable will not run by default...\n" +
                  "           " + e);
        }
    }

    // Files into root dir
    var files = [
                 "dist/KNOWN_ISSUES",
                 "dist/LICENSE",
                 "dist/README",
                 "dist/changes-" + version
    ];

    if (os_name() == OS_NAME_MACOSX) {
        files.push("dist/mac/qtjambi.sh");
        files.push(["dist/mac/generator_example.sh", "generator_example"]);
        files.push("dist/mac/designer.sh");
    } else if (os_name() == OS_NAME_WINDOWS) {
        files.push("dist/win/designer.bat");
        files.push("dist/win/qtjambi.exe");
        files.push(["dist/win/generator_example.bat", "generator_example"]);
    } else {
        files.push("dist/linux/designer.sh");
        files.push("dist/linux/qtjambi.sh");
        files.push(["dist/linux/generator_example.sh", "generator_example"]);
    }

    for (var i=0; i<files.length; ++i) {
        var source = "";
        var target = "";

        if (files[i] is Array) {
            source = files[i][0];
            target = files[i][1];
        } else {
            source = files[i];
            target = ".";
        }

        if (source.endsWith(".sh"))
            execute([command.chmod, "u+x", source]);
        execute([command.cp, source, target]);
    }


}


/*******************************************************************************
 * Removes all the parts that should not be used
 */
function removeFiles() {
    var dirs = [
                "autotestlib",
                "com/trolltech/autotests",
                "com/trolltech/benchmarks",
                "com/trolltech/qt",
                "com/trolltech/tests",
                "com/trolltech/tools",
                "common",
                "cpp",
                "designer-integration",
                "dist",
                "doc/config",
                "doc/src",
                "eclipse-integration",
                "eclipse-stable",
                "generator",
                "juic",
                "launcher_launcher",
                "libbenchmark",
                "qtjambi",
                "qtjambi_core",
                "qtjambi_designer",
                "qtjambi_generator",
                "qtjambi_generator_tests",
                "qtjambi_gui",
                "qtjambi_network",
                "qtjambi_opengl",
                "qtjambi_sql",
                "qtjambi_svg",
                "qtjambi_xml",
                "scripts",
                "uic4",
                "tools",
                "whitepaper"
    ];

    for (var i=0; i<dirs.length; ++i)
        execute([command.rm, "-rf", dirs[i]]);

    var files = [
                 "Makefile",
                 "java.pro",
                 "java_files",
                 "rebuild.bat",
                 "rebuild.sh",
                 "build.xml"
    ];

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
    });

    for (var i=0; i<files.length; ++i)
        execute([command.rm, files[i]]);
}


/*******************************************************************************
 * Creates the bundle...
 */
function createBundle() {
    var dir = new Dir(javaDir);
    dir.cdUp();
    dir.setCurrent();

    var packageName = "qtjambi-" + option.packageName + "-" + version;

    execute([command.mv, version, packageName]);

    var packageFile = option.startDir + "/" + packageName;
    if (os_name() == OS_NAME_WINDOWS)
        packageFile += ".zip";
    else
        packageFile += ".tar.gz";

    if (os_name() == OS_NAME_WINDOWS) {
        try  { execute([command.rm, packageFile]); } catch (e) { }
        execute([command.zip, "-rq", packageFile, packageName]);
    } else {
        execute([command.tar, "-czf", packageFile, packageName]);
    }

    verbose("created package...");
}

// function createPlatformArchive() {
//     var dir = new Dir(javaDir);

//     if (os_name() == OS_NAME_WINDOWS) {
//         dir.cd("bin");
//         execute[command.jar, "-cf",
//     }

// }


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
