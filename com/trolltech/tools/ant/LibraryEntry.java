package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.File;

public class LibraryEntry extends Task {

    public static final String TYPE_DEFAULT            = "user";
    public static final int VERSION_DEFAULT            = 4;

    public static final String TYPE_PLUGIN             = "plugin";
    public static final String TYPE_QT                 = "qt";
    public static final String TYPE_QTJAMBI            = "qtjambi";
    public static final String TYPE_UNVERSIONED_PLUGIN = "unversioned-plugin";

    public static final String LOAD_DEFAULT            = "default";
    public static final String LOAD_YES                = "yes";
    public static final String LOAD_NEVER              = "never";

    public static final String SUBDIR_DEFAULT          = "auto";

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getRootpath() {
        return rootpath;
    }

    public void setRootpath(File rootpath) {
        this.rootpath = rootpath;
    }

    public String getSubdir() {
        return subdir;
    }

    public void setSubdir(String subdir) {
        this.subdir = subdir;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public void setIf(boolean included) {
        this.included = included;
    }

    public boolean isIncluded() {
        return included;
    }

    @Override
    public void execute() throws BuildException {
        if (name == null || name.length() == 0)
            throw new BuildException("Required attribute 'name' missing");

        PropertyHelper h = PropertyHelper.getPropertyHelper(getProject());

        boolean debug = "debug".equals(h.getProperty(null, InitializeTask.CONFIGURATION));

        // Change subdir...
        if (subdir.equals("auto"))
            subdir = (String) h.getProperty(null, InitializeTask.LIBSUBDIR);

        // Fix name...
        if (type.equals(TYPE_PLUGIN))       name = formatPluginName(name, debug);
        else if (type.equals(TYPE_QT))      name = formatQtName(name, debug, version);
        else if (type.equals(TYPE_QTJAMBI)) name = formatQtJambiName(name, debug);
        else if (type.equals(TYPE_UNVERSIONED_PLUGIN)) name = formatUnversionedPluginName(name, debug);

        if (!load.equals(LOAD_YES) && !load.equals(LOAD_NEVER) && !load.equals(LOAD_DEFAULT))
            load = LOAD_DEFAULT;
    }


    public static String formatPluginName(String name, boolean debug) {
        if (debug) {
            switch (Util.OS()) {
            case WINDOWS: return name + "d4.dll";
            case MAC: return "lib" + name + "_debug.dylib";
            case LINUX: return "lib" + name + ".so";
            }
        } else {
            switch (Util.OS()) {
            case WINDOWS: return name + "4.dll";
            case MAC: return "lib" + name + ".dylib";
            case LINUX: return "lib" + name + ".so";
            }
        }
        throw new BuildException("unhandled case...");
    }



    public static String formatQtName(String name, boolean debug) {
        return formatQtName(name, debug, 4);
    }

    public static String formatQtName(String name, boolean debug, int version) {
        if (debug) {
            switch (Util.OS()) {
            case WINDOWS: return name + "d" + version + ".dll";
            case MAC: return "lib" + name + "_debug." + version + ".dylib";
            case LINUX: return "lib" + name + ".so." + version;
            }
        } else {
            switch (Util.OS()) {
            case WINDOWS: return name + version + ".dll";
            case MAC: return "lib" + name + "." + version + ".dylib";
            case LINUX: return "lib" + name + ".so." + version;
            }
        }
        throw new BuildException("unhandled case...");
    }

    public static String formatUnversionedPluginName(String name, boolean debug) {
        if (debug) {
            switch (Util.OS()) {
            case WINDOWS: return name + "d.dll";
            case MAC: return "lib" + name + "_debug.dylib";
            case LINUX: return "lib" + name + ".so";
            }
        } else {
            switch (Util.OS()) {
            case WINDOWS: return name + ".dll";
            case MAC: return "lib" + name + ".dylib";
            case LINUX: return "lib" + name + ".so";
            }
        }
        throw new BuildException("unhandled case...");
    }

    public static String formatQtJambiName(String name, boolean debug) {
        if (debug)  {
            switch (Util.OS()) {
            case WINDOWS: return name + "_debuglib.dll";
            case MAC: return "lib" + name + "_debuglib.jnilib";
            case LINUX: return "lib" + name + "_debuglib.so";
            }
        } else {
            switch (Util.OS()) {
            case WINDOWS: return name + ".dll";
            case MAC: return "lib" + name + ".jnilib";
            case LINUX: return "lib" + name + ".so";
            }
        }
        throw new BuildException("unhandled case...");
    }


    private String type = TYPE_DEFAULT;
    private int version = VERSION_DEFAULT;
    private String name;
    private File rootpath;
    private String subdir = SUBDIR_DEFAULT;
    private String load = LOAD_DEFAULT;
    private boolean included = true;
}
