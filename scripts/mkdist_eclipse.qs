
// Set up P4
const client = "eclipse-package-builder" + (os_name() == OS_NAME_WINDOWS ? "" : "-linux");
const cygdrive = (os_name() == OS_NAME_WINDOWS ? "/cygdrive/c" : "")
const cygdrive_slash = (os_name() == OS_NAME_WINDOWS ? cygdrive + "/" : "")

// Set up constants
const rootDir = "/tmp";
const destDir = rootDir + "/output";

const qtjambidir = rootDir + "/qtjambi/1.0.0-tp2"

const javaScriptDir = qtjambidir + "/scripts";

const generatorDir = qtjambidir + "/eclipse-integration/qswt/designer";
const generatorExe = generatorDir
                     + (os_name() == OS_NAME_WINDOWS ? "/release/designer.exe" : "/designer");

const generatedDir = generatorDir + "/qtdesigner";
const generatedExe = os_name() == OS_NAME_WINDOWS ? "qtdesigner.dll" : "libqtdesigner.so";

const qtdesignerPackageDir = qtjambidir + "/eclipse-integration/com.trolltech.qtdesigner";
const qtjavaPackageDir = qtjambidir + "/eclipse-integration/com.trolltech.qtjambi";
const qtdesignerJavaSrcRoot = qtdesignerPackageDir + "/src";
const qtjavaJavaSrcRoot = qtjavaPackageDir + "/src";
const eclipseRoot = "/source/eclipse_packages";

const qtjavaSources = [qtjavaJavaSrcRoot + "/com/trolltech/qtjambi"];

const qtdesignerSources = [qtdesignerJavaSrcRoot + "/com/trolltech/qtdesigner/editors",
                           qtdesignerJavaSrcRoot + "/com/trolltech/qtdesigner/views",
                           qtdesignerJavaSrcRoot + "/com/trolltech/qtdesigner/"];

const directoriesP4 = [qtjambidir,                       
		       qtdesignerPackageDir,
                       javaScriptDir,
                       qtjavaPackageDir];


const qtjavaJarDest = rootDir + "/plugins/com.trolltech.qtjambi_1.0.0.jar";
const qtdesignerBinDir = "com/trolltech/qtdesigner";
const qtjavaBinDir = "com/trolltech/qtjambi";
const qtdesignerJarDest = qtdesignerJavaSrcRoot + "/QtDesigner.jar";
const mainQtDesignerJarDest = rootDir + "/plugins/com.trolltech.qtdesigner_1.0.0.jar";
const mainQtDesignerJarContents = ["plugin.xml",
                                   "icons/designer.gif",
                                   "QtDesigner.jar"];

const zipDest = (os_name() == OS_NAME_WINDOWS ? destDir + "/qt-jambi-eclipse-integration-1.0.0.zip" : destDir + "/qt-jambi-eclipse-integration-1.0.0.tar");
zipContents = ["plugins/com.trolltech.qtdesigner_1.0.0.jar",
               "plugins/com.trolltech.qtjambi_1.0.0.jar",
               "plugins/com.trolltech.help_1.0.0/doc.zip",
               "plugins/com.trolltech.help_1.0.0/plugin.xml",
               "plugins/com.trolltech.help_1.0.0/qt.xml",
               "features/com.trolltech.help_1.0.0/feature.xml"];


  const vcRedistributableDir = "/Progra~1/Micros~1.net/SDK/v1.1/Bin"
if (os_name() == OS_NAME_WINDOWS) {
  zipContents.push("register_eclipse_integration.bat");
  zipContents.push(generatedExe);
  zipContents.push("msvcp71.dll");
  zipContents.push("msvcr71.dll");
}


const qtjavaJarContents = ["plugin.xml",
                           "icons/designer.gif",
                           "com/trolltech/qtjambi/templates/Dialog_with_Buttons_Bottom.ui",
                           "com/trolltech/qtjambi/templates/Dialog_with_Buttons_Right.ui",
                           "com/trolltech/qtjambi/templates/Main_Window.ui",
                           "com/trolltech/qtjambi/templates/templates.txt",
                           "com/trolltech/qtjambi/templates/Widget.ui"];

