
#ifndef JAMBI_CUSTOM_WIDGET_H
#define JAMBI_CUSTOM_WIDGET_H

#include <QtDesigner/QtDesigner>
#include <jni.h>

class JambiCustomWidget: public QObject, public QDesignerCustomWidgetInterface
{
    Q_OBJECT
    Q_INTERFACES(QDesignerCustomWidgetInterface)

public:
    JambiCustomWidget();
    virtual ~JambiCustomWidget();

    virtual bool isInitialized() const;
    virtual void initialize(QDesignerFormEditorInterface *core);

    virtual QWidget *createWidget(QWidget *parent);

    virtual bool isContainer() const;
    virtual QString group() const;
    virtual QString name() const;
    virtual QString toolTip() const;
    virtual QString whatsThis() const;
    virtual QString includeFile() const;
    virtual QIcon icon() const;

    QDesignerLanguageExtension *language() const;

private:
    QDesignerFormEditorInterface *m_core;
    jclass m_class;
    jobject m_object;
};

#endif // JAMBI_CUSTOM_WIDGET_H
