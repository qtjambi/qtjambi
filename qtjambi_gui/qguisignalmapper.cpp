#include "qguisignalmapper.h"

QGuiSignalMapper::QGuiSignalMapper() : QSignalMapper() 
{ 
    init();
}

QGuiSignalMapper::QGuiSignalMapper(QObject *parent) : QSignalMapper(parent) 
{ 
    init();
}

void QGuiSignalMapper::emitMapped(QWidget *widget) 
{
    // Make sure we emit mapped() exactly once for each emission of mappedQWidget()
    if (!emittingMapped) {
        emittingMapped = true;
        emit mapped(widget);
        emittingMapped = false;
    }
}

void QGuiSignalMapper::emitMappedQWidget(QWidget *widget) 
{
    // Make sure we emit mappedQWidget() exactly once for each emission of mapped()
    if (!emittingMappedQWidget) {
        emittingMappedQWidget = true;
        emit mappedQWidget(widget);
        emittingMappedQWidget = false;
    }
}

void QGuiSignalMapper::init() 
{
    // mappedQWidget is a replacement for the original signal
    connect(this, SIGNAL(mappedQWidget(QWidget *)), this, SLOT(emitMapped(QWidget*)));
    connect(this, SIGNAL(mapped(QWidget *)), this, SLOT(emitMappedQWidget(QWidget*)));

    emittingMappedQWidget = false;
    emittingMapped = false;
}

void QGuiSignalMapper::setMapping(QObject *sender, QWidget *widget)
{
    QSignalMapper::setMapping(sender, widget);
}

QObject *QGuiSignalMapper::mapping(QWidget *widget) const
{
    return QSignalMapper::mapping(widget);
}
