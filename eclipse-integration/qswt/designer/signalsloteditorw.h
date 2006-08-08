#ifndef SIGNALSLOTEDITORW_H
#define SIGNALSLOTEDITORW_H

#include <QWidget>
#include "qswt.h"

class SignalSlotEditorW : public QWidget
{
    Q_OBJECT
    Q_CLASSINFO("ClassID", "{72BD12D6-9599-4171-A76A-FB01DBE15D2C}")
    Q_CLASSINFO("InterfaceID", "{0B3F6E54-4B88-4B76-B87F-98F0CA076333}")
    Q_CLASSINFO("EventsID", "{25E5BEC7-FD60-4326-8718-785B8C92D3CF}")
public:
    SignalSlotEditorW(QWidget *parent = 0);
    ~SignalSlotEditorW();
    
    QSize minimumSize();
protected:
    void resizeEvent(QResizeEvent *event);
    
private:
    QWidget *m_editor;
};
    
#endif //SIGNALSLOTEDITORW_H
