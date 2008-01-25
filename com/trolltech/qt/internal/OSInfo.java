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

package com.trolltech.qt.internal;

/**
 * The OSInfo class contains some basic information about the current
 * running system. The information is mostly retreived from
 * System.getProperty() and similar.
 */
public class OSInfo
{
    public enum OS
    {
        Unknown,
        Windows,
        Linux,
        MacOS,
        Solaris
    }

    /**
     * Returns the operating system
     */
    public static OS os() {
        if (os == null) {
            String osname = System.getProperty("os.name").toLowerCase();
            if (osname.contains("linux"))
                os = OS.Linux;
            else if (osname.contains("windows"))
                os = OS.Windows;
            else if (osname.contains("mac os x"))
                os = OS.MacOS;
            else if (osname.contains("sunos"))
                os = OS.Solaris;
            else
                os = OS.Unknown;
        }
        return os;
    }


    /**
     * Returns a string containing the operating system and
     * architecture name
     *
     * @return e.g. "win32" or "linux64"..
     */
    public static String osArchName() {
        if (osArchName == null) {
            switch (os()) {
            case Windows:
                osArchName = System.getProperty("os.arch").equalsIgnoreCase("amd64")
                             ? "win64"
                             : "win32";
                break;
            case Linux:
                osArchName = System.getProperty("os.arch").equalsIgnoreCase("amd64")
                             ? "linux64"
                             : "linux32";
                break;
            case MacOS:
                osArchName = "macosx";
                break;
            case Solaris:
                osArchName = "sunos";
                break;
            case Unknown:
                osArchName = "unknown";
                break;
            }
        }
        return osArchName;
    }

    private static OS os;
    private static String osArchName;
}

