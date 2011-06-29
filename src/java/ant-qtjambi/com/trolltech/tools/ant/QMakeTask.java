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
import java.util.*;

public class QMakeTask extends Task {

    private String msg = "";
    private String config = "";
    private String dir = ".";
    private String pro = "";
    
    //parameters
    private String qtconfig = null;
    private String includepath = null;
    private String qmakebinary = null;

    private boolean recursive = false;
    private boolean debugTools = false;
    
    private List<String> parseArguments() {
	List<String> arguments = new ArrayList<String>();

        StringTokenizer tokenizer = new StringTokenizer(config, " ");
        
        while (tokenizer.hasMoreTokens()) {
            arguments.add("-config");
            arguments.add(tokenizer.nextToken());
        }

        if (recursive) {
            arguments.add("-r");
        }
        if (debugTools) {
            arguments.add("DEFINES+=QTJAMBI_DEBUG_TOOLS");
        }
        
        return arguments;
    }
    
    private List<String> parseParameters() {
	List<String> parameters = new ArrayList<String>();

	if (qtconfig != null) {
	    parameters.add("QT_CONFIG+=" + qtconfig);
	}

	if (includepath != null) {
	    parameters.add("INCLUDEPATH+=" + includepath);
	}

	return parameters;
    }

    @Override
    public void execute() throws NullPointerException {
	System.out.println(msg);

	String proFile = "";
	if (!pro.equals("")) {
	    proFile = Util.makeCanonical(pro).getAbsolutePath();
	}
	
	final List<String> command =  new ArrayList<String>();
	
	command.add(qmakebinary);
	command.add(proFile);

        List<String> arguments = parseArguments();
        if (!arguments.isEmpty())
            command.addAll(arguments);

        List<String> parameters = parseParameters();
        if (!parameters.isEmpty())
	    command.addAll(parameters);
	
	Exec.execute(command, new File(dir), getProject());
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public void setConfig(String config) {
        this.config = config;
    }
    
    public void setQtconfig(String config) {
    	this.qtconfig = config;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
    
    public void setQmakebinary(String binary) {
        this.qmakebinary = binary;
    }
    
    public void setIncludepath(String path) {
    	this.includepath = path;
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
