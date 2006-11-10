#ifndef JAMBILANGUAGEEXTENSION_H
#define JAMBILANGUAGEEXTENSION_H

#include <QtDesigner/QtDesigner>

class QLineEdit;

class JambiResourceBrowser : public QDesignerResourceBrowserInterface
{
public:
    JambiResourceBrowser(QWidget *parent);

    virtual void setCurrentPath(const QString &filePath);
    virtual QString currentPath() const;

private:
    QLineEdit *m_lineEdit;
};

#endif
