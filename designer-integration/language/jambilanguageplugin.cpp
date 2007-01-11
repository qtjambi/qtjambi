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

#include "qtjambi_core.h"
#include "qtjambi_utils.h"

#include <QtDebug>
#include <QtPlugin>


#include <QtGui>


jclass class_ResourceBrowser;

jmethodID method_ResourceBrowser;

static ClassData jni_class_table[] = {
    { &class_ResourceBrowser, "com/trolltech/tools/designer/ResourceBrowser" },
    { 0, 0 }
};

static MethodData jni_method_table[] = {
    { &class_ResourceBrowser, &method_ResourceBrowser, "<init>", "(Lcom/trolltech/qt/gui/QWidget;)V" },
    { 0, 0, 0, 0 }
};

JambiLanguagePlugin::JambiLanguagePlugin():
    m_core(0)
{
    printf("JambiLanguagePlugin: created\n");

    qtjambi_initialize_vm();
    JNIEnv *env = qtjambi_current_environment();
    qtjambi_set_java_connect_override(true);
    qtjambi_resolve_classes(env, jni_class_table);
    qtjambi_resolve_methods(env, jni_method_table);
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
    mgr->registerExtensions(new JambiExtensionFactory(this, mgr), Q_TYPEID(QDesignerMemberSheetExtension));
    mgr->registerExtensions(new JambiExtensionFactory(this, mgr), Q_TYPEID(QDesignerExtraInfoExtension));
}

QAction *JambiLanguagePlugin::action() const
{
    return 0;
}

QDesignerFormEditorInterface *JambiLanguagePlugin::core() const
{
    return m_core;
}

JambiLanguage::JambiLanguage(QObject *parent)
    : QObject(parent)
{
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
    return name;
}

QString JambiLanguage::neutralEnumerator(const QString &name) const
{
    return name;
}

QDesignerResourceBrowserInterface *JambiLanguage::createResourceBrowser(QWidget *parentWidget)
{
    JNIEnv *env = qtjambi_current_environment();
    jobject jParent = qtjambi_from_QWidget(env, parentWidget);
    QTJAMBI_EXCEPTION_CHECK(env);

    jobject jWidget = env->NewObject(class_ResourceBrowser, method_ResourceBrowser, jParent);
    QTJAMBI_EXCEPTION_CHECK(env);

    QObject *widget = qtjambi_to_qobject(env, jWidget);
    QTJAMBI_EXCEPTION_CHECK(env);

    QDesignerResourceBrowserInterface *iface =
        qobject_cast<QDesignerResourceBrowserInterface *>(widget);
    Q_ASSERT(iface);

    return iface;
}

bool JambiLanguage::isLanguageResource(const QString &path) const
{
    return path.startsWith("classpath:");
}


JambiExtraInfoExtension::JambiExtraInfoExtension(QWidget *widget,
                                                 QDesignerFormEditorInterface *core)
    : m_widget(widget),
      m_core(core)
{
    printf("JambiExtraInfoExtension::created...\n");
}


bool JambiExtraInfoExtension::saveUiExtraInfo(DomUi *ui)
{
    printf("JambiExtraInfoExtension::saveUiExtraInfo...\n");
    return false;
}


bool JambiExtraInfoExtension::loadUiExtraInfo(DomUi *ui)
{
    printf("JambiExtraInfoExtension::loadUiExtraInfo()\n");
    return false;
}


bool JambiExtraInfoExtension::saveWidgetExtraInfo(DomWidget *ui_widget)
{
    printf("JambiExtraInfoExtension::saveWidgetExtraInfo()\n");
    return false;
}


bool JambiExtraInfoExtension::loadWidgetExtraInfo(DomWidget *ui_widget)
{
    printf("JambiExtraInfoExtension::loadWidgetExtraInfo()\n");
    return false;
}



JambiExtensionFactory::JambiExtensionFactory(JambiLanguagePlugin *plugin, QExtensionManager *parent):
    QExtensionFactory(parent),
    m_jambi(plugin)
{
    printf("JambiExtensionFactory\n");
}


JambiExtensionFactory::~JambiExtensionFactory()
{
}


QObject *JambiExtensionFactory::createExtension(QObject *object, const QString &iid, QObject *parent) const
{
    if (iid == Q_TYPEID(QDesignerLanguageExtension) && qobject_cast<QDesignerFormEditorInterface*> (object))
        return new JambiLanguage(parent);

    else if (iid == Q_TYPEID(QDesignerPropertySheetExtension)) {

        JNIEnv *env = qtjambi_current_environment();
        jclass cl = qtjambi_find_class(env, "com/trolltech/tools/designer/PropertySheet");
        Q_ASSERT(cl);

        jmethodID id = env->GetMethodID(cl, "<init>", "(Lcom/trolltech/qt/core/QObject;"
                                                       "Lcom/trolltech/qt/core/QObject;)V");
        Q_ASSERT(id);

        jobject jps = env->NewObject(cl, id,
                                     qtjambi_from_QObject(env, object),
                                     qtjambi_from_QObject(env, parent)
                                     );

        QObject *qps = qtjambi_to_qobject(env, jps);
        Q_ASSERT(qps);

        QDesignerPropertySheetExtension *p = qobject_cast<QDesignerPropertySheetExtension *>(qps);
        Q_ASSERT(p);

        return qps;
    } else if (iid == Q_TYPEID(QDesignerMemberSheetExtension)) {
        JNIEnv *env = qtjambi_current_environment();
        jclass cl = qtjambi_find_class(env, "com/trolltech/tools/designer/MemberSheet");
        Q_ASSERT(cl);

        jmethodID id = env->GetMethodID(cl, "<init>", "(Lcom/trolltech/qt/core/QObject;"
                                                       "Lcom/trolltech/qt/core/QObject;)V");
        Q_ASSERT(id);

        jobject jps = env->NewObject(cl, id,
                                     qtjambi_from_QObject(env, object),
                                     qtjambi_from_QObject(env, parent)
                                     );

        QObject *qps = qtjambi_to_qobject(env, jps);
        Q_ASSERT(qps);

        QDesignerMemberSheetExtension *p = qobject_cast<QDesignerMemberSheetExtension *>(qps);
        Q_ASSERT(p);

        return qps;
    } else if (iid == Q_TYPEID(QDesignerExtraInfoExtension)) {
        QWidget *w = qobject_cast<QWidget *>(object);
        return new JambiExtraInfoExtension(w, m_jambi->core());
    }

    return 0;
}

Q_EXPORT_PLUGIN2(JambiLanguagePlugin, JambiLanguagePlugin)
