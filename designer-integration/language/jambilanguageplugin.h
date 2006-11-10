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

#ifndef JAMBI_LANGUAGE_PLUGIN_H
#define JAMBI_LANGUAGE_PLUGIN_H

#include "jambilanguageextension.h"

#include <QtDesigner/QtDesigner>
#include <QtDesigner/QExtensionFactory>

#include "jambiresourcebrowser.h"

#include <jni.h>

class JavaNameTable;

class JambiLanguagePlugin: public QObject, public QDesignerFormEditorPluginInterface
{
    Q_OBJECT
    Q_INTERFACES(QDesignerFormEditorPluginInterface)

public:
    JambiLanguagePlugin();
    virtual ~JambiLanguagePlugin();

    virtual bool isInitialized() const;
    virtual void initialize(QDesignerFormEditorInterface *core);
    virtual QAction *action() const;

    virtual QDesignerFormEditorInterface *core() const;

private:
    QDesignerFormEditorInterface *m_core;
};

class JambiLanguage: public QObject, public JambiLanguageExtension
{
    Q_OBJECT
    Q_INTERFACES(QDesignerLanguageExtension JambiLanguageExtension)

public:
    JambiLanguage(QObject *parent = 0);
    virtual ~JambiLanguage();

    virtual QDialog *createFormWindowSettingsDialog(QDesignerFormWindowInterface *formWindow, QWidget *parentWidget);

    virtual QDesignerResourceBrowserInterface *createResourceBrowser(QWidget *parentWidget);
    virtual bool isLanguageResource(const QString &path) const;

    virtual QString classNameOf(QObject *object) const;
    virtual QString enumerator(const QString &name) const;
    virtual QString neutralEnumerator(const QString &name) const;

private:
    JavaNameTable *m_name_table;
};

class JambiExtensionFactory: public QExtensionFactory
{
    Q_OBJECT

public:
    JambiExtensionFactory(JambiLanguagePlugin *plugin, QExtensionManager *parent);
    virtual ~JambiExtensionFactory();

protected:
    virtual QObject *createExtension(QObject *object, const QString &iid, QObject *parent) const;

private:
    JambiLanguagePlugin *m_jambi;
    QPointer<QDesignerLanguageExtension> m_language;
};


#endif // JAMBI_LANGUAGE_PLUGIN_H
