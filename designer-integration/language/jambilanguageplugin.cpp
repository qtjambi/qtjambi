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
#include "jambivm.h"

#include <QtDesigner/private/qdesigner_utils_p.h>

#include <QtDebug>
#include <QtPlugin>

JambiLanguagePlugin::JambiLanguagePlugin():
    m_core(0)
{
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
    //mgr->registerExtensions(new JambiExtensionFactory(this, mgr), Q_TYPEID(QDesignerPropertySheetExtension));

    JambiVM::environment();
}

QAction *JambiLanguagePlugin::action() const
{
    return 0;
}

QDesignerFormEditorInterface *JambiLanguagePlugin::core() const
{
    return m_core;
}

JambiLanguage::JambiLanguage(QObject *parent):
    QObject(parent)
{
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
    if (JNIEnv *env = JambiVM::environment()) {
        if (qtjambi_is_created_by_java(object)) {
            jobject objectID = qtjambi_from_qobject(env, object, "QWidget", "com/trolltech/qt/gui/");
            jclass classID = env->GetObjectClass(objectID);
            return qtjambi_class_name(env, classID);
        }
    }

    return QString();
}

QString JambiLanguage::enumerator(const QString &name) const
{
    QString id = name;
    id.replace(QLatin1String("::"), QLatin1String("."));
    id += QLatin1String("_Jambi");
    return id;
}

QString JambiLanguage::neutralEnumerator(const QString &name) const
{
    QString id = name;
    if (id.endsWith(QLatin1String("_Jambi"))) {
        id.replace(QLatin1String("."), QLatin1String("::"));
        id = id.left(id.length() - 6);
    }
    return id;
}

JNIEnv *JambiLanguage::environment() const
{
    return JambiVM::environment();
}

JambiPropertySheet::JambiPropertySheet(JambiLanguagePlugin *plugin, QObject *object, QObject *parent):
    QDesignerPropertySheet(object, parent),
    m_jambi(plugin)
{
    QExtensionManager *mgr = m_jambi->core()->extensionManager();
    m_language = qt_extension<QDesignerLanguageExtension*> (mgr, m_jambi->core());
}

JambiPropertySheet::~JambiPropertySheet()
{
}

void JambiPropertySheet::setProperty(int index, const QVariant &v)
{
    using namespace qdesigner_internal;

    QDesignerPropertySheet::setProperty(index, v);
}

QVariant JambiPropertySheet::property(int index) const
{
    using namespace qdesigner_internal;

    QVariant v = QDesignerPropertySheet::property(index);

    if (! m_language)
        return v;

    if (qVariantCanConvert<EnumType>(v)) {
        EnumType e = qvariant_cast<EnumType> (v);

        QMap<QString, QVariant> items;
        QMapIterator<QString, QVariant> it (e.items);
        while (it.hasNext()) {
            it.next();
            items.insert(m_language->enumerator(it.key()), it.value());
        }
        e.items = items;
        qVariantSetValue(v, e);
    } else if (qVariantCanConvert<FlagType>(v)) {
        FlagType e = qvariant_cast<FlagType> (v);

        QMap<QString, QVariant> items;
        QMapIterator<QString, QVariant> it (e.items);
        while (it.hasNext()) {
            it.next();
            items.insert(m_language->enumerator(it.key()), it.value());
        }
        e.items = items;
        qVariantSetValue(v, e);
    }

    return v;
}

JambiExtensionFactory::JambiExtensionFactory(JambiLanguagePlugin *plugin, QExtensionManager *parent):
    QExtensionFactory(parent),
    m_jambi(plugin)
{
}

JambiExtensionFactory::~JambiExtensionFactory()
{
}

QObject *JambiExtensionFactory::createExtension(QObject *object, const QString &iid, QObject *parent) const
{
    if (iid == Q_TYPEID(QDesignerLanguageExtension) && qobject_cast<QDesignerFormEditorInterface*> (object))
        return new JambiLanguage(parent);

    else if (iid == Q_TYPEID(QDesignerPropertySheetExtension))
        return new JambiPropertySheet(m_jambi, object, parent);

    return 0;
}

Q_EXPORT_PLUGIN2(JambiLanguagePlugin, JambiLanguagePlugin)
