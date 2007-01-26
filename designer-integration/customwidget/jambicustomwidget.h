
#ifndef JAMBI_CUSTOM_WIDGET_H
#define JAMBI_CUSTOM_WIDGET_H

#include <QtDesigner/QtDesigner>
#include <qtjambi_global.h>

class JambiCustomWidget: public QObject, public QDesignerCustomWidgetInterface
{
    Q_OBJECT
    Q_INTERFACES(QDesignerCustomWidgetInterface)

public:
    JambiCustomWidget(jobject object);
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

    virtual QString domXml() const;


    QDesignerLanguageExtension *language() const;

private:
    QString callStringMethod(jmethodID method) const;

    QDesignerFormEditorInterface *m_core;
    jobject m_object;


};


class JambiCustomWidgetCollection : public QObject, public QDesignerCustomWidgetCollectionInterface
{
    Q_OBJECT
    Q_INTERFACES(QDesignerCustomWidgetCollectionInterface)

public:
    JambiCustomWidgetCollection();
    ~JambiCustomWidgetCollection();

    QList<QDesignerCustomWidgetInterface*> customWidgets() const;

public slots:
    void loadPlugins(const QString &path, QObject *widgetFactory);

private:
    void initializeWidgets(JNIEnv *env);

    jobject m_manager;
    jmethodID m_id_customWidgets;
    jmethodID m_id_loadPlugins;

    QList<QDesignerCustomWidgetInterface *> m_widgets;
};
#endif // JAMBI_CUSTOM_WIDGET_H
