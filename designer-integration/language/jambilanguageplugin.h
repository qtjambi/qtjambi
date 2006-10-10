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
#include <QtDesigner/private/qdesigner_propertysheet_p.h>

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


class JambiPropertySheet: public QDesignerPropertySheet
{
    Q_OBJECT

public:
    JambiPropertySheet(JambiLanguagePlugin *jambi, QObject *object, QObject *parent);
    virtual ~JambiPropertySheet();

    JambiLanguagePlugin *jambi() const { return m_jambi; }

    virtual int count() const;
    virtual bool hasReset(int index) const;
    virtual int indexOf(const QString & name) const;
    virtual bool isAttribute(int index) const;
    virtual bool isChanged(int index) const;
    virtual bool isVisible(int index) const;
    virtual QVariant property(int index) const;
    virtual QString propertyGroup(int index) const;
    virtual QString propertyName(int index) const;
    virtual bool reset(int index);
    virtual void setAttribute(int index, bool attribute);
    virtual void setChanged(int index, bool changed);
    virtual void setProperty(int index, const QVariant & value);
    virtual void setPropertyGroup(int index, const QString & group);
    virtual void setVisible(int index, bool visible);

private:
    void buildPropertySheet();

    QString callStringMethod_int(jmethodID mid, int i) const;
    bool callBoolMethod(jmethodID mid, int i) const;
    void call_int_bool(jmethodID mid, int i, bool b) const;

    JambiLanguagePlugin *m_jambi;
    QDesignerLanguageExtension *m_language;

    jobject m_property_sheet;
};

#endif // JAMBI_LANGUAGE_PLUGIN_H
