#include "widgetboxw.h"
#include "formeditorw.h"

#include "abstractwidgetbox.h"

#include <QtCore/QDebug>
#include <QtCore/QDir>
#include <QResizeEvent>

WidgetBoxW::WidgetBoxW(QWidget *parent)
    : QWidget(parent)
{
    if (!FormEditorW::instance()->formEditor()->widgetBox()) {
        m_editor = QDesignerComponents::createWidgetBox(FormEditorW::instance()->formEditor(), this);
        FormEditorW::instance()->formEditor()->setWidgetBox(m_editor);
    } else {
        m_editor = FormEditorW::instance()->formEditor()->widgetBox();
        m_editor->setParent(this);
        m_editor->show();
    }

    bool ok = true;
    m_editor->setFileName(QLatin1String(":/trolltech/widgetbox/widgetbox.xml"));
    ok = m_editor->load();
    m_editor->setFileName(QDir::homePath() + QLatin1String("/.designer/widgetbox.xml"));
    ok = m_editor->load();

    if(!FormEditorW::instance()->updateTopLevel())
        FormEditorW::instance()->formEditor()->setTopLevel(m_editor);
}

WidgetBoxW::~WidgetBoxW()
{
    m_editor->hide();
    m_editor->setParent(0);

    FormEditorW::instance()->updateTopLevel();
}

void WidgetBoxW::resizeEvent(QResizeEvent *event)
{
    m_editor->resize(event->size());
    QWidget::resizeEvent(event);
}

QSize WidgetBoxW::minimumSize()
{
    return m_editor->minimumSize();
}
