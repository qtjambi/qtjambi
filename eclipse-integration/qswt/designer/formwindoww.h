#ifndef FORMWINDOWW_H
#define FORMWINDOWW_H

#include <QtGui/QWidget>
#include <QtCore/QObject>
#include <QtCore/QPoint>

#include "qswt.h"
#include "widgethost.h"

class QDesignerFormEditorInterface;
class QDesignerFormWindowInterface;
class QDesignerFormWindowManagerInterface;
class QFile;

class FormWindowW : public WidgetHost
{
    Q_OBJECT
    Q_CLASSINFO("ClassID", "{3B33923D-9423-4662-A2D2-35CA60098A4E}")
    Q_CLASSINFO("InterfaceID", "{147566D3-D7EC-4745-BA9B-C7904FB800BF}")
    Q_CLASSINFO("EventsID", "{E4A3B9F5-DEBE-4CD5-83ED-34694204E0CE}")

public:
    FormWindowW(QWidget *parent = 0);
    ~FormWindowW();

    void signalChange(int id)
        { emit actionChanged(id); }
    
    bool eventFilter(QObject *watched, QEvent *e);

    //do nothing, use default always
    void setFont(const QFont &) { }
public slots:
    void open(QString fileName);
    bool save();
    bool saveAs(QString fileName);
    bool isDirty();
    void close();
    
    void setActiveFormWindow();
    
    // actions
    int actionCount();
    QString actionName(int id);
    QString actionToolTip(int id);
    void actionTrigger(int id);
    bool isEnabled(int id);
    
    // tools
    int toolCount();
    QString toolName(int index);
    QString toolToolTip(int index);
    int currentTool();
    void setCurrentTool(int index);

/*protected:
    void mouseReleaseEvent ( QMouseEvent * e );*/
    
signals:
    void actionChanged(int id);
    void checkActiveWindow();
    void resourceFilesChanged();
    void updateDirtyFlag();

private slots:
    void formSizeChanged(int w, int h);
    void formSelectionChanged();

    void formChanged();

private:
    bool save(QString fileName);
    QFile *m_file;
    QDesignerFormWindowInterface *m_form;
    QDesignerFormWindowManagerInterface *m_fwm;
    QDesignerFormEditorInterface *m_core;

    bool m_lastDirtyFlag;
};
    
#endif //FORMWINDOWW_H
