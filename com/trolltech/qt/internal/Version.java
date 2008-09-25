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

// !!NOTE!! This class can have no dependencies on Qt since
// it is used by the NativeLibraryManager

/**
 * The Version class contains the version number for the this version
 * of Qt Jambi as separate numbers and as a combined string.
 */
public class Version
{
    /**
     * The Major version of Qt Jambi. This version number follows the
     * version of the Qt/C++ library used.
     */
    public static final int MAJOR = 4;

    /**
     * The Minor version of Qt Jambi. This version number follows the
     * version of the Qt/C++ library used.
     */
    public static final int MINOR = 4;

    /**
     * The Patch version of Qt Jambi. This version number follows the
     * version of the Qt/C++ library used.
     */
    public static final int PATCH = 3;

    /**
     * The Build id for this version of Qt Jambi.
     */
    public static final int BUILD = 1;

    /**
     * A version string on the form "MAJOR.MINOR.PATCH_BUILD" where build is two
     * characters long...
     */
    public static final String STRING = String.format("%1$d.%2$d.%3$d_%4$02d",
                                                      MAJOR,
                                                      MINOR,
                                                      PATCH,
                                                      BUILD);
}

