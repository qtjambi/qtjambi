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

package com.trolltech.launcher;

import com.trolltech.qt.*;

import java.io.*;
import java.net.*;

public class LauncherOSX {

    // private static final String JAVA_PATH_MACOSX = "/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Commands/java";
    //private static final String PLUGINS_JAR_MACOSX = "qtjambi-mac-gpl-" + Utilities.VERSION_STRING + ".jar";

    private  static void copy(URL sourceUrl, String destination) throws IOException {
        URLConnection connection = sourceUrl.openConnection();
        if (connection instanceof JarURLConnection)
            sourceUrl = ((JarURLConnection) connection).getJarFileURL();
        else
            throw new IllegalArgumentException("bad input url...: " + sourceUrl);

        copy(sourceUrl.openStream(), new FileOutputStream(destination));
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte buffer[] = new byte[1024 * 64];
        while (in.available() > 0) {
            int read = in.read(buffer);
            out.write(buffer, 0, read);
        }
        in.close();
        out.close();
    }

/*    private static String plugins() {
        if (System.getProperty("os.name").toLowerCase().contains("mac os x"))
            return PLUGINS_JAR_MACOSX;
        return null;
    }
*/

    public static void main(String args[]) throws Exception {

        if (!System.getProperty("os.name").toLowerCase().contains("mac os x")) {
            Launcher.main(args);
            return;
        }

        String tmp = "/tmp/QtJambi_webstart/";
        new File(tmp).mkdirs();

        copy(Thread.currentThread().getContextClassLoader().getResource("com/trolltech/qt/QtJambiObject.class"),
             tmp + "/classes.jar");
        copy(Thread.currentThread().getContextClassLoader().getResource("com/trolltech/launcher/Launcher.class"),
             tmp + "/examples.jar");
        copy(Thread.currentThread().getContextClassLoader().getResource("libQtCore.4.dylib"),
             tmp + "/native.jar");

        StringBuffer cmd = new StringBuffer();

        String javaLocation = System.getProperty("java.home") + "/bin/";
        cmd.append(javaLocation + "java");

        // classpath...
        cmd.append(" -cp " + tmp + "/classes.jar:" + tmp + "/examples.jar:" + tmp + "/native.jar");

        cmd.append(" -XstartOnFirstThread");
        cmd.append(" -Dcom.trolltech.launcher.webstart=true");

        // the app itself...
        cmd.append(" com.trolltech.launcher.Launcher");

        System.out.println(cmd.toString());

        ProcessBuilder procBuilder = new ProcessBuilder(cmd.toString().split(" "));
        Process proc = procBuilder.start();

        proc.waitFor();
    }
}
