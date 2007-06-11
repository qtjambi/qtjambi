package com.trolltech.tools.ant;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class JuicTask extends MatchingTask {
    private String msg = "";
    private String classpath = "";
    private String outputDir = "";
    private String xmlConfigFile = "";
    private String trFunction = "";
    private String classNamePrefix = "";
    private boolean alwaysUpdate = false;

    public void execute() throws BuildException {
        System.out.println(msg);

        String arguments = "";
        if (!outputDir.equals(""))
            arguments += " -d " + outputDir;
        if (!xmlConfigFile.equals(""))
            arguments += " -d " + xmlConfigFile;
        if (!trFunction.equals(""))
            arguments += " -tr " + trFunction;
        if (!classNamePrefix.equals(""))
            arguments += " -pf" + classNamePrefix;
        if (alwaysUpdate)
            arguments += " -a ";

        StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            File dir = new File(tokenizer.nextToken());
            String comandPart = "juic " + arguments;

            DirectoryScanner ds = getDirectoryScanner(dir);
            String[] files = ds.getIncludedFiles();
            for (int i = 0; i < files.length; i++) {
                String file = files[i];
                String comand = comandPart + " -p " + file.substring(0, file.lastIndexOf(File.separator)) + " " + dir.getAbsolutePath() + File.separator + file;

                try {
                    Process process = Runtime.getRuntime().exec(comand);
                    int returnValue = process.waitFor();
                    if (returnValue == 0)
                        System.out.println(file + " done.");
                    else
                        System.out.println(file + " failed with error code: " + returnValue);

                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    public void setXmlConfigFile(String xmlConfigFile) {
        this.xmlConfigFile = xmlConfigFile;
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
