package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;

public class MakeTask extends Task {
    private String msg = "";
    private String target = "";
    private String dir = ".";
    private boolean silent = true;
  
    private String compilerName() {
        switch(Util.OS()){
        case WINDOWS:
            return "nmake";
        }
        return "make";
    }
    
    @Override
    public void execute() throws BuildException {
        System.out.println(msg);

        String arguments = "";
        
        if (silent && Util.OS() != Util.OS.WINDOWS)
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

