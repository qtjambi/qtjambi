#include "actioneditorw.h"
#include "formeditorw.h"

#include "abstractactioneditor.h"

#include <QResizeEvent>

ActionEditorW::ActionEditorW(QWidget *parent)
    : QWidget(parent)
{
    if (!FormEditorW::instance()->formEditor()->actionEditor()) {
        m_editor = QDesignerComponents::createActionEditor(FormEditorW::instance()->formEditor(), this);
        FormEditorW::instance()->formEditor()->setActionEditor(m_editor);
    } else {
        m_editor = FormEditorW::instance()->formEditor()->actionEditor();
        m_editor->setParent(this);
        m_editor->show();
    }
}

ActionEditorW::~ActionEditorW()
{
    m_editor->hide();
    m_editor->setParent(0);
}

void ActionEditorW::resizeEvent(QResizeEvent *event)
{
    m_editor->resize(event->size());
    QWidget::resizeEvent(event);
}

QSize ActionEditorW::minimumSize()
{
    return m_editor->minimumSize();
}
