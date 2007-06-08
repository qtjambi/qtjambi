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

        Utilities.loadSystemLibraries();

        Utilities.loadQtLibrary("QtCore");
        Utilities.loadQtLibrary("QtGui");
        Utilities.loadQtLibrary("QtXml");
        Utilities.loadQtLibrary("QtNetwork");
        Utilities.loadQtLibrary("QtOpenGL");
        Utilities.loadQtLibrary("QtSql");
        Utilities.loadQtLibrary("QtSvg");

        Utilities.loadJambiLibrary("qtjambi");

        Utilities.loadJambiLibrary("com_trolltech_qt_core");
        Utilities.loadJambiLibrary("com_trolltech_qt_gui");
        Utilities.loadJambiLibrary("com_trolltech_qt_xml");
        Utilities.loadJambiLibrary("com_trolltech_qt_network");
        Utilities.loadJambiLibrary("com_trolltech_qt_opengl");
        Utilities.loadJambiLibrary("com_trolltech_qt_sql");
        Utilities.loadJambiLibrary("com_trolltech_qt_svg");

        Utilities.unpackPlugins();

        String tmp = Utilities.jambiTempDir().getAbsolutePath();


        copy(Thread.currentThread().getContextClassLoader().getResource("com/trolltech/qt/QtJambiObject.class"),
             tmp + "/qtjambi.jar");
        copy(Thread.currentThread().getContextClassLoader().getResource("com/trolltech/launcher/Launcher.class"),
             tmp + "/qtjambi-launcher.jar");

        StringBuffer cmd = new StringBuffer();

        cmd.append("java");

        // classpath...
        cmd.append(" -cp " + tmp + "/qtjambi.jar:" + tmp + "/qtjambi-launcher.jar");

        // library path...
        cmd.append(" -Djava.library.path=" + tmp);

	cmd.append(" -XstartOnFirstThread");

        // the app itself...
        cmd.append(" com.trolltech.launcher.Launcher");

	System.out.println(cmd.toString());

        ProcessBuilder procBuilder = new ProcessBuilder(cmd.toString().split(" "));
        procBuilder.environment().put("QT_PLUGIN_PATH", tmp + "/plugins");
	procBuilder.environment().put("DYLD_LIBRARY_PATH", tmp);
        Process proc = procBuilder.start();

	proc.waitFor();
    }
}
