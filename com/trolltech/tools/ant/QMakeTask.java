package com.trolltech.tools.ant;

import java.io.*;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

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
            
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ( (line = br.readLine()) != null)
                System.out.println(line);
            
            int returnValue = process.waitFor();
            if (returnValue == 0)
                System.out.println("OK");
            else {
                throw new BuildException("qmake exited with error: " + returnValue);
            }

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
