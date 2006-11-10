#include "jambiresourcebrowser.h"

#include <QtGui/QLineEdit>
#include <QtGui/QVBoxLayout>

JambiResourceBrowser::JambiResourceBrowser(QWidget *parent)
    : QDesignerResourceBrowserInterface(parent)
{
    QVBoxLayout *layout = new QVBoxLayout(this);

    m_lineEdit = new QLineEdit();
    layout->addWidget(m_lineEdit);

    connect(m_lineEdit, SIGNAL(textChanged(QString)),
            this, SIGNAL(currentPathChanged(QString)));
}


void JambiResourceBrowser::setCurrentPath(const QString &filePath)
{
    m_lineEdit->setText(filePath);
}


QString JambiResourceBrowser::currentPath() const
{
    return m_lineEdit->text();
}

