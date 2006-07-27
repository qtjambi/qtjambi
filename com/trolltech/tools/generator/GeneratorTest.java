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

package com.trolltech.tools.generator;

import java.util.*;

import com.trolltech.qt.core.QDir;

public class GeneratorTest {
	public static void main(String[] args) {
		TypeDatabase db = TypeDatabase.instance();
		
		QDir.setCurrent("generator");
		
		db.parseFile("build_all.txt");
		
		for (Map.Entry<String, TypeEntry> entry : db.entries().entrySet()) {
			System.out.printf("%s : %d\n", entry.getKey(), entry.getValue().type());
		}
		
	}

}
