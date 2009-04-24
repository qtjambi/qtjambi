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
    public static final int MINOR = 5;

    /**
     * The Patch version of Qt Jambi. This version number follows the
     * version of the Qt/C++ library used.
     */
    public static final int PATCH = 0;

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

