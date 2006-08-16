#include "formwindoww.h"
#include "formeditorw.h"

#include <QtCore/QDebug>
#include <QtCore/QFile>
#include <QtCore/QFileInfo>
#include <QtCore/QByteArray>
#include <QtCore/QEvent>
#include <QtGui/QKeyEvent>
#include <QtGui/QAction>

#include "abstractformwindowcursor.h"
#include "abstractformwindowtool.h"

FormWindowW::FormWindowW(QWidget *parent)
    : WidgetHost(parent)
{
    m_file = 0;
    m_lastDirtyFlag = true;

    m_fwm = FormEditorW::instance()->formEditor()->formWindowManager();
    m_core = FormEditorW::instance()->formEditor();
    m_core->setTopLevel(parent);
    m_form = m_fwm->createFormWindow(this);
    setFormWindow(m_form);

    connect(m_form, SIGNAL(selectionChanged()), 
        this, SLOT(formSelectionChanged()));
    connect(this, SIGNAL(formWindowSizeChanged(int,int)), 
        this, SLOT(formSizeChanged(int,int)));
    connect(m_form, SIGNAL(resourceFilesChanged()),
        this, SIGNAL(resourceFilesChanged()));
    connect(m_form, SIGNAL(changed()),
        this, SLOT(formChanged()));

    if(!FormEditorW::instance()->updateTopLevel())
        FormEditorW::instance()->formEditor()->setTopLevel(m_form);

    installEventFilter(this);

#ifdef Q_OS_WIN
    
#endif
}

bool FormWindowW::eventFilter(QObject *watched, QEvent *e)
{
    if (e->type() == QEvent::KeyPress || e->type() == QEvent::ShortcutOverride) {
        QKeyEvent *k = static_cast<QKeyEvent *>(e);
        QKeySequence ks(k->key() + k->modifiers());

        FormEditorW *fe = FormEditorW::instance();
        for (int i=7; i<=fe->LastAction; ++i) {
            if (fe->idToAction(i)->shortcut() == ks) {
                fe->idToAction(i)->trigger();
                break;
            }
        }
    }

    return QScrollArea::eventFilter(watched, e);
}

FormWindowW::~FormWindowW()
{
    close();
    FormEditorW::instance()->updateTopLevel(m_form);
    delete m_form;
}

void FormWindowW::close()
{
    if (m_file) {
        m_file->close();
        delete m_file;
        m_file = 0;
    }
}

void FormWindowW::open(QString fileName)
{
    m_file = new QFile(fileName);
    m_form->setContents(m_file);
    m_form->setFileName(fileName);

    QWidget *mc = m_form->mainContainer();
    
    if (mc) {
        setFormWindowSize(mc->size());

        if (mc->objectName().isEmpty())
            mc->setObjectName(QFileInfo(fileName).baseName());
    }
}

bool FormWindowW::save()
{
    return save(m_form->fileName());
}

bool FormWindowW::saveAs(QString fileName)
{
    if (!save(fileName))
        return false;
        
    m_form->setFileName(fileName);
    return true;
}

bool FormWindowW::save(QString fileName)
{
    QFile f(fileName);
    if (!f.open(QFile::WriteOnly))
        return false;
    
    QByteArray utf8Array = m_form->contents().toUtf8();
    
    if (f.write(utf8Array, utf8Array.size()) != utf8Array.size())
        return false;
    
    m_form->setDirty(false);
    return true;
}

bool FormWindowW::isDirty()
{
    m_lastDirtyFlag = m_form->isDirty();
    return m_lastDirtyFlag;
}

void FormWindowW::formChanged()
{
    if (m_lastDirtyFlag != m_form->isDirty()) {
        emit updateDirtyFlag();
    }
}

int FormWindowW::actionCount()
{
    return FormEditorW::LastAction+1;
}

QString FormWindowW::actionName(int id)
{
    if(QAction *act = FormEditorW::instance()->idToAction(id))
        return act->text();
    return QString("unknown id");
}

QString FormWindowW::actionToolTip(int id)
{
    if(QAction *act = FormEditorW::instance()->idToAction(id))
        return act->toolTip();
    
    return QString("error");
}

void FormWindowW::actionTrigger(int id)
{
    if(QAction *act = FormEditorW::instance()->idToAction(id))
        act->trigger();
}

bool FormWindowW::isEnabled(int id)
{
    if(QAction *act = FormEditorW::instance()->idToAction(id))
        return act->isEnabled();
    return false;
}

void FormWindowW::setActiveFormWindow()
{
    m_fwm->setActiveFormWindow(m_form);
    m_form->setFocus();
}

int FormWindowW::toolCount()
{
    return m_form->toolCount();
}

QString FormWindowW::toolName(int index)
{
    QDesignerFormWindowToolInterface *fwtool = m_form->tool(index);
    if(fwtool->action())
        return fwtool->action()->text();
        
    return QString("error");
}

QString FormWindowW::toolToolTip(int index)
{
    QDesignerFormWindowToolInterface *fwtool = m_form->tool(index);
    if(fwtool->action())
        return fwtool->action()->toolTip();
        
    return QString("error");
}

int FormWindowW::currentTool()
{
    return m_form->currentTool();
}

void FormWindowW::setCurrentTool(int index)
{
    m_form->setCurrentTool(index);
}

void FormWindowW::formSizeChanged(int, int)
{
    //###
}

void FormWindowW::formSelectionChanged()
{
    QDesignerFormWindowCursorInterface *cursor = 0;
    cursor = m_form->cursor();
    if (m_form == m_fwm->activeFormWindow() &&
        cursor && cursor->isWidgetSelected(m_form->mainContainer())) {
        WidgetHost::selectFormWindow();

        bool blocked = m_form->blockSignals(true);
        emit checkActiveWindow();            
        m_form->blockSignals(blocked);
    } else {
        WidgetHost::unSelectFormWindow();
    }    
}

/*void FormWindowW::mouseReleaseEvent(QMouseEvent *)
{
    FormEditorW::instance()->unselectAllFormWindows();
}*/
