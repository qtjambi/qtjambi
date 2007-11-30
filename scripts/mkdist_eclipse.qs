
// Change these with every new version
const VERSION = figureVersion();

const version           = "1.0." + VERSION.patch;
const qtVersion         = VERSION.major + "." + VERSION.minor + "." + VERSION.patch;
const depotVersion      = qtVersion + "_01";
const jambiVersion      = qtVersion + "_01";
const eclipseBranch     = "stable";
// --


// ### Fixed path (must also be changed in client spec)
const packageDir        = os_name() == OS_NAME_WINDOWS
                            ? "c:/tmp/package-builder"
                            : "/tmp/package-builder";

// ### Fixed path
const eclipsePackages   = os_name() == OS_NAME_WINDOWS
                            ? "c:/eclipse-sdk"
                            : "/home/qt/eclipse_package";

const dirSeparator      = os_name() == OS_NAME_WINDOWS ? ";" : ":";
const execPrefix = os_name() == OS_NAME_WINDOWS ? "release/" : "./";
const licenseLocation = packageDir + "/qtjambi/" + depotVersion + "/dist/eclipse";
const licenseFile = "LICENSE.QT_JAMBI_ECLIPSE_INTEGRATION";

const originalPath = System.getenv("PATH");

const jarFilesDest = packageDir + "/jarFiles";
var jarFilesDir = new Dir(jarFilesDest);
jarFilesDir.mkdirs(jarFilesDest);

// ### Fixed paths (needed to build with mingw, but we should implement a search for vs i suppose)
const vcPath = System.getenv("VSINSTALLDIR") + "/vc/bin;"
               + System.getenv("VSINSTALLDIR") + "/common7/ide;"
               + System.getenv("VSINSTALLDIR") + "/common7/tools/bin";
const vcInclude = System.getenv("VSINSTALLDIR") + "/vc/include;"
                  + System.getenv("VSINSTALLDIR") + "/vc/platformsdk/include"


// *** Options
var option = new Object();
option.verbose = array_contains(args, "--verbose");
option.gpl = array_contains(args, "--gpl");
option.sixtyFour = System.getenv("ARCH") == "x86_64";
option.qtdir = findQtDir();

option.crtRedist = array_get_next_value(args, "--crt-redist");
if (option.crtRedist)
    option.crtRedist = option.crtRedist.replace(/\\/g, "/");

// *** Commands
var command = new Object();
command.chmod = find_executable("chmod");
command.p4 = find_executable("p4");
command.rm = find_executable("rm");
command.make = make_from_qmakespec();
command.javac = find_executable("javac");
command.jar = find_executable("jar");
command.cp = find_executable("cp");
command.zip = find_executable("zip");
command.gzip = find_executable("gzip");
command.tar = find_executable("tar");
command.unzip = find_executable("unzip");
command.qmake = option.qtdir + "/bin/qmake";


for (var i in option)
    verbose("'%1': %2".arg(i).arg(option[i]));
verbose("");
for (var i in command)
    verbose("using command '%1' in: %2".arg(i).arg(command[i]));

// Suffix for package names
const gplExtension = option.gpl ? "-mingw" : "";

const sixtyFourBitExtension = option.sixtyFour ? "-64-bit" : "";

function verbose(s) {
    if (option.verbose)
        print(s);
}


function findQtDir() {
    var qtdir = array_get_next_value(args, "--qt");
    if (!qtdir)
        qtdir = System.getenv("QTDIR");
    if (!File.exists(qtdir))
        throw "Qt library '%1' does not exist".arg(option.qtdir);

    while (qtdir.indexOf("\\") >= 0) {
        qtdir = qtdir.replace("\\", "/");
    }

    return qtdir;
}

/*******************************************************************************
 * Display the help options
 */
function displayHelp() {
    print("Options:" +
          "\n  --help, -help, -h    This help" +
          "\n  --verbose            Verbose output" +
          "\n  --gpl                Name package to be compatible with GPL" +
          "\n  --64                 Name package to be 64 bit" +
          "\n");
}

function prepareSourceTree() {
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
             "//depot/qtjambi/" + depotVersion + "/...",
             "//depot/eclipse/" + eclipseBranch + "/...",
             "//depot/ide/..."]);

    execute([command.chmod, "-R", "u+w", "."]);
}

