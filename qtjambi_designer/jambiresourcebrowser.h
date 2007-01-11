#ifndef JAMBIRESOURCEBROWSER_H
#define JAMBIRESOURCEBROWSER_H

#include <QtCore/QString>
#include <QtDesigner/QDesignerResourceBrowserInterface>

class JambiResourceBrowser: public QDesignerResourceBrowserInterface
{
    Q_OBJECT
public:
    JambiResourceBrowser(QWidget *parent) : QDesignerResourceBrowserInterface(parent) { }

public slots:
    virtual void updateRootDirs(const QString &paths) = 0;
};

#endif