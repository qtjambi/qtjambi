
// *** Constants
const version           = "1.0.0";
const depotVersion      = "4.3.0_01";
const jambiVersion      = "4.3.0_01";
const eclipseBranch     = "stable";
const packageDir        = os_name() == OS_NAME_WINDOWS
                            ? "c:/package-builder/tmp"
                            : "/home/qt/package-builder/tmp";
const eclipsePackages   = os_name() == OS_NAME_WINDOWS
                            ? "c:/package-builder/eclipse_classes"
                            : "/home/qt/eclipse_package";
const dirSeparator      = os_name() == OS_NAME_WINDOWS ? ";" : ":";
const execPrefix = os_name() == OS_NAME_WINDOWS ? "release/" : "./";

const licenseLocation = packageDir + "/qtjambi/" + depotVersion + "/dist/eclipse";
const licenseFile = "LICENSE.QT_JAMBI_ECLIPSE_INTEGRATION";

const originalPath = System.getenv("PATH");

const jarFilesDest = packageDir + "/jarFiles";
var jarFilesDir = new Dir(jarFilesDest);
jarFilesDir.mkdirs(jarFilesDest);

const midlPath = "c:/progra~1/micros~1.net/common7/tools/bin/midl.exe";
const vcPath = "c:/progra~1/micros~1.net/vc7/bin;c:/progra~1/micros~1.net/common7/ide";
const vcInclude = "c:/progra~1/micros~1.net/vc7/include;c:/progra~1/micros~1.net/vc7/platformsdk/include;c:/progra~1/micros~1.net/sdk/v1.1/include";


// *** Commands
var command = new Object();
command.chmod = find_executable("chmod");
command.p4 = find_executable("p4");
command.qmake = find_executable("qmake");
command.rm = find_executable("rm");
command.make = make_from_qmakespec();
command.javac = find_executable("javac");
command.jar = find_executable("jar");
command.cp = find_executable("cp");
command.zip = find_executable("zip");
command.gzip = find_executable("gzip");
command.tar = find_executable("tar");
command.unzip = find_executable("unzip");

// *** Options
var option = new Object();
option.verbose = array_contains(args, "--verbose");
option.gpl = array_contains(args, "--gpl");
option.qtdir = findQtDir();

if (option.gpl)
    command.make = find_executable("mingw32-make");


const gplExtension = option.gpl ? "-mingw" : "";

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
          "\n");
}

function prepareSourceTree() {
    verbose("Preparing source tree:");
    var client = "eclipse-package-builder" + (os_name() == OS_NAME_WINDOWS ? "" : "-linux");

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
    var classPath = packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtproject/src"
                    + dirSeparator + eclipsePackages;
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

    copyFiles([jambiRootDir + "/plugin.xml"], jambiRootDir, qtjambiPackageDest);
    qtjambiPackageDir.setCurrent();

    makeJarFile(jarFilesDest + "/com.trolltech.qtjambi_" + version + ".jar",
                jambiRootDir + "/META-INF/MANIFEST.MF",
                ["plugin.xml", "com", "icons"]);
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
    compileJavaFiles(srcPath, eclipsePackages, "com/trolltech/qt", bundleDest + "/bin");

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
    
    execute([midlPath, "release/qtdesigner.idl", "/nologo", "/tlb", "release/qtdesigner.tlb"]);
    
    System.setenv("PATH", currentPath);
    System.setenv("INCLUDE", "");
    
    execute([option.qtdir + "/bin/idc", "release/qtdesigner.dll", "/tlb", "release/qtdesigner.tlb"]);
}

function generateDesignerCode() {
    verbose("-- generating code for designer package");

    var generatorPath = packageDir + "/eclipse/" + eclipseBranch + "/qswt/designer";
    var dir = new Dir(generatorPath);
    dir.setCurrent();

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
    var linuxQtDest = packageDir + "/output/plugins/com.trolltech.qt.linux.x86_4.3.0";
    var linuxQtRootDir = packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qt.linux.x86";
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
	? packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner.win32.x86"
	: packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner.linux.x86";

    if (os_name() != OS_NAME_WINDOWS) {
	    var pluginsDir = packageDir + "/output/plugins/com.trolltech.qtdesigner.linux.x86_" + version;
	    dir = new Dir(pluginsDir);
	    dir.mkdirs(pluginsDir);
	    var suffixes = ["", ".4", ".4.3", ".4.3.0"];
	    for (var i=0; i<suffixes.length; ++i) {
	        copyFiles([packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner.linux.x86/lib/libqtdesigner.so"
                        + suffixes[i]],
                        packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner.linux.x86",
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

    var dllDest = destDir + "/plugins/com.trolltech.qtdesigner.win32.x86_" + version;
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
        copyFiles(["c:/windows/system32/msvcp71.dll", "c:/windows/system32/msvcr71.dll"],
                  "c:/windows/system32",
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

function setPathForMinGW() {
    // setup mingw for compilation...
    if (os_name() == OS_NAME_WINDOWS) {
        // remove cygwin from path...
        var path = originalPath.split(";");
        var newPath = [];
        for (var i=0; i<path.length; ++i) {
            if (path[i].find("cygwin") < 0) {
                newPath.push(path[i]);
            }
        }
        System.setenv("PATH", newPath.join(";"));
    }
}

function build() {    
    prepareSourceTree();
    if (option.gpl)
        setPathForMinGW();
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
