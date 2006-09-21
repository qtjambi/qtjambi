#ifndef WIDGETBOXW_H
#define WIDGETBOXW_H

#include <QWidget>
#include "qswt.h"

class QDesignerWidgetBoxInterface;

class WidgetBoxW : public QWidget
{
    Q_OBJECT
    Q_CLASSINFO("ClassID", "{D9D8240E-5461-49D8-86B5-E52CAA7BBC4D}")
    Q_CLASSINFO("InterfaceID", "{660D2DFB-6525-47AE-81D9-F4E7A6C07F8E}")
    Q_CLASSINFO("EventsID", "{F562CFD4-893B-471F-B068-5052F4628639}")
    Q_CLASSINFO("ToSuperClass", "WidgetBoxW")

public:
    WidgetBoxW(QWidget *parent = 0);
    ~WidgetBoxW();
    
    QSize minimumSize();
protected:
    void resizeEvent(QResizeEvent *event);
    
private:
    QDesignerWidgetBoxInterface *m_editor;
    
};
    
#endif //WIDGETBOXW_H
