#ifndef WINDOW_H
#define WINDOW_H

#include <QWidget>

class QtWindow : public QWidget
{
    Q_OBJECT
    Q_CLASSINFO("ClassID", "{4F8F7384-4A1D-4B44-B9C1-8656D7FDF49B}")
    Q_CLASSINFO("InterfaceID", "{9A87C0F8-EFB8-4FB1-90A8-20B16182BF45}")
    Q_CLASSINFO("EventsID", "{0FF9E0E2-9045-4E7F-B866-F3EECB22485D}")

public:
    QtWindow(QWidget *parent = 0);
    static QtWindow *instance(QWidget *parent = 0)
        { return new QtWindow(parent); }
    void dispose() { }
public slots:
    void showMessageBox();
    
signals:
    void messageBoxClosed();
};

#endif
