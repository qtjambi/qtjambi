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
    private String dir = ".";
    private String qtLibDirectory = null;

    public String executableName() {
        switch (OSInfo.os()) {
            case Windows: return "juic.exe";
            default: return "juic";
        }
    }

    @Override
    public void execute() throws BuildException {
        System.out.println(msg);

        List<String> commandList = new ArrayList<String>();
        commandList.add(Util.escape(Util.LOCATE_EXEC(executableName(), "./bin", null).getAbsolutePath()));
        if (!outputDir.equals("")) {
            commandList.add("-d");
            commandList.add(Util.escape(outputDir));
        }
        if (!trFunction.equals("")) {
            commandList.add("-tr");
            commandList.add(Util.escape(trFunction));
        }
        if (!classNamePrefix.equals("")) {
            commandList.add("-pf");
            commandList.add(Util.escape(classNamePrefix));
        }
        if (alwaysUpdate) {
            commandList.add("-a");
        }

        StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            File dirToScan = Util.makeCanonical(tokenizer.nextToken());

            DirectoryScanner ds = getDirectoryScanner(dirToScan);
            String[] files = ds.getIncludedFiles();
            for (String file : files) {

                file = file.replaceAll("\\\\", "/");

                List<String> thisCommandList = new ArrayList<String>();
                thisCommandList.addAll(commandList);
                String packageString = Util.escape(file.substring(0, file.lastIndexOf('/')).replaceAll("/", "."));
                String uicFileString = Util.escape(dirToScan.getAbsolutePath() + '/' + file);
                thisCommandList.add("-p");
                thisCommandList.add(packageString);
                thisCommandList.add(uicFileString);

                Exec.execute(thisCommandList, new File(dir), getProject(), qtLibDirectory);
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

    public void setDir(String dir) {
    	this.dir  = dir;
    }

    public void setQtLibDirectory(String dir) {
    	this.qtLibDirectory  = dir;
    }
}
