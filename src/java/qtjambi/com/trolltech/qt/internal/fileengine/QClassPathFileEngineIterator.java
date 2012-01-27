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

package com.trolltech.qt.internal.fileengine;

import java.util.Collections;
import java.util.List;

import com.trolltech.qt.core.QAbstractFileEngine;
import com.trolltech.qt.core.QAbstractFileEngineIterator;
import com.trolltech.qt.core.QDir.Filters;

public class QClassPathFileEngineIterator extends QAbstractFileEngineIterator {
    private List<String> entries = null;
    private int index;

    public QClassPathFileEngineIterator(String path, Filters filters, List<String> nameFilters) {
        super(filters, nameFilters);

        System.out.println("QClassPathFileEngineIterator().ctor(\"" + path + "\", ...)");
        index = 0;
        QAbstractFileEngine engine = QAbstractFileEngine.create(path);
            entries = engine.entryList(filters, nameFilters);
        System.out.println("QClassPathFileEngineIterator().ctor(\"" + path + "\", ...) entries.size()="+entries.size());
    }

    // FIXME: Implement #currentFileInfo()

    @Override
    public String currentFileName() {
        return entries.get(index);
    }

    @Override
    public boolean hasNext() {
        return index < entries.size() - 1;
    }

    @Override
    public String next() {
        if(!hasNext())
            return "";
        index++;
        return currentFilePath();
    }
}
