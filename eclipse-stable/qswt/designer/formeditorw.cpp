#include <qdebug.h>
#include <QtGui/QAction>
#include <QtCore/QPluginLoader>

#include <QtDesigner/private/qdesigner_integration_p.h>
#include "abstractformeditorplugin.h"

#include "propertyeditorw.h"
#include "objectinspectorw.h"
#include "formeditorw.h"
#include "widgetboxw.h"
#include "formwindoww.h"
#include "actioneditorw.h"
#include "signalsloteditorw.h"
#include "resourceeditorw.h"

FormEditorW *FormEditorW::m_self = 0;

FormEditorW::FormEditorW(QObject *parent)
    : QObject(parent)
{
    m_self = this;
    m_formeditor = QDesignerComponents::createFormEditor(parent);
    QDesignerComponents::initializePlugins(formEditor());
    QDesignerComponents::initializeResources();
    QDesignerComponents::createTaskMenu(m_formeditor, this);

    m_objectInspector = new ObjectInspectorW();
    m_propertyEditor = new PropertyEditorW();
    m_widgetBox = new WidgetBoxW();
    m_actionEditor = new ActionEditorW();
    m_signalSlotEditor = new SignalSlotEditorW();
    m_resourceEditor = new ResourceEditorW();
    new qdesigner_internal::QDesignerIntegration(m_formeditor, this);

    for(int id=0; id<=LastAction; ++id)
    {  
        connect(idToAction(id), SIGNAL(changed()), 
            new ActionChangedNotifier(this, id), SLOT(actionChanged()));
    }
    
    initializeCorePlugins();
    
//    (void)new TaskMenuComponent(this, this);
}

QAction *FormEditorW::idToAction(int id)
{
    QDesignerFormWindowManagerInterface *fwm = m_formeditor->formWindowManager();
    
    switch(id)
    {
    case ActionCut:
        return fwm->actionCut();
    case ActionCopy:
        return fwm->actionCopy();
    case ActionPaste:
        return fwm->actionPaste();
    case ActionDelete:
        return fwm->actionDelete();
    case ActionSelectAll:
        return fwm->actionSelectAll();
    case ActionUndo:
        return fwm->actionUndo();
    case ActionRedo:
        return fwm->actionRedo();
    case ActionLower:
        return fwm->actionLower();
    case ActionRaise:
        return fwm->actionRaise();
    case ActionHorizontalLayout:
        return fwm->actionHorizontalLayout();
    case ActionVerticalLayout:
        return fwm->actionVerticalLayout();
    case ActionSplitHorizontal:
        return fwm->actionSplitHorizontal();
    case ActionSplitVertical:
        return fwm->actionSplitVertical();
    case ActionGridLayout:
        return fwm->actionGridLayout();
    case ActionBreakLayout:
        return fwm->actionBreakLayout();
    case ActionAdjustSize:
        return fwm->actionAdjustSize();
    }
    
    return 0;
}

FormEditorW::~FormEditorW()
{
    delete m_objectInspector;
    delete m_propertyEditor;
    delete m_widgetBox;
    delete m_actionEditor;
    delete m_signalSlotEditor;
    delete m_resourceEditor;
}

FormEditorW *FormEditorW::instance(QObject *parent)
{
    if (!m_self)
        m_self = new FormEditorW(parent);
        
    return m_self;        
}

void FormEditorW::initializeCorePlugins()
{
    QList<QObject*> builtinPlugins = QPluginLoader::staticInstances();
    foreach (QObject *plugin, builtinPlugins) {
        if (QDesignerFormEditorPluginInterface *formEditorPlugin = 
                qobject_cast<QDesignerFormEditorPluginInterface*>(plugin)) {
            if (!formEditorPlugin->isInitialized())
                formEditorPlugin->initialize(m_formeditor);
        }
    }
}

// this function makes sure the toplevel widget is set to a visible widget
// we don't have a real top level widget.
bool FormEditorW::updateTopLevel(QWidget *delWidget)
{
    if (m_formeditor->topLevel() 
        && m_formeditor->topLevel()->isVisible() 
        && m_formeditor->topLevel() != delWidget)
        return true;
    
    // updating the widget
    if (m_formeditor->widgetBox() 
        && m_formeditor->widgetBox()->isVisible() 
        && m_formeditor->widgetBox() != delWidget)
    {
        m_formeditor->setTopLevel(m_formeditor->widgetBox());
        return true;
    }
        
        
/*    if (propertyEditor().isVisible() && (propertyEditor() != exceptWidget))
        setTopLevel(propertyEditor());
    if (objectInspector().isVisible() && (objectInspector() != exceptWidget))
        setTopLevel(objectInspector()); */
        
    if (QDesignerFormWindowManagerInterface *fwm = m_formeditor->formWindowManager())
    {
        for (int i=0; i<fwm->formWindowCount(); ++i)
        {
            if (fwm->formWindow(i)->isVisible()
                && fwm->formWindow(i) != delWidget)
            {
                m_formeditor->setTopLevel(fwm->formWindow(i));
                return true;
            }
        }
    }
    
    return false;
}

ActionChangedNotifier::ActionChangedNotifier(QObject *parent, int id)
    : QObject(parent)
{
    actId = id;
}
    
void ActionChangedNotifier::actionChanged()
{
    if(QDesignerFormWindowInterface *fw = 
        FormEditorW::instance()->formEditor()->formWindowManager()->activeFormWindow())
    {
        if(FormWindowW *fww = qobject_cast<FormWindowW *>(fw->parentWidget()->
            parentWidget()->parentWidget()->parentWidget()))
            fww->signalChange(actId);
    }
}

QSWT_MAIN_BEGIN("qtdesigner", "com.trolltech.qtdesigner.views", 
    "{3186D076-3FFA-4080-B788-8815E3078679}", "{E585D458-14CB-42A2-80CC-608252CB2781}")
    QSWT_CLASS(WidgetBoxW, "widgetboxw.h", "widgetboxw.cpp")
    QSWT_CLASS(PropertyEditorW, "propertyeditorw.h", "propertyeditorw.cpp")
    QSWT_CLASS(ObjectInspectorW, "objectinspectorw.h", "objectinspectorw.cpp")
    QSWT_CLASS(FormWindowW, "formwindoww.h", "formwindoww.cpp")
    QSWT_CLASS(ActionEditorW, "actioneditorw.h", "actioneditorw.cpp")
    QSWT_CLASS(SignalSlotEditorW, "signalsloteditorw.h", "signalsloteditorw.cpp")
    QSWT_CLASS(ResourceEditorW, "resourceeditorw.h", "resourceeditorw.cpp")
QSWT_MAIN_END()
