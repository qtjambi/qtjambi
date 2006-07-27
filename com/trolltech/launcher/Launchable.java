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
import com.trolltech.qt.gui.*;

import java.lang.reflect.*;

public class Launchable {

    private static class SourceFormatter {
	private static String KEYWORDS[] = new String[] {
	    "for ",
	    "if ",
	    "switch ",
	    " int ",
	    "const",
	    "void ",
	    "case ",
	    "double ",
	    "static",
	    "new",
	    "this",
	    "package",
	    "true",
	    "false",
	    "public",
	    "protected",
	    "private",
	    "final",
	    "native",
	    "import",
	    "extends",
	    "implements",
	    "else",
	    "class",
	    "super",
	};

	static public String format(String source) {
	    source = source.replace("&", "&amp;");
	    source = source.replace("<", "&lt;");
	    source = source.replace(">", "&gt;");

	    for (int i=0; i<KEYWORDS.length; ++i) {
		String keyword = KEYWORDS[i];
		source = source.replace(keyword, "<font color=olive>" + keyword + "</font>");
	    }
	    source = source.replace("(int ", "(<font color=olive><b>int </b></font>");
	    source = source.replaceAll("(\\d\\d?)", "<font color=navy>$1</font>");

	    String commentRe = "(//.+)\\n";
	    source = source.replaceAll(commentRe, "<font color=red>$1</font>\n");

	    String stringLiteralRe = "(\".+\")";
	    source = source.replaceAll(stringLiteralRe, "<font color=green>$1</font>");

 	    source = "<html style=\"white-space:pre-wrap;font-family:courier new\">" + source + "</html>";
	    return source;
	}
    } // end of SourceFormatter


    private QWidget m_widget;
    private String m_description;
    private String m_name;
    private String m_source;

    private Launchable(String name) {
	m_name = name;
    }

    public QWidget widget() {
	if (m_widget == null)
	    createWidget();
	return m_widget;
    }

    public String name() {
	return m_name;
    }

    public String description() {
	if (m_description == null)
	    loadDescription();
	return m_description;

    }

    public String source() {
	if (m_source == null)
	    loadSource();
	return m_source;
    }

    public void killWidget() {
        m_widget.disposeLater();
        m_widget = null;
    }

    private final String resourceFile(String fileType) {
	QFile f = new QFile("classpath:" + widget().getClass().getName().replace(".", "/") + "." + fileType);
	if (f.exists() && f.open(QFile.ReadOnly))
	    return f.readAll().toString();
	return null;
    }

    /**
     * Searches for the description and loads it if possible
     */
    private void loadDescription() {
	m_description = resourceFile("html");
	if (m_description == null)
	    m_description = "<i>No description</i>";
    }

    /**
       Searches for the source file and loads and HTMLifies it if possible.
    */
    private void loadSource() {
	m_source = resourceFile("java");
     	if (m_source == null) {
	    m_source = "<i>No source</i>";
	} else {
	    m_source = SourceFormatter.format(m_source);
	}
    }

    private void createWidget() {
	if (m_widget != null)
	    throw new RuntimeException("widget shouldn't exist at this point");

	try {
	    Class cl = Class.forName(m_name);

	    try {
		cl.getConstructor();
		m_widget = (QWidget) cl.newInstance();
	    } catch(Exception e) {
		Constructor constructor = cl.getConstructor(QWidget.class);
		m_widget = (QWidget) constructor.newInstance((QWidget) null);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static Launchable create(String className) {
	try {
            Class cl = Class.forName(className);
            if (Modifier.isPublic(cl.getModifiers())
                    && QWidget.class.isAssignableFrom(cl)) {

                try {
                    Method canInstantiate = cl.getMethod("canInstantiate");
                    if (!(Boolean) canInstantiate.invoke(null))
                        return null;
                } catch (NoSuchMethodException e) {
                    // supposed to happen.
                } catch (Exception e) {
                    return null;
                }

                Constructor constructor = null;
                try {
                    constructor = cl.getConstructor();
                } catch (Exception e) {
                    constructor = cl.getConstructor(QWidget.class);
                }

                if (constructor != null
                        && Modifier.isPublic(constructor.getModifiers())) {
                    Launchable l = new Launchable(className);
                    return l;
                }
            }
        } catch (Throwable e) {
            System.out.println("failed: " + className + ": "
                    + e.getClass().getName() + "; " + e.getMessage());
        }

        return null;
    }
}