const qtdesignerJarContents = [
                               qtdesignerBinDir + "/editors/actionicons/adjustsize.gif",
                               qtdesignerBinDir + "/editors/actionicons/buddytool.gif",
                               qtdesignerBinDir + "/editors/actionicons/editbreaklayout.gif",
                               qtdesignerBinDir + "/editors/actionicons/editgrid.gif",
                               qtdesignerBinDir + "/editors/actionicons/edithlayout.gif",
                               qtdesignerBinDir + "/editors/actionicons/edithlayoutsplit.gif",
                               qtdesignerBinDir + "/editors/actionicons/editlower.gif",
                               qtdesignerBinDir + "/editors/actionicons/editraise.gif",
                               qtdesignerBinDir + "/editors/actionicons/editvlayout.gif",
                               qtdesignerBinDir + "/editors/actionicons/editvlayoutsplit.gif",
                               qtdesignerBinDir + "/editors/actionicons/resourceeditortool.gif",
                               qtdesignerBinDir + "/editors/actionicons/signalslottool.gif",
                               qtdesignerBinDir + "/editors/actionicons/tabordertool.gif",
                               qtdesignerBinDir + "/editors/actionicons/widgettool.gif"];

var java_files_qtjava = [];
var java_files_qtdesigner = [];


commandP4 = find_executable("p4");
commandQmake = find_executable("qmake");
commandRM = find_executable("rm");
commandMake = make_from_qmakespec();
commandJavac = find_executable("javac");
commandJar = find_executable("jar");
commandCp = find_executable("cp");
commandZip = find_executable("zip");
commandGZip = find_executable("gzip");
commandTar = find_executable("tar");
commandWGet = find_executable("wget");
commandUnzip = find_executable("unzip");

// Actions to sync P4 tree
const actionsP4 = [
                    [commandP4, "sync -f ", directoriesP4, "/..."]
                  ];

// Actions to generate activeX control
const actionsAX = [
                    ["cd", generatorDir],
                    [commandQmake, "-config", " release"],
                    [commandMake + (os_name() == OS_NAME_WINDOWS ? " release" : "")],
                    [generatorExe],
                    ["cd", generatedDir],
                    [commandQmake, "-config", " release"],
                    [commandMake + (os_name() == OS_NAME_WINDOWS ? " release" : "")]
                  ];


// Actions to compile Java
const javaCommand_qtjava =
    os_name() == OS_NAME_WINDOWS
     ? [commandJavac, "-cp ", qtjavaJavaSrcRoot + PATH_SEPARATOR + qtdesignerJavaSrcRoot + PATH_SEPARATOR + eclipseRoot + " ", java_files_qtjava]
     : [commandJavac, java_files_qtjava];
const javaCommand_qtdesigner =
    os_name() == OS_NAME_WINDOWS
     ? [commandJavac, "-cp ", qtjavaJavaSrcRoot + PATH_SEPARATOR + qtdesignerJavaSrcRoot + PATH_SEPARATOR + eclipseRoot + " ", java_files_qtdesigner]
     : [commandJavac, java_files_qtdesigner];


const actionsJava = [
                     ["cd", qtdesignerJavaSrcRoot],
                     ["files", qtdesignerJavaSrcRoot, "java", java_files_qtdesigner],
                     javaCommand_qtdesigner,
                     ["cd", qtjavaJavaSrcRoot],
                     ["files", qtjavaJavaSrcRoot, "java", java_files_qtjava],
                     javaCommand_qtjava
                    ];

qtdesignerJarContentsStr = [""];
qtjavaJarContentsStr = [""];

platformSpecificCommand = [];
if (os_name() != OS_NAME_WINDOWS) {
    platformSpecificCommand.push(commandJar);
    platformSpecificCommand.push("-uf ");
    platformSpecificCommand.push(mainQtDesignerJarDest);
    platformSpecificCommand.push(" ");
    platformSpecificCommand.push(generatedExe);
}

if (os_name() == OS_NAME_WINDOWS) {
    packageCommand = [
                      ["cd", rootDir],
                      [commandCp, "-f ", cygdrive + vcRedistributableDir + "/" + "msvcp71.dll", " ."],
                      [commandCp, "-f ", cygdrive + vcRedistributableDir + "/" + "msvcr71.dll", " ."],
                      [commandCp, "-f ", cygdrive + rootDir + "/research/java/scripts/register_eclipse_integration.bat", " ."],
                      [commandCp, "-f ", cygdrive + generatedDir + "/release/" + generatedExe, " ."],
                      [commandWGet, "http://anarki.troll.no/~gunnar/packages/old_packages/com.trolltech.help_1.0.0.zip"],
		      [commandUnzip, "-o ", "com.trolltech.help_1.0.0.zip"],
                      [commandZip, cygdrive + zipDest + " ", zipContents],
    ];
} else {
    packageCommand = [
                      ["cd", rootDir],
                      [commandWGet, "http://anarki.troll.no/~gunnar/packages/com.trolltech.help_1.0.0.zip"],
		      [commandUnzip, "-o ", "com.trolltech.help_1.0.0.zip"],
                      [commandTar, "rf", " " + cygdrive + zipDest + " ",zipContents],
                      [commandRM, "-f ", cygdrive + zipDest + ".gz"],
                      [commandGZip, cygdrive + zipDest]
    ];
}

