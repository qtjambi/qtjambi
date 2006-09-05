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

#include "jambi_plugin.h"
#include <QtDesigner/private/qdesigner_utils_p.h>

#include <QtDebug>
#include <QtPlugin>

JambiPlugin::JambiPlugin():
    m_core(0)
{
}

JambiPlugin::~JambiPlugin()
{
}

bool JambiPlugin::isInitialized() const
{
    return m_core != 0;
}

void JambiPlugin::initialize(QDesignerFormEditorInterface *core)
{
    if (m_core)
        return;

    m_core = core;

    QExtensionManager *mgr = m_core->extensionManager();
    Q_ASSERT (mgr != 0);

    mgr->registerExtensions(new JambiExtensionFactory(this, mgr), Q_TYPEID(QDesignerLanguageExtension));
    mgr->registerExtensions(new JambiExtensionFactory(this, mgr), Q_TYPEID(QDesignerPropertySheetExtension));
}

QAction *JambiPlugin::action() const
{
    return 0;
}

QDesignerFormEditorInterface *JambiPlugin::core() const
{
    return m_core;
}

JambiLanguageExtension::JambiLanguageExtension(QObject *parent):
    QObject(parent)
{
}

JambiLanguageExtension::~JambiLanguageExtension()
{
}

QDialog *JambiLanguageExtension::createFormWindowSettingsDialog(QDesignerFormWindowInterface *, QWidget *)
{
    return 0;
}

QString JambiLanguageExtension::enumerator(const QString &name) const
{
    QString id = name;
    id.replace(QLatin1String("::"), QLatin1String("."));
    id += QLatin1String("_Jambi");
    return id;
}

QString JambiLanguageExtension::neutralEnumerator(const QString &name) const
{
    QString id = name;
    if (id.endsWith(QLatin1String("_Jambi"))) {
        id.replace(QLatin1String("."), QLatin1String("::"));
        id = id.left(id.length() - 6);
    }
    return id;
}

JambiPropertySheet::JambiPropertySheet(JambiPlugin *plugin, QObject *object, QObject *parent):
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


JambiExtensionFactory::JambiExtensionFactory(JambiPlugin *plugin, QExtensionManager *parent):
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
        return new JambiLanguageExtension(parent);

    else if (iid == Q_TYPEID(QDesignerPropertySheetExtension))
        return new JambiPropertySheet(m_jambi, object, parent);

    return 0;
}

Q_EXPORT_PLUGIN2(jambi, JambiPlugin)
