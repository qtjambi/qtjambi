package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;

import com.trolltech.qt.internal.*;

public class GeneratorTask extends Task{
    private String msg = "";
    private String header = "";
    private String typesystem = "";
    private String outputDirectory = ".";
    private String dir = ".";
    private String includePaths = "";
    private boolean silent = true;
    private String options;

    private String searchPath() {

        String s = File.separator;
        switch(OSInfo.os()) {
        case Windows:
            return "generator\\release;generator\\debug";
        default:
            return "." + s + "generator";
        }
    }

    private String generatorExecutable() {
        switch (OSInfo.os()) {
            case Windows: return "generator.exe";
            default: return "generator";
        }
    }

    public void setOptions(String options) { this.options = options; }
    public String getOptions() { return options; }

    @Override
    public void execute() throws BuildException {
        System.out.println(msg);
        String arguments = " " + options;

        if( !includePaths.equals("") ){
            arguments += " --include-paths=" + includePaths;
        }

        if( !outputDirectory.equals("")){
            File file = Util.makeCanonical(outputDirectory);
            if (!file.exists()) {
                throw new BuildException("Output directory '" + outputDirectory + "' does not exist.");
            }
            arguments += " --output-directory=" + file.getAbsolutePath();
        }

        File typesystemFile = Util.makeCanonical(typesystem);
        if (!typesystemFile.exists()) {
            throw new BuildException("Typesystem file '" + typesystem + "' does not exist.");
        }

        File headerFile = Util.makeCanonical(header);
        if (!headerFile.exists()) {
            throw new BuildException("Header file '" + header + "' does not exist.");
        }

        arguments += " " + headerFile.getAbsolutePath() + " " + typesystemFile.getAbsolutePath();

        String command = Util.LOCATE_EXEC(generatorExecutable(), searchPath(), null).getAbsolutePath() + arguments;

        Util.exec(command, new File(dir));
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
