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

import com.trolltech.qt.core.*;


import java.util.*;

class LaunchableListModel extends QAbstractListModel
{
    private List<Launchable> m_list = new ArrayList<Launchable>();

    public void add(Launchable l) {	
	m_list.add(l);
	dataChanged.emit(createIndex(0, 0), createIndex(size()-1, 0));
    }

    public int size() { 
	return m_list.size();
    }

    public Launchable at(int i) {
	return m_list.get(i);
    }
    
    public Launchable at(QModelIndex index) {
	if (!index.isValid())
	    throw new ArrayIndexOutOfBoundsException("invalid index");
	return at(index.row());
    }

    // item view classes...
    public int rowCount(QModelIndex parent) {
	return size();
    }

    public Object data(QModelIndex index, int role) {
	if (role == Qt.DisplayRole)
	    return at(index.row()).name();
	return null;
    }
};
