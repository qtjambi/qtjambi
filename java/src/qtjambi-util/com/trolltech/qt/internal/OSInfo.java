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
        Unknown ("unkwnown"),
        Windows ("windows"),
        Linux ("linux"),
        MacOS ("macosx"),
        Solaris ("sunos");

        private String name;

        OS(final String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
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

