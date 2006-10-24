#include "objectinspectorw.h"
#include "formeditorw.h"

#include "abstractobjectinspector.h"

#include <QResizeEvent>

ObjectInspectorW::ObjectInspectorW(QWidget *parent)
    : QWidget(parent)
{
    if (!FormEditorW::instance()->formEditor()->objectInspector()) {
        m_editor = QDesignerComponents::createObjectInspector(FormEditorW::instance()->formEditor(), this);
        FormEditorW::instance()->formEditor()->setObjectInspector(m_editor);
    } else {
        m_editor = FormEditorW::instance()->formEditor()->objectInspector();
        m_editor->setParent(this);
        m_editor->show();
    }
}

ObjectInspectorW::~ObjectInspectorW()
{
    m_editor->hide();
    m_editor->setParent(0);
}

void ObjectInspectorW::resizeEvent(QResizeEvent *event)
{
    m_editor->resize(event->size());
    QWidget::resizeEvent(event);
}

QSize ObjectInspectorW::minimumSize()
{
    return m_editor->minimumSize();
}
