package com.trolltech.tools.designer;

import java.io.*;
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
        List<String> paths = new ArrayList<String>();
        
        splitIntoList(System.getenv("QT_PLUGIN_PATH"), paths);
        splitIntoList(System.getProperty("com.trolltech.qt.plugin-path"), paths);
        
        for (String path : paths) {
            loadPlugins(path);
        }
    }
    
    private void loadPlugins(String path) {
        QDir dir = new QDir(path + "/qtjambi");
        if (!new QFileInfo(dir.absolutePath()).exists()) {
            System.err.println("CustomWidgetManager: plugin path doesn't exist: " + path);
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
                Class type = Class.forName(e.attribute("class"));                
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
                ex.printStackTrace();
            }
        }
    }        
    
    private void warn(String s) {
        System.err.println("CustomWidgetManager: " + s);
    }
    
    private List<CustomWidget> customWidgets = new ArrayList<CustomWidget>();
    
    private static CustomWidgetManager instance;
    
    
    
    public static void main(String[] args) {
        QApplication.initialize(args);
        
        List<CustomWidget> list = instance().customWidgets();
        for (CustomWidget w : list)
            w.createWidget(null).show();
        
        QApplication.exec();
    }

}
