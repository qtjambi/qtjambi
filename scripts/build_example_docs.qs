var command = [];

command.qdoc = find_executable("qdoc3");
command.p4 = find_executable("p4");
command.cp = find_executable("cp");

const DEMO = "demos";
const EXAMPLE = "examples";

const input = [
            [ DEMO,     "Deform" ],
            [ DEMO,     "HelloGL" ],
            [ DEMO,     "ImageViewer" ],
            [ DEMO,     "MandelBrot" ],
            [ DEMO,     "TextEdit" ],
            [ EXAMPLE,  "AnalogClock" ],
            [ EXAMPLE,  "Application" ],
            [ EXAMPLE,  "CachedTable" ],
            [ EXAMPLE,  "Calculator" ],
            [ EXAMPLE,  "Chat" ],
            [ EXAMPLE,  "CollidingMice" ],
            [ EXAMPLE,  "ConfigDialog" ],
            [ EXAMPLE,  "CustomFilter" ],
            [ EXAMPLE,  "ElasticNodes" ],
            [ EXAMPLE,  "FridgeMagnets" ],
            [ EXAMPLE,  "GeneratorExample" ],
            [ EXAMPLE,  "I18N" ],
            [ EXAMPLE,  "ItemviewChart" ],
            [ EXAMPLE,  "LineEdits" ],
            [ EXAMPLE,  "Menus" ],
            [ EXAMPLE,  "ResourceSystem" ],
            [ EXAMPLE,  "Screenshot" ],
            [ EXAMPLE,  "ShapedClock" ],
            [ EXAMPLE,  "SimpleUIExample" ],
            [ EXAMPLE,  "SimpleHttpServer" ],
            [ EXAMPLE,  "SpinBoxes" ],
            [ EXAMPLE,  "StyleSheet" ],
            [ EXAMPLE,  "SvgCards" ],
            [ EXAMPLE,  "SyntaxHighlighter" ],
            [ EXAMPLE,  "SystemTrayExample" ],
            [ EXAMPLE,  "Tetrix" ],
            [ EXAMPLE,  "TreeModel" ],
            [ EXAMPLE,  "Wiggly" ]
];

var dir = new Dir();
dir.cdUp();
dir.setCurrent();

Process.execute([command.qdoc, "doc/config/qtjambi-examples.qdocconf"]);

var errors = [];

for (var i=0; i<input.length; ++i) {
    var current = input[i];
    var source = "doc/html/qtjambi-" + current[1].lower() + ".html";

    if (!File.exists(source)) {
        errors.push("missing for: " + current + ", '" + source + "'");
        continue;
    }

    var target = "com/trolltech/" + current[0] + "/" + current[1] + ".html";
    try {
        Process.execute([command.p4, "edit", target]);
        Process.execute([command.cp, source, target]);

        replace_in_file(target, "images/", "classpath:com/trolltech/images/");
        print("updated: " + target);
    } catch (e) {
        errors.push(e);
    }
}

Process.execute([command.p4, "add", "com/trolltech/...html"]);
Process.execute([command.p4, "revert", "-a", "com/trolltech/...html"]);
Process.execute([command.p4, "opened", "com/trolltech/...html"]);
print("HTML files pending after update...:\n" + Process.stdout);

for (var i=0; i<errors.length; ++i)
    print(errors[i]);
