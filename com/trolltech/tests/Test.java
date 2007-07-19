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
