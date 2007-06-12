package com.trolltech.tools.ant;

import java.io.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class MakeTask extends Task {
    private String msg = "";
    private String target = "";
    private String dir = ".";
    private boolean silent = true;
  
    private String compilerName() {
        String os = System.getProperty("os.name");
        if(os.equalsIgnoreCase("linux"))
            return "make";
        if(os.equalsIgnoreCase("windows"))
            return "nmake";
        
        return "make";
    }
    
    public void execute() throws BuildException {
        System.out.println(msg);

        String arguments = "";
        
        if (silent)
            arguments += " -s";

        String comand = compilerName() + arguments + " " + target;
        
        System.out.println(dir + "  " + comand);
        try {
            Process process = Runtime.getRuntime().exec(comand, null, new File(dir) );
            
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ( (line = br.readLine()) != null)
                System.out.println(line);
            
            int returnValue = process.waitFor();
            if (returnValue == 0)
                System.out.println("OK");
            else {
                throw new BuildException("make exited with error: " + returnValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    
    public static void main(String[] args){
        MakeTask task = new MakeTask();
        task.setTarget("clean");
        task.setSilent(false);
        task.execute();
    }
}

