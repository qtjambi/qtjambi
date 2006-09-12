
#include "jambicustomwidget.h"
#include <jambilanguageextension.h>
#include <qtjambi_core.h>
#include <QtPlugin>
#include <QtDebug>

JambiCustomWidget::JambiCustomWidget():
    m_core(0),
    m_class(0),
    m_object(0)
{
}

JambiCustomWidget::~JambiCustomWidget()
{
}

QDesignerLanguageExtension *JambiCustomWidget::language() const
{
    if (! m_core)
        return 0;

    QExtensionManager *mgr = m_core->extensionManager();
    return qt_extension<QDesignerLanguageExtension*> (mgr, m_core);
}

bool JambiCustomWidget::isInitialized() const
{
    return m_core != 0;
}

void JambiCustomWidget::initialize(QDesignerFormEditorInterface *core)
{
    if (m_core)
        return;

    m_core = core;

    JambiLanguageExtension *lang = static_cast<JambiLanguageExtension*> (language()); // ### wrong
    if (! lang)
        return;

    JNIEnv *env = lang->environment();
    Q_ASSERT (env != 0);

    m_class = env->FindClass("Prog");
    if (! m_class) {
        env->ExceptionDescribe();
        return;
    }

    jmethodID ctor = env->GetMethodID(m_class, "<init>", "()V");
    if (! ctor) {
        env->ExceptionDescribe();
        return;
    }

    m_object = env->NewObject(m_class, ctor, 0);
    if (! m_object) {
        env->ExceptionDescribe();
        return;
    }
}

QWidget *JambiCustomWidget::createWidget(QWidget *parent)
{
    if (! m_class || ! m_object)
        return 0;

    JambiLanguageExtension *lang = static_cast<JambiLanguageExtension*> (language()); // ### wrong
    if (! lang)
        return 0;

    JNIEnv *env = lang->environment();
    Q_ASSERT (env != 0);

    jmethodID createWidgetID = env->GetMethodID(m_class, "createWidget", "(Lcom/trolltech/qt/gui/QWidget;)Lcom/trolltech/qt/gui/QWidget;");
    if (! createWidgetID) {
        env->ExceptionDescribe();
        return 0;
    }

    jobject parentID = qtjambi_from_qobject (env, parent, "QWidget", "com/trolltech/qt/gui/");
    jobject widgetID = env->CallObjectMethod(m_object, createWidgetID, parentID, 0);

    if (! widgetID) {
        env->ExceptionDescribe();
        return 0;
    }

    return qobject_cast<QWidget*> (qtjambi_to_qobject (env, widgetID));
}

QString JambiCustomWidget::name() const
{
    if (! m_class || ! m_object)
        return 0;

    JambiLanguageExtension *lang = static_cast<JambiLanguageExtension*> (language()); // ### wrong
    if (! lang)
        return 0;

    JNIEnv *env = lang->environment();
    Q_ASSERT (env != 0);

    jmethodID nameID = env->GetMethodID(m_class, "name", "()Ljava/lang/String;");
    if (! nameID) {
        env->ExceptionDescribe();
        return QString();
    }

    jobject str = env->CallObjectMethod(m_object, nameID, 0);

    if (! str) {
        env->ExceptionDescribe();
        return QString();
    }

    return qtjambi_to_qstring (env, (jstring) str);
}

bool JambiCustomWidget::isContainer() const
{
    return false;
}

QString JambiCustomWidget::group() const
{
    return QLatin1String("Jambi");
}

QString JambiCustomWidget::toolTip() const
{
    return QString();
}

QString JambiCustomWidget::whatsThis() const
{
    return QString();
}

QString JambiCustomWidget::includeFile() const
{
    return QString();
}

QIcon JambiCustomWidget::icon() const
{
    return QIcon();
}

Q_EXPORT_PLUGIN2(JambiCustomWidget, JambiCustomWidget)
