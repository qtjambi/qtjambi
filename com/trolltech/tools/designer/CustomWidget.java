package com.trolltech.tools.designer;

import java.lang.reflect.*;

import com.trolltech.qt.gui.*;

public class CustomWidget {

    public CustomWidget(Class<?> pluginClass) throws NoSuchMethodException {
        this.pluginClass = pluginClass;
        constructor = pluginClass.getConstructor(QWidget.class);
    }

    public QWidget createWidget(QWidget parent) {
        QWidget widget = null;

        // Try the parent constructor first...
        try {
            widget = (QWidget) constructor.newInstance(parent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Avoid Designer seg-fault when the constructor throws an exception
        if (widget == null)
            widget = new QWidget(parent);

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

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("CustomWidget(").append(pluginClass.toString());

        if (name != null && name.length() != 0) s.append(",name=").append(name);
        if (group != null && group.length() != 0) s.append(",group=").append(group);
        if (tooltip != null && tooltip.length() != 0) s.append(",toolTip=").append(tooltip);
        if (whatsThis != null && whatsThis.length() != 0) s.append(",whatsThis=").append(whatsThis);
        if (includeFile != null && includeFile.length() != 0) s.append(",include=").append(includeFile);
        if (icon != null && !icon.isNull()) s.append(",icon=").append(icon);

        s.append(")");

        return s.toString();
    }

    private Class pluginClass;
    private String name;
    private boolean container;
    private String group;
    private String tooltip;
    private String whatsThis;
    private String includeFile;
    private QIcon icon;
    private Constructor constructor;
}