function compileJavaFiles(rootDir, classpath, subDirectory, outputDir) {
    verbose("-- compiling .java files");
    verbose("--      from: '" + rootDir + "/" + subDirectory + "'");
    verbose("--        to: '" + outputDir + "'");

    files = find_files(rootDir + "/" + subDirectory, ["java"]);

    for (i=0;i<files.length; ++i) {
        verbose("--       -- : " + files[i]);
        execute([command.javac,
                "-target", "1.5",
                "-cp", rootDir + dirSeparator + classpath,
                "-d", outputDir,
                files[i]]);
    }
}

function makeJarFile(path, manifestFile, files) {
    verbose("-- making jar-file: " + path);
    execute([command.jar, "-cfm", path, manifestFile].concat(files));
}

function cygwinnify(input) {
    if (os_name() == OS_NAME_WINDOWS) {
        if (input.startsWith("/"))
           input = "/cygdrive/c" + input;
        else
            input = input.replace("c:", "/cygdrive/c");
    }

    return input;
}

// *** Copies files in "files" array to "destDir" into the subdir you get
// *** by removing "rootPath" from the file path
function copyFiles(files, rootPath, destDir) {
    for (var i=0; i<files.length; ++i) {
        var subPath = files[i].right(files[i].length - rootPath.length - 1);
        if (subPath.lastIndexOf("/") == -1) {
            subPath = "";
        } else {
            subPath = subPath.left(subPath.lastIndexOf("/"));
            var dir = new Dir(subPath);
            verbose("-- creating directory '" + destDir + "/" + subPath + "'");
            dir.mkdirs(destDir + "/" + subPath);
        }


        verbose("-- copying\n"
                + "--    from: " + files[i].right(files[i].length - files[i].lastIndexOf("/") - 1) + "\n"
                + "--      to: " + destDir + "/" + subPath);
        execute([command.cp, cygwinnify(files[i]), cygwinnify(destDir + "/" + subPath)]);
    }
}

function buildJambi() {
    verbose("Building Qt Jambi package");
    var jambiRootDir = packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtjambi";
    var classPath = packageDir + "/tempQtBundle/bin" + dirSeparator + eclipsePackages;
    var classFileOutput = packageDir + "/tempClassFiles";

    var dir = new Dir(classFileOutput);
    dir.mkdirs(classFileOutput);

    compileJavaFiles(jambiRootDir + "/src", classPath, "com/trolltech", classFileOutput);

    // Find files to put in package
    var qtjambiPackageDest = packageDir + "/tempQtJambiPackage";
    var qtjambiPackageDir = new Dir(qtjambiPackageDest);
    qtjambiPackageDir.mkdirs(qtjambiPackageDest);

    dir.setCurrent();
    var filesInPackage = find_files(classFileOutput, ["class"]);  // .class files
    copyFiles(filesInPackage, classFileOutput, qtjambiPackageDest);

    // Icons
    var iconsDir = jambiRootDir + "/icons";
    filesInPackage = find_files(iconsDir, ["gif"]);
    copyFiles(filesInPackage, jambiRootDir, qtjambiPackageDest);

    // Resources
    var resourcesDir = jambiRootDir + "/resources";
    filesInPackage = find_files(resourcesDir, ["png", "jui", "txt", "java_template"]);
    copyFiles(filesInPackage, resourcesDir, qtjambiPackageDest);
    copyFiles([resourcesDir + "/qt_system_libs"], resourcesDir, qtjambiPackageDest);

    copyFiles([jambiRootDir + "/plugin.xml"], jambiRootDir, qtjambiPackageDest);
    qtjambiPackageDir.setCurrent();

    makeJarFile(jarFilesDest + "/com.trolltech.qtjambi_" + version + ".jar",
                jambiRootDir + "/META-INF/MANIFEST.MF",
                ["plugin.xml", "com", "icons", "qt_system_libs"]);
}

function buildDesignerCommon() {
    verbose("Building common designer package");

    var designerRootDir = packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner";
    designerPackageDest = packageDir + "/tempQtDesignerPackage";

    var dir = new Dir(designerPackageDest + "/bin");
    dir.mkdirs(designerPackageDest + "/bin");

    generateDesignerCode();
    compileDesignerJavaCode(designerPackageDest);

    var designerPackageDir = new Dir(designerPackageDest);
    designerPackageDir.setCurrent();

    // Icons
    var iconsDir = designerRootDir + "/icons";
    var files = find_files(iconsDir, ["gif"]);
    copyFiles(files, designerRootDir, designerPackageDest);

    // Action icons
    var sourceDir = designerRootDir + "/src";
    files = find_files(sourceDir, ["png"]);
    copyFiles(files, sourceDir, designerPackageDest + "/bin");

    // Classes
    files = find_files(designerRootDir + "/bin", ["class"]);
    copyFiles(files, designerRootDir, designerPackageDest);
    copyFiles([designerRootDir + "/plugin.xml"], designerRootDir, designerPackageDest);

    makeJarFile(jarFilesDest + "/com.trolltech.qtdesigner_" + version + ".jar",
                designerRootDir + "/META-INF/MANIFEST.MF",
                ["plugin.xml", "icons", "bin"]);
}

