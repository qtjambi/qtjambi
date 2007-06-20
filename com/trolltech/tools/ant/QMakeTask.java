package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;
import java.util.*;

public class QMakeTask extends Task {
    private String msg = "";
    private String config = "";
    private String dir = ".";

    private boolean recursive = false;

    public void execute() throws BuildException {
        System.out.println(msg);

        String arguments = "";
        StringTokenizer tokenizer = new StringTokenizer(config, " ");
        while (tokenizer.hasMoreTokens()) {
            arguments += " -config " + tokenizer.nextToken();
        }

        if (recursive)
            arguments += " -r ";

        String comand = "qmake" + arguments;
        System.out.println(comand);
        try {
            Process process = Runtime.getRuntime().exec(comand, null, new File(dir));
            Util.redirectOutput(process, true);
            System.out.println("OK");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public void setConfig(String config) {
        this.config = config;
    }
    
    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }
}
