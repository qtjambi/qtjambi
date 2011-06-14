/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
** 
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
** 
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** 
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.trolltech.qt.internal.*;

public class GeneratorTask extends Task {
    private String header = "";
    private String typesystem = "";
    private String inputDirectory;
    private String outputDirectory = ".";
    private String cppOutputDirectory;
    private String javaOutputDirectory;
    private String dir = ".";
    private String phononpath = "";
    private String kdephonon = "";
    private String options = null;
    private String qtIncludeDirectory = null;
    private String qtLibDirectory = null;
    private String jambiDirectory = null;
    private List<String> commandList = new ArrayList<String>();

    private String searchPath() {
        String s = File.separator;
        String prefix = "";
        if(jambiDirectory != null) {
            prefix = jambiDirectory + s;
        }
        switch(OSInfo.os()) {
        case Windows:
            return prefix + "generator\\release;generator\\debug";
        default:
            return prefix + "." + s + "generator";
        }
    }

    private String generatorExecutable() {
        switch (OSInfo.os()) {
            case Windows:
                return "\"" + Util.LOCATE_EXEC("generator.exe",
                        searchPath(), null).getAbsolutePath() + "\"";
            default:
                return Util.LOCATE_EXEC("generator",
                        searchPath(), null).getAbsolutePath();
        }
    }

    public void setOptions(String options) { this.options = options; }
    public String getOptions() { return options; }

    private String parseArgumentFiles(List<String> commandList) {
        File typesystemFile = Util.makeCanonical(typesystem);
        if (!typesystemFile.exists()) {
            throw new BuildException("Typesystem file '" + typesystem + "' does not exist.");
        }

        File headerFile;
        if("".equals(kdephonon)) {
            headerFile = Util.makeCanonical(header);
        } else {
            headerFile = Util.makeCanonical(kdephonon);
        }

        if (!headerFile.exists()) {
            throw new BuildException("Header file '" + header + "' does not exist.");
        }

        commandList.add(Util.escape(headerFile.getAbsolutePath()));
        commandList.add(Util.escape(typesystemFile.getAbsolutePath()));
        return " " + Util.escape(headerFile.getAbsolutePath()) + " " + Util.escape(typesystemFile.getAbsolutePath());
    }

    private boolean parseArguments() {
        if (options != null && !options.equals("")) {
            commandList.add(options);
        }

        if(!phononpath.equals("")) {
            commandList.add("--phonon-include=" + Util.escape(phononpath));
        }

        if(qtIncludeDirectory != null) {
            commandList.add("--qt-include-directory=" + Util.escape(qtIncludeDirectory));
        }

        if(qtLibDirectory != null) {
            commandList.add("--qt-lib-directory=" + Util.escape(qtLibDirectory));
        }

        if(inputDirectory != null && !inputDirectory.equals("")){
            File file = Util.makeCanonical(inputDirectory);
            if (!file.exists()) {
                throw new BuildException("Input directory '" + inputDirectory + "' does not exist.");
            }
            commandList.add("--input-directory=" + Util.escape(file.getAbsolutePath()));
        }

        if(!outputDirectory.equals("")){
            File file = Util.makeCanonical(outputDirectory);
            if (!file.exists()) {
                throw new BuildException("Output directory '" + outputDirectory + "' does not exist.");
            }
            commandList.add("--output-directory=" + Util.escape(file.getAbsolutePath()));
        }

        if(cppOutputDirectory != null && !cppOutputDirectory.equals("")){
            File file = Util.makeCanonical(cppOutputDirectory);
            if (!file.exists()) {
                throw new BuildException("CPP Output directory '" + cppOutputDirectory + "' does not exist.");
            }
            commandList.add("--cpp-output-directory=" + Util.escape(file.getAbsolutePath()));
        }

        if(javaOutputDirectory != null && !javaOutputDirectory.equals("")){
            File file = Util.makeCanonical(javaOutputDirectory);
            if (!file.exists()) {
                throw new BuildException("Java Output directory '" + javaOutputDirectory + "' does not exist.");
            }
            commandList.add("--java-output-directory=" + Util.escape(file.getAbsolutePath()));
        }

        parseArgumentFiles(commandList);

        return true;
    }

    //! TODO: remove when ant 1.7 is not anymore supported.
    @SuppressWarnings("deprecation")
    @Override
    public void execute() throws BuildException {

        parseArguments();
        String generator = generatorExecutable();
        List<String> thisCommandList = new ArrayList<String>();
        thisCommandList.add(generator);
        thisCommandList.addAll(commandList);
        System.out.println(thisCommandList.toString());

        PropertyHelper props = PropertyHelper.getPropertyHelper(getProject());
        String msyssupportStr = (String) props.getProperty((String) null, InitializeTask.MSYSBUILD);
        boolean msyssupport = false;
        if("true".equals(msyssupportStr)) {
            msyssupport = true;
        }
        Exec.execute(thisCommandList, new File(dir), qtLibDirectory, msyssupport);
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setTypesystem(String typesystem) {
        this.typesystem = typesystem;
    }

    public void setPhononpath(String path) {
        this.phononpath = path;
    }

    public void setKdephonon(String kdephonon) {
        this.kdephonon = kdephonon;
    }

    public void setJambidirectory(String dir) {
        this.jambiDirectory = dir;
    }

    public void setQtIncludeDirectory(String dir) {
        this.qtIncludeDirectory  = dir;
    }

    public void setQtLibDirectory(String dir) {
        this.qtLibDirectory  = dir;
    }

    public void setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setCppOutputDirectory(String cppOutputDirectory) {
        this.cppOutputDirectory = cppOutputDirectory;
    }

    public void setJavaOutputDirectory(String javaOutputDirectory) {
        this.javaOutputDirectory = javaOutputDirectory;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