function buildQtBundle() {
    verbose("Building Qt plugin");

    var bundleRootDir = packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qt";
    var bundleDest = packageDir + "/tempQtBundle";

    var dir = new Dir(bundleDest + "/bin");
    dir.mkdirs(bundleDest + "/bin");

    var srcPath = bundleRootDir + "/src";
    compileJavaFiles(srcPath, eclipsePackages, "com/trolltech", bundleDest + "/bin");

    copyFiles([bundleRootDir + "/plugin.xml"], bundleRootDir, bundleDest);

    dir = new Dir(bundleDest);
    dir.setCurrent();

    makeJarFile(jarFilesDest + "/com.trolltech.qt_" + version + ".jar",
                bundleRootDir + "/META-INF/MANIFEST.MF",
                ["bin", "plugin.xml"]);
}

function buildDesignerQtJambiFragment() {
    verbose("Building Qt Jambi fragment for designer plugin");

    var fragmentRootDir = packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner.qtjambi";
    var fragmentPackageDest = packageDir + "/tempQtJambiFragment";

    var dir = new Dir(fragmentPackageDest + "/bin");
    dir.mkdirs(fragmentPackageDest + "/bin");

    var srcPath = fragmentRootDir + "/src";
    var separator = os_name() == OS_NAME_WINDOWS ? ";" : ":";
    compileJavaFiles(srcPath, packageDir + "/tempClassFiles" + separator + packageDir + "/tempQtDesignerPackage/bin" + separator + eclipsePackages,
                    "com/trolltech/qtdesigner/qtjambi", fragmentPackageDest + "/bin");

    copyFiles([fragmentRootDir  +"/fragment.xml"], fragmentRootDir, fragmentPackageDest);

    dir = new Dir(fragmentPackageDest);
    dir.setCurrent();

    makeJarFile(jarFilesDest + "/com.trolltech.qtdesigner.qtjambi_" + version + ".jar",
                fragmentRootDir + "/META-INF/MANIFEST.MF",
                ["bin", "fragment.xml"]);
}


function workAroundMissingMidl() {
    var currentPath = System.getenv("PATH");
    System.setenv("PATH", vcPath + ";" + currentPath);
    System.setenv("INCLUDE", vcInclude);

    const midlPath = find_executable("midl.exe");
    execute([midlPath, "tmp/obj/release_shared/qtdesigner.idl", "/nologo", "/tlb", "tmp/obj/release_shared/qtdesigner.tlb"]);

    System.setenv("PATH", currentPath);
    System.setenv("INCLUDE", "");

    execute([option.qtdir + "/bin/idc", "release/qtdesigner.dll", "/tlb", "tmp/obj/release_shared/qtdesigner.tlb"]);
}

function generateDesignerCode() {
    verbose("-- generating code for designer package");

    var generatorPath = packageDir + "/eclipse/" + eclipseBranch + "/qswt/designer";
    var dir = new Dir(generatorPath);
    dir.setCurrent();

    var oldpath = System.getenv("PATH");
    var oldinclude = System.getenv("INCLUDE");

    if (option.gpl && os_name() == OS_NAME_WINDOWS) {
        System.setenv("INCLUDE", "");
        System.setenv("PATH", "c:/MinGW/bin;" + option.qtdir + "/bin");
    }

    execute([command.qmake, "-config", "release"]);
    execute([command.make]);
    execute([execPrefix + "designer"]);

    var generatedPath = generatorPath + "/qtdesigner";
    dir = new Dir(generatedPath);
    dir.setCurrent();

    execute([command.qmake, "DESTDIR = .", "-config", "release"]);
    execute([command.make]);

    if (option.gpl) {
        workAroundMissingMidl();
    }
}

function compileDesignerJavaCode(destDir) {
    var sourcePath = packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner/src";
    var separator = os_name() == OS_NAME_WINDOWS ? ";" : ":";
    compileJavaFiles(sourcePath, packageDir + "/tempQtBundle/bin" + separator + eclipsePackages, "com/trolltech/qtdesigner", destDir + "/bin");
}

