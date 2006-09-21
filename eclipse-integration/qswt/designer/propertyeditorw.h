#ifndef PROPERTYEDITORW_H
#define PROPERTYEDITORW_H

#include <QWidget>
#include "qswt.h"

class QDesignerPropertyEditorInterface;

class PropertyEditorW : public QWidget
{
    Q_OBJECT
    Q_CLASSINFO("ClassID", "{29B6C1B8-5163-4FFC-B762-ABD202FCA4B0}")
    Q_CLASSINFO("InterfaceID", "{2EAF3EA8-438B-46EC-BDA9-1A3A8CE28609}")
    Q_CLASSINFO("EventsID", "{ACE7282C-6385-49F1-9192-316EB8893F75}")
    Q_CLASSINFO("ToSuperClass", "PropertyEditorW")

public:
    PropertyEditorW(QWidget *parent = 0);
    ~PropertyEditorW();
    
    QSize minimumSize();
protected:
    void resizeEvent(QResizeEvent *event);
        
private:
    QDesignerPropertyEditorInterface *m_editor;
};
    
#endif //PROPERTYEDITORW_H
