package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.File;

public class PluginPath extends Task {

    public void execute() throws BuildException {
        if (path == null) {
            throw new BuildException("Missing required attribute 'path'...");
        }
    }


    public void setPath(String p) {
        path = p;
    }


    public String getPath() {
        return path;
    }

    private String path;
}