function buildLinuxQtJarFile() {
    verbose("Building Linux Qt library package");
    var linuxQtDest = packageDir + "/output/plugins/com.trolltech.qt.linux." + System.getenv("ARCH") + "_" + qtVersion;
    var linuxQtRootDir = packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qt.linux." + System.getenv("ARCH");
    var dir = new Dir(linuxQtDest + "/lib");
    dir.mkdirs(linuxQtDest + "/lib");

    var dlls = ["libQtCore.so", "libQtDesigner.so", "libQtDesignerComponents.so",
                "libQtAssistantClient.so", "libQtGui.so", "libQtXml.so", "libQtScript.so"];
    var suffixes = [".4"];
    for (var i=0; i<dlls.length; ++i) {
        for (var j=0; j<suffixes.length; ++j) {
	    copyFiles([option.qtdir + "/lib/" + dlls[i] + suffixes[j]], option.qtdir, linuxQtDest);
	}
    }
}

function buildDesignerPlatform() {
    verbose("Building platform designer package");
    var qswtDir = packageDir + "/eclipse/" + eclipseBranch + "/qswt/designer/qtdesigner";
    var designerPackageDest = packageDir + "/tempQtDesignerPackage";

    var designerRootDir = os_name() == OS_NAME_WINDOWS
                          ? packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner.win32." + System.getenv("ARCH")
	: packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner.linux." + System.getenv("ARCH");

    if (os_name() != OS_NAME_WINDOWS) {
	    var pluginsDir = packageDir + "/output/plugins/com.trolltech.qtdesigner.linux."+ System.getenv("ARCH") + "_" + version;
	    dir = new Dir(pluginsDir);
	    dir.mkdirs(pluginsDir);
	    var suffixes = ["", ".4", ".4.3", "." + qtVersion];
	    for (var i=0; i<suffixes.length; ++i) {
	        copyFiles([packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner.linux." + System.getenv("ARCH") + "/lib/libqtdesigner.so"
                        + suffixes[i]],
                        packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner.linux." + System.getenv("ARCH"),
                        pluginsDir);
	    }
	    copyFiles([designerRootDir + "/META-INF/MANIFEST.MF"], designerRootDir, pluginsDir);
    } else {
        // nothing needed atm
    }
}

function buildDesigner() {
    buildDesignerCommon();
    buildDesignerPlatform();
}

function makePlatformSpecificPackageLinux(destDir) {
   verbose("-- gztar'ing package");

   copyFiles([licenseLocation + "/" + licenseFile], licenseLocation, destDir);

   dir = new Dir(destDir);
   dir.setCurrent();
   execute([command.tar, "cfz", "qtjambi-eclipse-integration-linux-" + jambiVersion + gplExtension + ".tar.gz", "plugins", licenseFile]);

}

function makePlatformSpecificPackageWindows(destDir) {
    var qswtDir = packageDir + "/eclipse/" + eclipseBranch + "/qswt/designer/qtdesigner";
    var jambiScriptDir = packageDir + "/qtjambi/" + depotVersion + "/scripts";

    var dllDest = destDir + "/plugins/com.trolltech.qtdesigner.win32." + System.getenv("ARCH") + "_" + version;
    var dir = new Dir(dllDest);
    dir.mkdirs(dllDest);

    copyFiles([qswtDir + "/" + execPrefix + "qtdesigner.dll"], qswtDir + "/" + execPrefix, dllDest);
    copyFiles([jambiScriptDir + "/register_eclipse_integration.bat"], jambiScriptDir, destDir);

    var qtLibraries = ["QtCore4.dll", "QtDesigner4.dll", "QtDesignerComponents4.dll",
                       "QtAssistantClient4.dll", "QtGui4.dll", "QtXml4.dll", "QtScript4.dll"];
    for (var i=0; i<qtLibraries.length; ++i) {
        copyFiles([option.qtdir + "/bin/" + qtLibraries[i]], option.qtdir + "/bin", dllDest);
    }

    if (!option.gpl) {
        copyFiles([option.crtRedist + "/msvcr80.dll",
                   option.crtRedist + "/msvcp80.dll",
                   option.crtRedist + "/msvcm80.dll",
                   option.crtRedist + "/Microsoft.VC80.CRT.manifest"],
                  option.crtRedist,
                  dllDest);
    } else {
        var mingwDllPath = find_executable("mingwm10.dll");
        var idx = mingwDllPath.lastIndexOf("/");
        var mingwDllDir = mingwDllPath.substring(0, idx);
        copyFiles([mingwDllPath], mingwDllDir, dllDest);
    }

    copyFiles([licenseLocation + "/" + licenseFile], licenseLocation, destDir);

    verbose("-- zipping package");
    dir = new Dir(destDir);
    dir.setCurrent();
    execute([command.zip, "-r", "qtjambi-eclipse-integration-win32-" + jambiVersion + gplExtension + ".zip", "*"]);
}


