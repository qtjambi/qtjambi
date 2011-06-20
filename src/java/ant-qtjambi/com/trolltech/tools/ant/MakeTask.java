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

//NOTE: remove this after removing support for 1.7
@SuppressWarnings("deprecation")
public class MakeTask extends Task {

    private String msg = "";
    private String target;
    private String dir = ".";
    private boolean silent = false;
    @SuppressWarnings("unused") //used by ant
	private String compilationType = null;

    private String compilerName() {
        switch(OSInfo.os()){
        case Windows:
            PropertyHelper props = PropertyHelper.getPropertyHelper(getProject());
            String compiler = (String) props.getProperty((String) null, InitializeTask.COMPILER);

            if (FindCompiler.Compiler.MinGW.toString().equals(compiler)) {
                return "mingw32-make";
            }
            return "nmake";
        }
        return "make";
    }

    @Override
    public void execute() throws BuildException {
        System.out.println(msg);

        List<String> commandArray = new ArrayList<String>();                
        commandArray.add(compilerName());

        if (silent && OSInfo.os() != OSInfo.OS.Windows) {
        	commandArray.add("-s");
        }

        try {
            final String makeOptions = System.getenv("MAKEOPTS");
            if (makeOptions != null) {
            	commandArray.add(makeOptions);
            }
        } catch (SecurityException e) {
        } catch (NullPointerException e) {
            // Cannot happen
        }

        if (target != null)
            commandArray.add(target);

        PropertyHelper props = PropertyHelper.getPropertyHelper(getProject());
        String ldpath = (String) props.getProperty((String) null, InitializeTask.LIBDIR);
        Exec.execute(commandArray, new File(dir), getProject(), ldpath);
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setCompilationType(String type) {
        this.compilationType = type;
    }
}
