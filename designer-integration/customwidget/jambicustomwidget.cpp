
#include "jambicustomwidget.h"
#include <jambilanguageextension.h>

#include <qtjambi_core.h>

#include <QtPlugin>
#include <QtCore/QtDebug>

#include <qapplication.h>

static jclass class_CustomWidget;
static jmethodID method_createWidget;
static jmethodID method_group;
static jmethodID method_icon;
static jmethodID method_includeFile;
static jmethodID method_isContainer;
static jmethodID method_name;
static jmethodID method_tooltip;
static jmethodID method_whatsThis;
static jmethodID method_pluginClass;

static void resolve(JNIEnv *env) {
    if (class_CustomWidget)
        return;

    class_CustomWidget = (jclass) env->NewGlobalRef(env->FindClass("com/trolltech/tools/designer/CustomWidget"));
    Q_ASSERT(class_CustomWidget);

    method_createWidget = env->GetMethodID(class_CustomWidget, "createWidget", "(Lcom/trolltech/qt/gui/QWidget;)Lcom/trolltech/qt/gui/QWidget;");
    Q_ASSERT(method_createWidget);

    method_group = env->GetMethodID(class_CustomWidget, "group", "()Ljava/lang/String;");
    Q_ASSERT(method_group);

    method_icon = env->GetMethodID(class_CustomWidget, "icon", "()Lcom/trolltech/qt/gui/QIcon;");
    Q_ASSERT(method_icon);

    method_includeFile = env->GetMethodID(class_CustomWidget, "includeFile", "()Ljava/lang/String;");
    Q_ASSERT(method_includeFile);

    method_isContainer = env->GetMethodID(class_CustomWidget, "isContainer", "()Z");
    Q_ASSERT(method_isContainer);

    method_name = env->GetMethodID(class_CustomWidget, "name", "()Ljava/lang/String;");
    Q_ASSERT(method_name);

    method_tooltip = env->GetMethodID(class_CustomWidget, "tooltip", "()Ljava/lang/String;");
    Q_ASSERT(method_tooltip);

    method_whatsThis = env->GetMethodID(class_CustomWidget, "whatsThis", "()Ljava/lang/String;");
    Q_ASSERT(method_whatsThis);

    method_pluginClass = env->GetMethodID(class_CustomWidget, "pluginClass", "()Ljava/lang/Class;");
    Q_ASSERT(method_whatsThis);

}

JambiCustomWidget::JambiCustomWidget(jobject object):
    m_core(0),
    m_object(0)
{
    Q_ASSERT(object);

    JNIEnv *env = qtjambi_current_environment();
    resolve(env);
    m_object = env->NewGlobalRef(object);

    QTJAMBI_EXCEPTION_CHECK(env);
}

JambiCustomWidget::~JambiCustomWidget()
{
    JNIEnv *env = qtjambi_current_environment();
    QTJAMBI_EXCEPTION_CHECK(env);
    env->DeleteGlobalRef(m_object);
    QTJAMBI_EXCEPTION_CHECK(env);
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
//     JambiLanguageExtension *lang = static_cast<JambiLanguageExtension*> (language()); // ### wrong
//     if (! lang)
//         return;

}

QString JambiCustomWidget::callStringMethod(jmethodID id) const
{
    JNIEnv *env = qtjambi_current_environment();
    Q_ASSERT(env);
    QTJAMBI_EXCEPTION_CHECK(env);

    jstring str = (jstring) env->CallObjectMethod(m_object, id);

    if (str == 0)
        return QString();

    QTJAMBI_EXCEPTION_CHECK(env);
    QString qstring = qtjambi_to_qstring(env, str);
    QTJAMBI_EXCEPTION_CHECK(env);
    return qstring;
}

