#ifndef ACTIONEDITORW_H
#define ACTIONEDITORW_H

#include <QWidget>
#include "qswt.h"

class QDesignerActionEditorInterface;

class ActionEditorW : public QWidget
{
    Q_OBJECT
    Q_CLASSINFO("ClassID", "{E22724DC-B592-47A2-8ED2-83B690BF6300}")
    Q_CLASSINFO("InterfaceID", "{5277C2A2-C326-49E6-A4FC-AEBAA8AF1024}")
    Q_CLASSINFO("EventsID", "{85F2AE23-730D-41CE-B98D-F168FCC88205}")
public:
    ActionEditorW(QWidget *parent = 0);
    ~ActionEditorW();
    
    QSize minimumSize();
protected:
    void resizeEvent(QResizeEvent *event);
    
private:
    QDesignerActionEditorInterface *m_editor;
};
    
#endif //ACTIONEDITORW_H
