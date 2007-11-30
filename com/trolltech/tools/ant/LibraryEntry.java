package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.File;

public class LibraryEntry extends Task {

    public static final String TYPE_DEFAULT     = "user";
    public static final String TYPE_PLUGIN      = "plugin";
    public static final String TYPE_QT          = "qt";
    public static final String TYPE_QTJAMBI     = "qtjambi";

    public static final String LOAD_DEFAULT     = "default";
    public static final String LOAD_YES         = "yes";
    public static final String LOAD_NEVER       = "never";

    public static final String SUBDIR_DEFAULT   = "auto";


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

    @Override
    public void execute() throws BuildException {
        if (name == null || name.length() == 0)
            throw new BuildException("Required attribute 'name' missing");

        PropertyHelper h = PropertyHelper.getPropertyHelper(getProject());

        // Change subdir...
        if (subdir.equals("auto"))
            subdir = (String) h.getProperty(null, InitializeTask.LIBSUBDIR);

        // Fix name...
        if (type.equals(TYPE_PLUGIN))       name = formatPluginName(name);
        else if (type.equals(TYPE_QT))      name = formatQtName(name);
        else if (type.equals(TYPE_QTJAMBI)) name = formatQtJambiName(name);

        if (!load.equals(LOAD_YES) && !load.equals(LOAD_NEVER) && !load.equals(LOAD_DEFAULT))
            load = LOAD_DEFAULT;
    }


    private String formatPluginName(String name) {
        switch (Util.OS()) {
            case WINDOWS: return name + "4.dll";
            case MAC: return "lib" + name + ".dylib";
            case LINUX: return "lib" + name + ".so";
        }
        throw new BuildException("unhandled case...");
    }


    private String formatQtName(String name) {
        switch (Util.OS()) {
            case WINDOWS: return name + "4.dll";
            case MAC: return "lib" + name + ".4.dylib";
            case LINUX: return "lib" + name + ".so.4";
        }
        throw new BuildException("unhandled case...");
    }


    private String formatQtJambiName(String name) {
        switch (Util.OS()) {
            case WINDOWS: return name + ".dll";
            case MAC: return "lib" + name + ".jnilib";
            case LINUX: return "lib" + name + ".so";
        }
        throw new BuildException("unhandled case...");
    }


    private String type = TYPE_DEFAULT;
    private String name;
    private File rootpath;
    private String subdir = SUBDIR_DEFAULT;
    private String load = LOAD_DEFAULT;
}
