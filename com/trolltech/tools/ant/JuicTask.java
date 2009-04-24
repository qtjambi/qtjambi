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
