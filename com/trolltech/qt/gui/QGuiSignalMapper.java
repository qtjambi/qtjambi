package com.trolltech.qt.gui;

public class QGuiSignalMapper extends com.trolltech.qt.core.QSignalMapper 
{
    public Signal1<QWidget> mappedQWidget = new Signal1<QWidget>();
    {
        makeConnection();
    }
    
    public QGuiSignalMapper() {
        super();
    }
    
    public QGuiSignalMapper(com.trolltech.qt.core.QObject parent) {
        super(parent);
    }
    
    @SuppressWarnings("deprecation")
    private void makeConnection()
    {
        mappedQWidget.connect(super.mappedQWidget);
        super.mappedQWidget.connect(mappedQWidget);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public final com.trolltech.qt.core.QObject mapping(QWidget widget) {
        return super.mapping(widget);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public final void setMapping(com.trolltech.qt.core.QObject sender, QWidget widget) {
        super.setMapping(sender, widget);
    }
}
