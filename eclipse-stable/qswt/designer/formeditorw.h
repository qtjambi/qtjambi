#ifndef FORMEDITORW_H
#define FORMEDITORW_H

// Designer includes
#include <qdesigner_components.h>
#include <QtDesigner/private/qdesigner_integration_p.h>
#include <abstractformwindowmanager.h>
#include <abstractformeditor.h>
#include <abstractpropertyeditor.h>
#include <abstractwidgetbox.h>

class ObjectInspectorW;
class PropertyEditorW;
class WidgetBoxW;
class ActionEditorW;
class SignalSlotEditorW;
class ResourceEditorW;

class FormEditorW : public QObject
{
public:
    enum DesignerAction {
        // integrated ones
        ActionCut = 0,
        ActionCopy = 1,
        ActionPaste = 2,
        ActionDelete = 3,
        ActionSelectAll = 4,
        ActionUndo = 5,
        ActionRedo = 6,
        
        // additional ones
        ActionLower = 7,
        ActionRaise = 8,
        ActionHorizontalLayout = 9,
        ActionVerticalLayout = 10,
        ActionSplitHorizontal = 11,
        ActionSplitVertical = 12,
        ActionGridLayout = 13,
        ActionBreakLayout = 14,
        ActionAdjustSize = 15,
        LastAction = 15
    };

    static FormEditorW *instance(QObject *parent = 0);
    QAction *idToAction(int id);
    
    bool updateTopLevel(QWidget *delWidget = 0);
    inline QDesignerFormEditorInterface *formEditor() { return m_formeditor; }
    
private:
    FormEditorW(QObject *parent = 0);
    virtual ~FormEditorW();
    
    void initializeCorePlugins();
    static FormEditorW *m_self;
    QDesignerFormEditorInterface *m_formeditor;

    ObjectInspectorW *m_objectInspector;
    PropertyEditorW *m_propertyEditor;
    WidgetBoxW *m_widgetBox;
    ActionEditorW *m_actionEditor;
    SignalSlotEditorW *m_signalSlotEditor;
    ResourceEditorW *m_resourceEditor;
};

class ActionChangedNotifier : public QObject
{
    Q_OBJECT
public:
    ActionChangedNotifier(QObject *parent, int id);

public slots:
    void actionChanged();

private:
    int actId;
};
    
#endif //FORMEDITORW_H
