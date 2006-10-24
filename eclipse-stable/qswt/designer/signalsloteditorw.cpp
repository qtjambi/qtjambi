#include "signalsloteditorw.h"
#include "formeditorw.h"

#include <QResizeEvent>
#include <QtCore/QDebug>

SignalSlotEditorW::SignalSlotEditorW(QWidget *parent)
    : QWidget(parent)
{
    m_editor = QDesignerComponents::createSignalSlotEditor(FormEditorW::instance()->formEditor(), this);
}

SignalSlotEditorW::~SignalSlotEditorW()
{
    m_editor->hide();
    m_editor->setParent(0);
}

void SignalSlotEditorW::resizeEvent(QResizeEvent *event)
{
    m_editor->resize(event->size());
    QWidget::resizeEvent(event);
}

QSize SignalSlotEditorW::minimumSize()
{
    return m_editor->minimumSize();
}
