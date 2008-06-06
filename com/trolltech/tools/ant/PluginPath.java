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

import java.io.File;

public class PluginPath extends Task {

    public void execute() throws BuildException {
        if (path == null) {
            throw new BuildException("Missing required attribute 'path'...");
        }
    }


    public void setPath(String p) {
        path = p;
    }


    public String getPath() {
        return path;
    }

    private String path;
}
