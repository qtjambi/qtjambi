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

package com.trolltech.tools.designer;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;

/**
 * The CustomWidgetManager class is used by the designer custom widget plugin to
 * load Java Widgets and expose them to designer.
 *
 * @author gunnar
 */
public class CustomWidgetManager {

    private CustomWidgetManager() {
        loadPlugins();
    }

    public static CustomWidgetManager instance() {
        if (instance == null)
            instance = new CustomWidgetManager();
        return instance;
    }

    public List<CustomWidget> customWidgets() {
        return customWidgets;
    }

    private void splitIntoList(String s, List<String> lst) {
        if (s != null)
            Collections.addAll(lst, s.split(File.pathSeparator));
    }

    private void loadPlugins() {
        warnings.clear();
        List<String> paths = new ArrayList<String>();

        splitIntoList(System.getenv("QT_PLUGIN_PATH"), paths);
        splitIntoList(System.getProperty("com.trolltech.qt.plugin-path"), paths);

        for (String path : paths) {
            loadPluginsFromPath(path + "/qtjambi");
        }

        if (warnings.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : warnings)
                sb.append(s).append("\n");
            QMessageBox.warning(null, "Custom widgets loading!", sb.toString());
        }
    }

    @SuppressWarnings("unused")
    private void loadPlugins(String path) {
        customWidgets.clear();
        String paths[] = path.split(System.getProperty("path.separator"));
        for (int i=0; i<paths.length; ++i) {
            loadPluginsFromPath(paths[i]);
        }
    }

    private void loadPluginsFromPath(String path) {
        QDir dir = new QDir(path);
        if (!new QFileInfo(dir.absolutePath()).exists()) {
            warn("CustomWidgetManager: plugin path doesn't exist: " + path);
            return;
        }

        List<String> nameFilters = new ArrayList<String>();
        nameFilters.add("*.xml");
        List<QFileInfo> plugins = dir.entryInfoList(nameFilters);

        for (QFileInfo fi : plugins) {
            loadPlugin(fi.absoluteFilePath());
        }
    }

    private void loadPlugin(String fileName) {
        QDomDocument doc = new QDomDocument();
        doc.setContent(new QFile(fileName));

        QDomElement root = doc.firstChild().toElement();

        QDomNodeList entries = root.childNodes();

        for (int i=0; i<entries.size(); ++i) {
            String errorPrefix = fileName + " : entry " + (i+1);

            QDomElement e = entries.at(i).toElement();

            String className = e.attribute("class");
            if (className.length() == 0) {
                warn(errorPrefix + "; missing 'class' attribute");
                continue;
            }

            try {
                Class type = null;
                try {
                    type = Class.forName(e.attribute("class"));
                } catch (ClassNotFoundException f) {
                    String classPathsProperty = System.getProperty("com.trolltech.qtjambi.internal.current.classpath");
                    if (classPathsProperty != null) {
                        String classpaths[] = classPathsProperty.split(System.getProperty("path.separator"));

                        URL urls[] = new URL[classpaths.length];
                        for (int j=0; j<classpaths.length; ++j)                            
                            urls[j] = new URL(classpaths[j]);                        

                        URLClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader());
                        type = loader.loadClass(e.attribute("class"));
                    }
                }

                if (type == null)
                    throw new NullPointerException("Failed to load class: " + className);

                CustomWidget customWidget = new CustomWidget(type);
                customWidgets.add(customWidget);

                // The simple properties...
                String group = e.attribute("group");
                if (group.length() == 0)
                    group = "Qt Jambi Custom Widgets";
                customWidget.setGroup(group);
                customWidget.setTooltip(e.attribute("tool-tip"));
                customWidget.setWhatsThis(e.attribute("whats-this"));
                customWidget.setName(e.attribute("name"));
                customWidget.setIncludeFile(e.attribute("import"));

                // The icon
                String iconPath = e.attribute("icon");
                QIcon icon = null;
                if (iconPath.length() != 0) {
                    icon = new QIcon(iconPath);
                    if (icon.isNull()) {
                        warn(errorPrefix + "; icon '" + iconPath + "' not loaded");
                        icon = null;
                    }
                }
                customWidget.setIcon(icon);

                // is it a container?
                boolean isContainer = false;
                String container = e.attribute("container");
                if (container != null) {
                    container = container.toLowerCase();
                    isContainer = container.equals("yes") || container.equals("true");
                }
                customWidget.setContainer(isContainer);

            } catch (Exception ex) {
                warn("class=" + className
                     + ", file=" + fileName
                     + ", error=" + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void warn(String s) {
        warnings.add(s);
    }

    private List<CustomWidget> customWidgets = new ArrayList<CustomWidget>();
    private List<String> warnings = new ArrayList<String>();

    private static CustomWidgetManager instance;



    public static void main(String[] args) {
        QApplication.initialize(args);

        List<CustomWidget> list = instance().customWidgets();
        for (CustomWidget w : list)
            w.createWidget(null).show();

        QApplication.exec();
    }

}
