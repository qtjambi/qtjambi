/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.tools.ant;

import org.apache.tools.ant.*;

import java.io.*;
import java.util.*;

public class QMakeTask extends Task {
    private String msg = "";
    private String config = "";
    private String dir = ".";
    private String pro = "";

    private boolean recursive = false;
    private boolean debugTools = false;

    @Override
    public void execute() throws BuildException {
        System.out.println(msg);

        String arguments = "";
        StringTokenizer tokenizer = new StringTokenizer(config, " ");
        while (tokenizer.hasMoreTokens()) {
            arguments += " -config " + tokenizer.nextToken();
        }

        if (recursive)
            arguments += " -r ";

        if (debugTools)
            arguments += " DEFINES+=QTJAMBI_DEBUG_TOOLS";

        String command = "qmake" + arguments;

        if (!pro.equals("")) {
            command += " " + Util.makeCanonical(pro).getAbsolutePath();
        }

        Util.exec(command, new File(dir));
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public void setDebugTools(boolean debugTools) {
        this.debugTools = debugTools;
    }
}
