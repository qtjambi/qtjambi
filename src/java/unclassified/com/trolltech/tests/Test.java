/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** $BEGIN_LICENSE$
** GNU Lesser General Public License Usage
** This file may be used under the terms of the GNU Lesser
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
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.tests;

import com.trolltech.qt.*;

import java.io.*;

public class Test extends QSignalEmitter {



    public static void main(String args[]) throws IOException, InterruptedException {
        String command[] = { "nmake", "clean" };

        File dir = new File("generator");

        ProcessBuilder procBuilder = new ProcessBuilder(command);
        procBuilder.directory(dir);
        procBuilder.redirectErrorStream();

        Process proc = procBuilder.start();
        System.out.println("started process...");

        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = null;
         while ( (line = br.readLine()) != null) {
             System.out.println("line from process: " + line);
        }

        proc.waitFor();

        System.out.println("all done...");
    }

}