QWidget *JambiCustomWidget::createWidget(QWidget *parent)
{
    JNIEnv *env = qtjambi_current_environment();
    Q_ASSERT (env != 0);
    QTJAMBI_EXCEPTION_CHECK(env);

    jobject javaParent = qtjambi_from_qobject(env, parent, "QWidget", "com/trolltech/qt/gui/");
    QTJAMBI_EXCEPTION_CHECK(env);

    jobject widget = env->CallObjectMethod(m_object, method_createWidget, javaParent);
    QTJAMBI_EXCEPTION_CHECK(env);

    QWidget *qwidget = qobject_cast<QWidget*>(qtjambi_to_qobject (env, widget));
    QTJAMBI_EXCEPTION_CHECK(env);

    // Designer assumes the widget allways has a parent....
    if (!qwidget->parent()) {
        qwidget->setParent(parent, qwidget->windowFlags() & ~Qt::Window);

    }

    return qwidget;
}

QString JambiCustomWidget::domXml() const
{
    QString className = name().split('.').last();
    className[0] = className[0].toLower();

    const char *xml_data = "<widget class=\"%1\"  name=\"%2\">"
                           "  <property name=\"objectName\">"
                           "    <string notr=\"true\">%3</string>"
                           "  </property>"
                           "</widget>";

    return QString::fromUtf8(xml_data)
        .arg(name())
        .arg(callStringMethod(method_name))
        .arg(className);
}

QString JambiCustomWidget::name() const
{
    JNIEnv *env = qtjambi_current_environment();
    jclass cl = (jclass) env->CallObjectMethod(m_object, method_pluginClass);
    return qtjambi_class_name(env, cl);
}

bool JambiCustomWidget::isContainer() const
{
    JNIEnv *env = qtjambi_current_environment();
    return env->CallBooleanMethod(m_object, method_isContainer);
}

QString JambiCustomWidget::group() const
{
    return callStringMethod(method_group);
}

QString JambiCustomWidget::toolTip() const
{
    return callStringMethod(method_tooltip);
}

QString JambiCustomWidget::whatsThis() const
{
    return callStringMethod(method_whatsThis);
}

QString JambiCustomWidget::includeFile() const
{
    return callStringMethod(method_includeFile);
}

QIcon JambiCustomWidget::icon() const
{
    JNIEnv *env = qtjambi_current_environment();
    jobject javaIcon = env->CallObjectMethod(m_object, method_icon);

    qtjambi_exception_check(env);

    if (javaIcon == 0)
        return QIcon();

    QIcon i = * (QIcon *) qtjambi_to_object(env, javaIcon);
    return i;
}


JambiCustomWidgetCollection::JambiCustomWidgetCollection()
{
    qtjambi_initialize_vm();

    JNIEnv *env = qtjambi_current_environment();
    Q_ASSERT (env != 0);

    jclass cl = env->FindClass("com/trolltech/tools/designer/CustomWidgetManager");
    if (qtjambi_exception_check(env))
        return;

    jmethodID method_instance = env->GetStaticMethodID(cl, "instance", "()Lcom/trolltech/tools/designer/CustomWidgetManager;");

    if (qtjambi_exception_check(env))
        return;

    m_id_customWidgets = env->GetMethodID(cl, "customWidgets", "()Ljava/util/List;");
    if (qtjambi_exception_check(env))
        return;

    m_manager = env->NewGlobalRef(env->CallStaticObjectMethod(cl, method_instance));
    if (qtjambi_exception_check(env))
        return;

    jobject widgetList = env->CallObjectMethod(m_manager, m_id_customWidgets);
    jobjectArray widgetArray = qtjambi_collection_toArray(env, widgetList);

    int length = env->GetArrayLength(widgetArray);
    for (int i=0; i<length; ++i) {
        jobject widget = env->GetObjectArrayElement(widgetArray, i);
        m_widgets << new JambiCustomWidget(widget);
    }

    env->DeleteLocalRef(cl);
    env->DeleteLocalRef(widgetArray);
    env->DeleteLocalRef(widgetList);
}

JambiCustomWidgetCollection::~JambiCustomWidgetCollection()
{
    JNIEnv *env = qtjambi_current_environment();
    env->DeleteGlobalRef(m_manager);
}


QList<QDesignerCustomWidgetInterface *> JambiCustomWidgetCollection::customWidgets() const
{
    return m_widgets;
}

Q_EXPORT_PLUGIN(JambiCustomWidgetCollection)

// Q_EXPORT_PLUGIN2(JambiCustomWidget, JambiCustomWidget)
