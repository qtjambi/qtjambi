/****************************************************************************
**
** Copyright (C) 2011 Darryl L. Miles.  All rights reserved.
** Copyright (C) 2011 D L Miles Consulting Ltd.  All rights reserved.
**
** This file is part of Qt Jambi.
**
**
** $BEGIN_LICENSE$
** GNU Lesser General Public License Usage
** This file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
** 
** In addition, as a special exception, the copyright holders grant you
** certain additional rights. These rights are described in the Nokia Qt
** LGPL Exception version 1.0, included in the file LGPL_EXCEPTION.txt in
** this package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 2.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL2 included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 2.0 requirements will be
** met: http://www.gnu.org/licenses/gpl-2.0.html
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL3 included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html
** $END_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.tools.ant;

import java.io.File;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;

public class ResolvePathTask extends Task {
    private boolean verbose;
    private boolean overwrite;
    private String path;
    private String var;

    public boolean isVerbose() {
        return verbose;
    }
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isOverwrite() {
        return overwrite;
    }
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public String getVar() {
        return var;
    }
    public void setVar(String var) {
        this.var = var;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public void execute() throws BuildException {
        if(var == null || var.length() == 0) {
            throw new BuildException("ResolvePathTask.var property is not set or empty string");
        }
        if(path == null || path.length() == 0) {
            throw new BuildException("ResolvePathTask.path property is not set or empty string");
        }

        File file = new File(path);
        if(!file.exists())
            throw new BuildException("Unable to resolve path \"" + path + "\" to absolute path");
        String newValue = file.getAbsolutePath();

        PropertyHelper props = PropertyHelper.getPropertyHelper(getProject());
        String oldPath = (String) props.getProperty(var);
        if(oldPath == null) {
            props.setNewProperty(var, newValue);
            if(verbose)
                System.out.println(var + "=\"" + newValue + "\"");
        } else {
            if(isOverwrite()) {
                props.setProperty(var, newValue, false);
                if(verbose)
                    System.out.println(var + "=\"" + newValue + "\" (old value=\"" + oldPath + "\")");
            }
        }
    }
}