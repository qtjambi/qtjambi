package com.trolltech.tools.designer;

import java.lang.reflect.*;

import com.trolltech.qt.gui.*;

public class CustomWidget {
    
    public CustomWidget(Class pluginClass) throws NoSuchMethodException {
        this.pluginClass = pluginClass;
        
        try {
            constructor = pluginClass.getConstructor(QWidget.class);
        } catch (NoSuchMethodException e) { 
            throw new NoSuchMethodException(pluginClass.getName() + "(QWidget parent) constructor missing");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public QWidget createWidget(QWidget parent) {
        QWidget widget = null;
        
        // Try the parent constructor first...
        try {
            widget = (QWidget) constructor.newInstance(parent);
        } catch (Exception e) { 
            e.printStackTrace();
        } 
        
        return widget;
    }

    public boolean isContainer() {
        return container;
    }

    public void setContainer(boolean container) {
        this.container = container;
    }

    public String group() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public QIcon icon() {
        return icon;
    }

    public void setIcon(QIcon icon) {
        this.icon = icon;
    }

    public String includeFile() {
        return includeFile;
    }

    public void setIncludeFile(String includeFile) {
        this.includeFile = includeFile;
    }

    public String name() {        
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class pluginClass() {
        return pluginClass;
    }

    public String tooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String whatsThis() {
        return whatsThis;
    }

    public void setWhatsThis(String whatsThis) {
        this.whatsThis = whatsThis;
    }

    private Class pluginClass;
    private String name;
    private boolean container;
    private String group = "Qt Jambi";
    private String tooltip;
    private String whatsThis;
    private String includeFile;
    private QIcon icon;
    private Constructor constructor;
}

