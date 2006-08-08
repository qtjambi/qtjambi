#include "resourceeditorw.h"
#include "formeditorw.h"

#include <QResizeEvent>
#include <QtCore/QDebug>

ResourceEditorW::ResourceEditorW(QWidget *parent)
    : QWidget(parent)
{
    m_editor = QDesignerComponents::createResourceEditor(FormEditorW::instance()->formEditor(), this);
}
    
ResourceEditorW::~ResourceEditorW()
{
    m_editor->hide();
    m_editor->setParent(0);
}
 
void ResourceEditorW::resizeEvent(QResizeEvent *event)
{
    m_editor->resize(event->size());
    QWidget::resizeEvent(event);
}

QSize ResourceEditorW::minimumSize()
{
    return m_editor->minimumSize();
}
