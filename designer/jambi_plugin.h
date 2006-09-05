/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef QDESIGNER_JAMBI_PLUGIN_H
#define QDESIGNER_JAMBI_PLUGIN_H

#include <QtDesigner>
#include <QExtensionFactory>

#include <QtDesigner/private/qdesigner_propertysheet_p.h>

class JambiPlugin: public QObject, public QDesignerFormEditorPluginInterface
{
    Q_OBJECT
    Q_INTERFACES(QDesignerFormEditorPluginInterface)

public:
    JambiPlugin();
    virtual ~JambiPlugin();

    virtual bool isInitialized() const;
    virtual void initialize(QDesignerFormEditorInterface *core);
    virtual QAction *action() const;

    virtual QDesignerFormEditorInterface *core() const;

private:
    QDesignerFormEditorInterface *m_core;
};

class JambiLanguageExtension: public QObject, public QDesignerLanguageExtension
{
    Q_OBJECT
    Q_INTERFACES(QDesignerLanguageExtension)

public:
    JambiLanguageExtension(QObject *parent = 0);
    virtual ~JambiLanguageExtension();

    virtual QDialog *createFormWindowSettingsDialog(QDesignerFormWindowInterface *formWindow, QWidget *parentWidget);

    virtual QString enumerator(const QString &name) const;
    virtual QString neutralEnumerator(const QString &name) const;
};

class JambiExtensionFactory: public QExtensionFactory
{
    Q_OBJECT

public:
    JambiExtensionFactory(JambiPlugin *plugin, QExtensionManager *parent);
    virtual ~JambiExtensionFactory();

protected:
    virtual QObject *createExtension(QObject *object, const QString &iid, QObject *parent) const;

private:
    JambiPlugin *m_jambi;
};

class JambiPropertySheet: public QDesignerPropertySheet
{
    Q_OBJECT

public:
    JambiPropertySheet(JambiPlugin *jambi, QObject *object, QObject *parent);
    virtual ~JambiPropertySheet();

    JambiPlugin *jambi() const { return m_jambi; }

    virtual QVariant property(int index) const;
    virtual void setProperty(int index, const QVariant &v);

private:
    JambiPlugin *m_jambi;
    QDesignerLanguageExtension *m_language;
};

#endif // QDESIGNER_JAMBI_PLUGIN_H
