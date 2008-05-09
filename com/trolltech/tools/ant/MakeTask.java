package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;

import com.trolltech.qt.internal.*;

public class MakeTask extends Task {
    private String msg = "";
    private String target = "";
    private String dir = ".";
    private boolean silent = false;

    private String compilerName() {
        switch(OSInfo.os()){
        case Windows:
            return "nmake";
        }
        return "make";
    }

    @Override
    public void execute() throws BuildException {
        System.out.println(msg);

        String arguments = "";

        if (silent && OSInfo.os() != OSInfo.OS.Windows)
            arguments += " -s";

        String command = compilerName() + arguments + " " + target;
        Util.exec(command, new File(dir));
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}