function buildDesignerPlugins() {
    verbose("Building designer plugin directory");
    var packageDest = packageDir + "/output/plugins/com.trolltech.qtdesignerplugins";
    var jambiDir = packageDir + "/qtjambi/" + depotVersion;
    var designerIntegrationDir = packageDir + "/qtjambi/" + depotVersion + "/designer-integration";
    var pluginsSrc = packageDir + "/qtjambi/" + depotVersion + "/plugins/designer";

    var dir = new Dir(packageDest);
    dir.mkdirs(packageDest);


    dir = new Dir(jambiDir + "/qtjambi");
    dir.setCurrent();
    execute([command.qmake, "-config", "release"]);
    execute([command.make]);

    dir = new Dir(designerIntegrationDir);
    dir.setCurrent();
    execute([command.qmake, "-r", "-config", "release"]);
    execute([command.make]);

    var files;
    if (os_name() == OS_NAME_WINDOWS)
        files = [pluginsSrc + "/JambiCustomWidget.dll", pluginsSrc + "/JambiLanguage.dll"];
    else
        files = [pluginsSrc + "/libJambiCustomWidget.so", pluginsSrc + "/libJambiLanguage.so"];

   copyFiles(files, pluginsSrc, packageDest);

   if (os_name() == OS_NAME_WINDOWS && !option.gpl) {
       dir.mkdirs(packageDest + "/Microsoft.VC80.CRT");
       copyFiles([option.crtRedist + "/msvcr80.dll",
                  option.crtRedist + "/msvcp80.dll",
                  option.crtRedist + "/msvcm80.dll",
                  option.crtRedist + "/Microsoft.VC80.CRT.manifest"],
                 option.crtRedist,
                 packageDest + "/Microsoft.VC80.CRT");
   }
}

function buildPackage() {
    verbose("Building .zip file");
    var packageDest = packageDir + "/output";
    var pluginsDir = packageDest + "/plugins";
    var dir = new Dir(pluginsDir);
    if (dir.fileExists("."))
       print("WARNING: output dir already exists. delete the entire " + packageDir + " folder before running this script for best results");
    dir.mkdirs(pluginsDir);
    buildDesignerPlugins();


    var files = find_files(jarFilesDest, ["jar"]);
    copyFiles(files, jarFilesDest, pluginsDir);

    eval("makePlatformSpecificPackage" + os_name() + "(packageDest);");
}

function copyQmakeCache() {
    verbose("Copy .qmake.cache");
    copyFiles([option.qtdir + "/.qmake.cache"], option.qtdir, packageDir);
}

function figureVersion() {
    var content = File.read("../com/trolltech/qt/Utilities.java");

    var regexp_major = /MAJOR_VERSION += +(\d) *;/;
    var regexp_minor = /MINOR_VERSION += +(\d) *;/;
    var regexp_patch = /PATCH_VERSION += +(\d) *;/;
    var regexp_build = /BUILD_NUMBER += +(\d) *;/;

    if (regexp_major.search(content) < 0) throw "failed to locate MAJOR_VERSION in QtJambi.java";
    if (regexp_minor.search(content) < 0) throw "failed to locate MINOR_VERSION in QtJambi.java";
    if (regexp_patch.search(content) < 0) throw "failed to locate PATCH_VERSION in QtJambi.java";
    if (regexp_build.search(content) < 0) throw "failed to locate BUILD_NUMBER in QtJambi.java";

    return { major: regexp_major.cap(1),
             minor: regexp_minor.cap(1),
            patch: regexp_patch.cap(1),
            build: regexp_build.cap(1) };
}


function build() {
    verbose("Hello, making Qt Jambi Eclipse Integration");
    if (option.gpl) verbose("  (the GPL version)");
    if (option.sixtyFour) verbose("  (the 64 bit version)");
    prepareSourceTree();
    if (option.gpl)
        setPathForMinGW();
    copyQmakeCache();
    buildQtBundle();
    buildDesigner();
    if (os_name() != OS_NAME_WINDOWS)
       buildLinuxQtJarFile();
    buildJambi();
    buildDesignerQtJambiFragment();
    buildPackage();
    if (option.gpl && os_name() == OS_NAME_WINDOWS)
        System.setenv("PATH", originalPath);
}

if (array_contains(args, "-help") || array_contains(args, "-h")) {
    displayHelp();
} else {
    build();
}
