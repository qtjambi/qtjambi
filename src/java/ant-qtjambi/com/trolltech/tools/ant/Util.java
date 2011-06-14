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

import com.trolltech.qt.internal.OSInfo;
import com.trolltech.qt.internal.OSInfo.OS;

import java.io.*;
import java.util.*;

class Util {

    @Deprecated
    public static File LOCATE_EXEC(String name) {
        return LOCATE_EXEC(name, "", "");
    }

    @Deprecated
    public static File LOCATE_EXEC(String name, String prepend, String append) {
        String searchPath = "";

        if (prepend != null && !prepend.equals(""))
            searchPath += prepend + File.pathSeparator;

        searchPath += System.getenv("PATH");

        if (append != null && !append.equals(""))
            searchPath += File.pathSeparator + append;

        StringTokenizer tokenizer = new StringTokenizer(searchPath, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            File exec = new File(tokenizer.nextToken() + File.separator + name);
            if (exec.isFile())
                return makeCanonical(exec);
        }

        throw new BuildException("Could not find executable: " + name);
    }

    public static void redirectOutput(Process proc) {
        try {
            StreamConsumer std = new StreamConsumer(proc.getInputStream(), System.out);
            StreamConsumer err = new StreamConsumer(proc.getErrorStream(), System.err);
            std.start();
            err.start();
            proc.waitFor();
            std.join();
            err.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void copy(File src, File dst) throws IOException {
        File destDir = dst.getParentFile();
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte buffer[] = new byte[1024 * 64];
        while (in.available() > 0) {
            int read = in.read(buffer);
            out.write(buffer, 0, read);
        }
        in.close();
        out.close();
    }


    public static void copyRecursive(File src, File target) throws IOException {
        if (src.isDirectory()) {
            File entries[] = src.listFiles();
            for (File e : entries) {
                copyRecursive(e, new File(target, e.getName()));
            }
        } else {
            copy(src, target);
        }
    }

    public static String escape(String param) {
        OSInfo.os();
        if(OSInfo.os() == OS.Windows) {
            return "\"" + param + "\"";
        }
        return param;
    }

    public static File findInPath(String name) {
        String PATH[] = System.getenv("PATH").split(File.pathSeparator);
        for (String p : PATH) {
            File f = new File(p, name);
            if (f.exists())
                return f;
        }
        return null;
    }

    public static File findInLibraryPath(String name, String javaLibDir) {
        
        String libraryPath;
        if(javaLibDir != null) {
                libraryPath = javaLibDir;
            } else {
                libraryPath = System.getProperty("java.library.path");
            }
            //System.out.println("library path is: " + libraryPath);

        // Make /usr/lib an implicit part of library path
        if (OSInfo.os() == OSInfo.OS.Linux || OSInfo.os() == OSInfo.OS.Solaris)
            libraryPath += File.pathSeparator + "/usr/lib";

        String PATH[] = libraryPath.split(File.pathSeparator);
        for (String p : PATH) {
            File f = new File(p, name);
            if (f.exists()) {
                return f;
            }
        }
        return null;
    }


    public static File makeCanonical(String file) throws BuildException {
        return makeCanonical(new File(file));
    }

    public static File makeCanonical(File file) throws BuildException {
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            throw new BuildException("Path : " + file.getAbsolutePath() + " failed to create canonical form.", e);
        }
    }

}
