#ifndef OBJECTINSPECTORW_H
#define OBJECTINSPECTORW_H

#include <QWidget>
#include "qswt.h"

class QDesignerObjectInspectorInterface;

class ObjectInspectorW : public QWidget
{
    Q_OBJECT
    Q_CLASSINFO("ClassID", "{D8647F38-ACB0-43BD-9C6A-42EC88983D22}")
    Q_CLASSINFO("InterfaceID", "{FD8C3F28-1EC1-41BD-B64B-0CD4EA14F9F8}")
    Q_CLASSINFO("EventsID", "{30740FA1-BE77-4BDD-8F9C-A4A670864A64}")
public:
    ObjectInspectorW(QWidget *parent = 0);
    ~ObjectInspectorW();
    
    QSize minimumSize();
protected:
    void resizeEvent(QResizeEvent *event);
    
private:
    QDesignerObjectInspectorInterface *m_editor;
};
    
#endif //OBJECTINSPECTORW_H
