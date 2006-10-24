#ifndef RESOURCEEDITORW_H
#define RESOURCEEDITORW_H

#include <QWidget>
#include "qswt.h"

class ResourceEditorW : public QWidget
{
    Q_OBJECT
    Q_CLASSINFO("ClassID", "{C5DC3150-BB15-4FF2-90A7-390C63243968}")
    Q_CLASSINFO("InterfaceID", "{771D2E1D-C95D-43B4-83D2-46175DA788C5}")
    Q_CLASSINFO("EventsID", "{45F7CC7C-A09F-4767-B879-672E2F4C31A7}")
    Q_CLASSINFO("ToSuperClass", "ResourceEditorW")

public:
    ResourceEditorW(QWidget *parent = 0);
    ~ResourceEditorW();
    
    QSize minimumSize();
protected:
    void resizeEvent(QResizeEvent *event);
    
private:
    QWidget *m_editor;
};
    
#endif //RESOURCEEDITORW_H
