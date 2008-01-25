package com.trolltech.tools.ant;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;

import java.io.*;
import java.util.*;

import com.trolltech.qt.internal.*;

public class JuicTask extends MatchingTask {
    private String msg = "";
    private String classpath = "";
    private String outputDir = "";
    private String trFunction = "";
    private String classNamePrefix = "";
    private boolean alwaysUpdate = false;

    public String executableName() {
        switch (OSInfo.os()) {
            case Windows: return "juic.exe";
            default: return "juic";
        }
    }

    @Override
    public void execute() throws BuildException {
        System.out.println(msg);

        String arguments = "";
        if (!outputDir.equals(""))
            arguments += " -d " + outputDir;
        if (!trFunction.equals(""))
            arguments += " -tr " + trFunction;
        if (!classNamePrefix.equals(""))
            arguments += " -pf" + classNamePrefix;
        if (alwaysUpdate)
            arguments += " -a ";

        String comandPart = Util.LOCATE_EXEC(executableName(), "./bin", null).getAbsolutePath() + arguments;

        StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            File dir = Util.makeCanonical(tokenizer.nextToken());
            
            DirectoryScanner ds = getDirectoryScanner(dir);
            String[] files = ds.getIncludedFiles();
            for (String file : files) {

                file = file.replaceAll("\\\\", "/");

                String packageString = file.substring(0, file.lastIndexOf('/')).replaceAll("/", ".");
                String command = comandPart + " -p " + packageString + " " + dir.getAbsolutePath() + '/' + file;

                Util.exec(command);
            }
        }
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public void setTrFunction(String trFunction) {
        this.trFunction = trFunction;
    }

    public void setClassNamePrefix(String classNamePrefix) {
        this.classNamePrefix = classNamePrefix;
    }

    public void setAlwaysUpdate(boolean alwaysUpdate) {
        this.alwaysUpdate = alwaysUpdate;
    }
}
