package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;

public class GeneratorTask extends Task{
    private String msg = "";
    private String header = "";
    private String typesystem = "";
    private String outputDirectory = ".";
    private String dir = ".";
    private String includePaths = "";
    private boolean silent = true;
  
    private String searchPath() {
        
        String s = File.separator;
        switch(Util.OS()){
        case WINDOWS:
            return "generator\\release;generator\\debug";
        case LINUX:
        case MAC:
            return "." + s + "generator";
        }

        return "";
    }

    private String generatorExecutable() {
        switch (Util.OS()) { 
            case WINDOWS: return "generator.exe";
            default: return "generator";
        }
    }
    
    @Override
    public void execute() throws BuildException {
        System.out.println(msg);
        String arguments = "";
        
        if( !includePaths.equals("") ){
            arguments += " --include-paths=" + includePaths; 
        }
        
        if( !outputDirectory.equals("")){
            arguments += " --output-directory=" + outputDirectory; 
        }
        
        arguments += " " + header + " " + typesystem;
        
        String comand = Util.LOCATE_EXEC(generatorExecutable(), searchPath(), null).getAbsolutePath() + arguments;
        
        System.out.println(comand);
        try {
            Process process = Runtime.getRuntime().exec(comand, null, new File(dir) );
            Util.redirectOutput(process, silent);
            System.out.println("OK");
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
    
    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
    
    public void setIncludePaths(String includePaths) {
        this.includePaths = includePaths;
    }
    
    public void setDir(String dir) {
        this.dir = dir;
    }
}
