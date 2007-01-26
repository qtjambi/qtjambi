
// *** Constants
const version           = "0.0.4";
const depotVersion      = "1.0.0-beta";
const eclipseBranch     = "qtjambi-1.0.0-beta";
const packageDir        = os_name() == OS_NAME_WINDOWS
                            ? "c:/package-builder/tmp"
                            : "/home/eblomfel/package-builder/tmp";                        
const eclipsePackages   = os_name() == OS_NAME_WINDOWS
                            ? "c:/package-builder/eclipse_packages"
                            : "/home/eblomfel/eclipse_package";
const dirSeparator      = os_name() == OS_NAME_WINDOWS ? ";" : ":";                                              
const execPrefix = os_name() == OS_NAME_WINDOWS ? "release/" : "./";

const jarFilesDest = packageDir + "/jarFiles";
var jarFilesDir = new Dir(jarFilesDest);
jarFilesDir.mkdirs(jarFilesDest);

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
option.qtdir = findQtDir();

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
             "//depot/eclipse/..."]);

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
    
    compileJavaFiles(jambiRootDir + "/src", classPath, "com/trolltech/qtjambi", classFileOutput);
    
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
        
    var designerPackageDest = packageDir + "/tempQtDesignerPackage";
    var designerPackageDir = new Dir(designerPackageDest);
    designerPackageDir.mkdirs(designerPackageDest);
    designerPackageDir.setCurrent();
    
    // Icons
    var iconsDir = designerRootDir + "/icons";
    var files = find_files(iconsDir, ["gif"]);  
    copyFiles(files, designerRootDir, designerPackageDest);
    
    // Action icons
    var sourceDir = designerRootDir + "/src";
    files = find_files(sourceDir, ["png"]);
    copyFiles(files, sourceDir, designerPackageDest);    
    copyFiles([designerRootDir + "/plugin.xml"], designerRootDir, designerPackageDest);
    
    makeJarFile(jarFilesDest + "/com.trolltech.qtdesigner_" + version + ".jar", 
                designerRootDir + "/META-INF/MANIFEST.MF",
                ["plugin.xml", "com", "icons"]);
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
}

function compileDesignerJavaCode(destDir) {
    var sourcePath = packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner/src";
    compileJavaFiles(sourcePath, eclipsePackages, "com/trolltech/qtdesigner", destDir + "/bin");
}

function buildDesignerPlatform() {
    verbose("Building Windows designer package");
    var qswtDir = packageDir + "/eclipse/" + eclipseBranch + "/qswt/designer/qtdesigner";    
    var designerPackageDest = packageDir + "/tempQtDesignerWindowsPackage";
    var dir = new Dir(designerPackageDest + "/bin");
    dir.mkdirs(designerPackageDest + "/bin");
    
    dir = new Dir(designerPackageDest);    
    dir.mkdirs(designerPackageDest);

    var files = ["bin"];
    if (os_name() != OS_NAME_WINDOWS) { // linux        
        var dlls = ["libQtCore.so", "libQtDesigner.so", "libQtDesignerComponents.so", 
                    "libQtAssistantClient.so", "libQtGui.so", "libQtXml.so"]; 
	var suffixes = ["", ".4", ".4.3", ".4.3.0"];
	for (var i=0; i<dlls.length; ++i) {
		for (var j=0; j<suffixes.length; ++j) {
			copyFiles([option.qtdir + "/lib/" + dlls[i] + suffixes[j]], option.qtdir + "/lib", designerPackageDest);
			files.push(dlls[i] + suffixes[j]);
		}
        }

    }
    

    generateDesignerCode();            
    compileDesignerJavaCode(designerPackageDest);
    
    var jarFileName = os_name() == OS_NAME_WINDOWS 
                        ? "com.trolltech.qtdesigner.win32.x86_" + version + ".jar"
                        : "com.trolltech.qtdesigner.linux.x86_" + version + ".jar";
    var designerRootDir = os_name() == OS_NAME_WINDOWS 
                          ? packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner.win32.x86"
                          : packageDir + "/eclipse/" + eclipseBranch + "/com.trolltech.qtdesigner.linux.x86";
    dir.setCurrent();                          
    
    makeJarFile(jarFilesDest + "/" + jarFileName, 
                designerRootDir + "/META-INF/MANIFEST.MF",
                files);
            
    
}

function buildDesigner() {
    buildDesignerCommon();
    buildDesignerPlatform();
}

function makePlatformSpecificPackageLinux(destDir) {
   verbose("-- gztar'ing package");
   dir = new Dir(destDir);
   dir.setCurrent();
   execute([command.tar, "cfz", "qtjambi-eclipse-integration-linux-" + depotVersion + ".tar.gz", "plugins"]);

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
                       "QtAssistantClient4.dll", "QtGui4.dll", "QtXml4.dll"];
    for (var i=0; i<qtLibraries.length; ++i) {
        copyFiles([option.qtdir + "/bin/" + qtLibraries[i]], option.qtdir + "/bin", dllDest);
    }
    
    verbose("-- zipping package");
    dir = new Dir(destDir);
    dir.setCurrent();
    execute([command.zip, "-r", "qtjambi-eclipse-integration-win32-" + depotVersion + ".zip", "*"]);
}

function buildPackage() {
    verbose("Building .zip file");
    var packageDest = packageDir + "/output";
    var pluginsDir = packageDest + "/plugins";
    var dir = new Dir(pluginsDir);
    if (dir.fileExists("."))
       print("WARNING: output dir already exists. delete the entire " + packageDir + " folder before running this script for best results");
    dir.mkdirs(pluginsDir);
    
    
    var files = find_files(jarFilesDest, ["jar"]);
    copyFiles(files, jarFilesDest, pluginsDir);            
        
    eval("makePlatformSpecificPackage" + os_name() + "(packageDest);");            
        
    
}

function build() {
    prepareSourceTree();
            
    buildDesigner();
    buildJambi();
    buildPackage();
}

if (array_contains(args, "-help") || array_contains(args, "-h")) {
    displayHelp();
} else {
    build();
}