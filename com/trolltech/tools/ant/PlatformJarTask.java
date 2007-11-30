package com.trolltech.tools.ant;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class PlatformJarTask extends Task {


    public String getCacheKey() {
        return cacheKey;
    }


    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }


    public String getDestfile() {
        return destfile;
    }


    public void setDestfile(String destfile) {
        this.destfile = destfile;
    }


    public File getOutdir() {
        return outdir;
    }


    public void setOutdir(File outdir) {
        this.outdir = outdir;
    }


    @Override
    public void execute() throws BuildException {
        if (outdir == null)
            throw new BuildException("Missing required attribute 'outdir'. This directory is used for building the .jar file...");

        if (outdir.exists())
            throw new BuildException("Output directory: '" + outdir.getAbsolutePath() + "' already exists, aborting...");

        outdir.mkdirs();

        writeQtJambiDeployment();

        for (LibraryEntry e : libs)
            processLibraryEntry(e);
    }

    private void writeQtJambiDeployment() {
        // TODO: missing systemlibs...

        PrintWriter writer;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, "qtjambi-deployment.xml"))));
        } catch (IOException e) {
            e.printStackTrace();
            throw new BuildException("Failed to open 'qtjambi-deployment.xml' for writing in '" + outdir + "'");
        }

        writer.println("<qtjambi-deploy>");
        writer.println("  <cache key=\"" + cacheKey + "\" />");
        for (LibraryEntry e : libs) {
            String libraryName = e.getName();
            String subdir = e.getSubdir();
            String load = e.getLoad();

            writer.print("  <library name=\"" + subdir + "/" + libraryName + "\"");
            if (!load.equals(LibraryEntry.LOAD_DEFAULT))
                writer.print(" load=\"" + load + "\"");
            writer.println("/>");
        }

        writer.println("</qtjambi-deploy>");

        writer.close();
    }


    public void addConfiguredLibrary(LibraryEntry task) {
        task.perform();
        libs.add(task);
    }


    private void processLibraryEntry(LibraryEntry e) {
        File rootPath = e.getRootpath();
        String libraryName = e.getName();
        String subdir = e.getSubdir();
        String load = e.getLoad();

        File src = new File(rootPath, subdir + "/" + libraryName);
        File dest = new File(outdir, subdir + "/" + libraryName);
        try {
            Util.copy(src, dest);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new BuildException("Failed to copy library '" + libraryName + "'");
        }
    }


    private String destfile = "qtjambi-native.jar";
    private String cacheKey = "default";
    private File outdir;
    private List<LibraryEntry> libs = new ArrayList<LibraryEntry>();

}