// Actions to package qtdesigner
const actionsPackage = [
                        ["md", rootDir + "/plugins"],
                        ["md", destDir],
                        ["cd", qtdesignerJavaSrcRoot],
                        [commandCp, "-f", " ../plugin.xml", " ."],
                        [commandCp, "-f -r", " ../icons", " ."],
                        ["files", qtdesignerJavaSrcRoot, "class", qtdesignerJarContents],
                        ["join", qtdesignerJarContentsStr, qtdesignerJarContents],
                        [commandJar, "-cf ", qtdesignerJarDest, " ", qtdesignerJarContentsStr],
                        [commandJar, "-cf ", mainQtDesignerJarDest, " ", mainQtDesignerJarContents.join(" ")],
                        ["cd", generatedDir],
                        platformSpecificCommand,
                        ["cd", "/home/qt/qtjambi/qtjambi-linux-preview/lib"],
                        ["cd", qtjavaJavaSrcRoot],
                        [commandCp, "-f", " ../plugin.xml", " ."],
                        [commandCp, "-f -r", " ../icons", " ."],
                        [commandCp, "-f -r", " ../resources/com", " ."],
                        ["files", qtjavaJavaSrcRoot, "class", qtjavaJarContents],
                        ["join", qtjavaJarContentsStr, qtjavaJarContents],
                        [commandJar, "-cfm ", qtjavaJarDest, " ../META-INF/MANIFEST.MF ", qtjavaJarContentsStr],
                        [commandRM, "-f ", cygdrive + zipDest],
                       ];


// Total build
const buildActions = [actionsP4,
                      actionsAX,
                      actionsJava,
                      actionsPackage,
                      packageCommand];


// Functions
function appendParam(strings, param)
{
    if (strings.length == 0)
        strings.push(new Array());
    if (!(param instanceof Array))
        param = new Array(param);

    var str = "";
    for (var i=strings.length-1; i>=0; --i) {
        var appended = "";
        if (i >= param.length) {
            appended = param[param.length - 1];
        } else {
            appended = param[i];
        }

        if (str == "" && param.length > strings.length)
            str = strings[i];

        strings[i] += appended;
    }

    if (param.length > strings.length) {
        for (var i=strings.length;i<param.length;++i) {
            strings.push(str + param[i]);
        }
    }
    return strings;
}

function mdAction(action)
{
    print("md " + action[1]);
    var dir = new Dir(action[1]);
    dir.mkdirs(action[1]);
}

function cdAction(action)
{
    print("cd to " + action[1]);
    var dir = new Dir(action[1]);
    if (dir.exists)
      dir.setCurrent();
}

function executeAction(action)
{
    var executeStrings = new Array();

    for (var i=1;i<action.length;++i) {
        executeStrings = appendParam(executeStrings, action[i]);
    }

    if (executeStrings.length == 0) {
        print("executing: " + action[0]);
        execute(action[0]);
    } else {
        for (var i=0;i<executeStrings.length;++i) {
            print("executing: " + new Array(action[0]).concat(executeStrings[i].split(" ")));
            execute(new Array(action[0]).concat(executeStrings[i].split(" ")));
        }
    }
}

function filesAction(action)
{
    print("Finding files in " + action[1] + " with extension " + action[2]);

    var array = find_files(action[1], [action[2]]);
    for (i=0;i<array.length;++i)
        action[3].push(array[i].substring(action[1].length + 1));
}

function joinAction(action)
{
    action[1][0] = action[2].join(" ");
}

function decideAction(action)
{
    if (action.length == 3 && action[0] == "join")
        joinAction(action);
    else if (action.length == 4 && action[0] == "files")
        filesAction(action);
    else if (action.length == 2 && action[0] == "md")
        mdAction(action);
    else if (action.length == 2 && action[0] == "cd")
        cdAction(action);
    else if (action.length > 0)
        executeAction(action);
}

function executeList(action_list)
{
    for (var i=0; i<action_list.length;++i) {
        decideAction(action_list[i]);
    }
}

function build(build_action_lists)
{
    System.setenv("P4PORT", "p4.troll.no:866");
    System.setenv("P4CLIENT", client);
    for (var i=0; i<build_action_lists.length; ++i) {
        executeList(build_action_lists[i]);
    }
}


// RUN!
build(buildActions);
