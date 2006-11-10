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

#include "jambilanguageplugin.h"
#include "jambipropertysheet.h"

#include "javanametable.h"

#include "qtjambi_core.h"

#include <QtDebug>
#include <QtPlugin>


#include <QtGui/QMessageBox>


JambiLanguagePlugin::JambiLanguagePlugin():
    m_core(0)
{
    printf("JambiLanguagePlugin: created\n");
}

JambiLanguagePlugin::~JambiLanguagePlugin()
{
}

bool JambiLanguagePlugin::isInitialized() const
{
    return m_core != 0;
}

void JambiLanguagePlugin::initialize(QDesignerFormEditorInterface *core)
{
    if (m_core)
        return;

    m_core = core;

    QExtensionManager *mgr = m_core->extensionManager();
    Q_ASSERT (mgr != 0);

    mgr->registerExtensions(new JambiExtensionFactory(this, mgr), Q_TYPEID(QDesignerLanguageExtension));
    mgr->registerExtensions(new JambiExtensionFactory(this, mgr), Q_TYPEID(QDesignerPropertySheetExtension));

    qtjambi_initialize_vm();
}

QAction *JambiLanguagePlugin::action() const
{
    printf("action being called...\n");
    return 0;
}

QDesignerFormEditorInterface *JambiLanguagePlugin::core() const
{
    return m_core;
}

JambiLanguage::JambiLanguage(QObject *parent)
    : QObject(parent)
{
    m_name_table = JavaNameTable::instance();
    printf("JambiLanguage: created\n");
}

JambiLanguage::~JambiLanguage()
{
}

QDialog *JambiLanguage::createFormWindowSettingsDialog(QDesignerFormWindowInterface *, QWidget *)
{
    return 0;
}

QString JambiLanguage::classNameOf(QObject *object) const
{
    printf("JambiLanguage: classname of: %s\n", object->metaObject()->className());
    QtJambiLink *link = QtJambiLink::findLinkForQObject(object);
    if (link && link->createdByJava()) {
        JNIEnv *env = qtjambi_current_environment();
        jobject jobj = link->javaObject(env);
        QString name = qtjambi_class_name(env, env->GetObjectClass(jobj));
        return name;
    }
    return object->metaObject()->className();
}

QString JambiLanguage::enumerator(const QString &name) const
{
    QString javaName = m_name_table->javaEnum(name);
    printf("JambiLanguage: enumerator: %s -> %s\n", qPrintable(name), qPrintable(javaName));
    return javaName.isEmpty() ? name : javaName.split('.').last();
}

QString JambiLanguage::neutralEnumerator(const QString &name) const
{
    QString cppName = m_name_table->cppEnum(name);
    printf("JambiLanguage: neutralEnumerator: %s -> %s\n", qPrintable(name), qPrintable(cppName));
    return cppName.isEmpty() ? name : cppName;
}

QDesignerResourceBrowserInterface *JambiLanguage::createResourceBrowser(QWidget *parentWidget)
{
    printf("JambiLanguage::createResourceBrowser()\n");
    return new JambiResourceBrowser(parentWidget);
}

bool JambiLanguage::isLanguageResource(const QString &path) const
{
    printf("JambiLanguage::isLanguageResource... %s\n", qPrintable(path));
    return path.startsWith("classpath:");
}



JambiExtensionFactory::JambiExtensionFactory(JambiLanguagePlugin *plugin, QExtensionManager *parent):
    QExtensionFactory(parent),
    m_jambi(plugin)
{
    printf("JambiExtensionFactory()\n");
}


JambiExtensionFactory::~JambiExtensionFactory()
{
}


QObject *JambiExtensionFactory::createExtension(QObject *object, const QString &iid, QObject *parent) const
{
     if (iid == Q_TYPEID(QDesignerLanguageExtension) && qobject_cast<QDesignerFormEditorInterface*> (object))
        return new JambiLanguage(parent);

    else if (iid == Q_TYPEID(QDesignerPropertySheetExtension)
             && qtjambi_is_created_by_java(object)) {
        return new JambiPropertySheet(m_jambi, object, parent);
    }

    return 0;
}

Q_EXPORT_PLUGIN2(JambiLanguagePlugin, JambiLanguagePlugin)
