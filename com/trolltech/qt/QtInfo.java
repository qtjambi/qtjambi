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

package com.trolltech.qt;

/**
 * This class contains static functions to query information about the
 * Qt library Qt Jambi links against.
 */
public class QtInfo {

    static {
    	QtJambi_LibraryInitializer.init();
    }

    /**
     * Returns The Qt version as a string on the form Major.Minor.Patch, e.g 4.1.2
     * @return The Qt version string
     */
    public static String versionString() {
        return String.format("%1$d.%2$d.%3$d", majorVersion(), minorVersion(), patchVersion());
    }


    /**
     * Returns the Qt version as a hexadecimal coded integer on the format
     * 0x00MMmmpp, where MM is major version, mm is minor version and pp is
     * patch version.
     *
     * @return the Qt version as a hexadecimal coded integer
     */
    public static int version() {
        return (majorVersion() << 16) | (minorVersion() << 8) | patchVersion();
    }


    /**
     * @return Qt's major version
     */
    public native static int majorVersion();


    /**
     * @return Qt's minor version
     */
    public native static int minorVersion();


    /**
     * @return Qt's patch version
     */
    public native static int patchVersion();
}
