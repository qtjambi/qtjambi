package com.trolltech.tools.ant;

import java.io.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class GeneratorTask extends Task{
    private String msg = "";
    private String header = "";
    private String typesystem = "";
    private String dir = ".";
    private boolean silent = true;
  
    private String searchPath() {
        
        String s = File.separator;
        switch(Util.OS()){
        case WINDOWS:
            return "." + s + "generator" + s + "release";
        
        case LINUX:
        case MAC:
            return "." + s + "generator";
        }

        return "";
    }
    
    public void execute() throws BuildException {
        System.out.println(msg);
        String arguments = " " + header + " " + typesystem;
        
        String comand = Util.LOCATE_EXEC("generator", searchPath(), null).getAbsolutePath() + arguments;
        
        System.out.println(dir + "  " + comand);
        try {
            Process process = Runtime.getRuntime().exec(comand, null, new File(dir) );
            
            if(!silent){
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = null;
                while ( (line = br.readLine()) != null)
                    System.out.println(line);
            }
            
            int returnValue = process.waitFor();
            if (returnValue == 0)
                System.out.println("OK");
            else {
                throw new BuildException("Qt Jambi generator, exited with error: " + returnValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public void setHeader(String header) {
        this.header = header;
    }
    
    public void setTypesystem(String typesystem) {
        this.typesystem = typesystem;
    }
    
    public void setSilent(boolean silent) {
        this.silent = silent;
    }
    
    public void setDir(String dir) {
        this.dir = dir;
    }
}
