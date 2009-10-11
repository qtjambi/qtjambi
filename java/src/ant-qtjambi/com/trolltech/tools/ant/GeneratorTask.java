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

import com.trolltech.qt.internal.*;

public class GeneratorTask extends Task{
    private String msg = "";
    private String header = "";
    private String typesystem = "";
    private String outputDirectory = ".";
    private String dir = ".";
    private String includePaths = "";
    private boolean silent = true;
    private String options = null;

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
        String arguments = " ";

        if (options != null) {
            arguments += options + " ";
        }

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
