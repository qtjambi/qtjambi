package com.trolltech.tools.ant;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;

import java.io.*;
import java.util.*;

public class JuicTask extends MatchingTask {
    private String msg = "";
    private String classpath = "";
    private String outputDir = "";
    private String trFunction = "";
    private String classNamePrefix = "";
    private boolean alwaysUpdate = false;

    public String executableName() {
        switch (Util.OS()) {
            case WINDOWS: return "juic.exe";
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
            File dir = new File(tokenizer.nextToken());

            try {
            DirectoryScanner ds = getDirectoryScanner(dir);
            String[] files = ds.getIncludedFiles();
                for (String file : files) {

                    file = file.replaceAll("\\\\", "/");

                    String packageString = file.substring(0, file.lastIndexOf('/')).replaceAll("/", ".");
                    String comand = comandPart + " -p " + packageString + " " + dir.getAbsolutePath() + '/' + file;

                    try {
                        Process process = Runtime.getRuntime().exec(comand);
                        Util.redirectOutput(process, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
