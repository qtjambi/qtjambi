#include "propertyeditorw.h"
#include "formeditorw.h"

#include "abstractpropertyeditor.h"

#include <QResizeEvent>

PropertyEditorW::PropertyEditorW(QWidget *parent)
    : QWidget(parent)
{
    if (!FormEditorW::instance()->formEditor()->propertyEditor()) {
        m_editor = QDesignerComponents::createPropertyEditor(FormEditorW::instance()->formEditor(), this);
        FormEditorW::instance()->formEditor()->setPropertyEditor(m_editor);
    } else {
        m_editor = FormEditorW::instance()->formEditor()->propertyEditor();
        m_editor->setParent(this);
        m_editor->show();
    }
}

PropertyEditorW::~PropertyEditorW()
{
    m_editor->hide();
    m_editor->setParent(0);
}

void PropertyEditorW::resizeEvent(QResizeEvent *event)
{
    m_editor->resize(event->size());
    QWidget::resizeEvent(event);
}

QSize PropertyEditorW::minimumSize()
{
    return m_editor->minimumSize();
}

