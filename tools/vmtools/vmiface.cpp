#include <QtCore/QDir>
#include <QtCore/QLibrary>

#include <QtCore/QDebug>

#ifdef Q_OS_WIN
#include <QtCore/QSettings>
#endif

#include "vmiface.h"

#include "qtjambi_core.h"

typedef jint (JNICALL *PtrGetDefaultJavaVMInitArgs)(void *);
typedef jint (JNICALL *PtrCreateJavaVM)(JavaVM **, void **, void *);
typedef jint (JNICALL *PtrGetCreatedJavaVMs)(JavaVM **, jsize, jsize *);
typedef jint (JNICALL *PtrOnLoad)(JavaVM *, void *);
typedef void (JNICALL *PtrOnUnload)(JavaVM *, void *);

static PtrGetDefaultJavaVMInitArgs ptrGetDefaultJavaVMInitArgs;
static PtrCreateJavaVM ptrCreateJavaVM;
static PtrGetCreatedJavaVMs ptrGetCreatedJavaVMs;
static PtrOnLoad ptrOnLoad;
static PtrOnUnload ptrOnUnload;

static QString locate_vm();


QtJambi::VirtualMachineInterface::VirtualMachineInterface():
    m_vm(0),
    m_env(0)
{
    QString libvm = locate_vm();

    if (libvm.isEmpty()) {
        qWarning("Jambi: failed to initialize...");
        return;
    }

    QLibrary lib(libvm);
    if (!lib.load()) {
        qWarning("Jambi: failed to load: '%s'", qPrintable(libvm));
        return;
    }

    ptrCreateJavaVM = (PtrCreateJavaVM) lib.resolve("JNI_CreateJavaVM");
    ptrGetDefaultJavaVMInitArgs = (PtrGetDefaultJavaVMInitArgs) lib.resolve("JNI_GetDefaultJavaVMInitArgs");
//     ptrGetCreatedJavaVMs = (PtrGetCreatedJavaVMs) lib.resolve("GetCreatedJavaVMs");
//     ptrOnLoad = (PtrOnLoad) lib.resolve("JNI_OnLoad");
//     ptrOnUnload = (PtrOnUnload) lib.resolve("JNI_OnUnload");

    Q_ASSERT(ptrCreateJavaVM);
//     Q_ASSERT(ptrGetCreatedJavaVMs);
    Q_ASSERT(ptrGetDefaultJavaVMInitArgs);
//     Q_ASSERT(ptrOnLoad);
//     Q_ASSERT(ptrOnUnload);

    m_class_path = ::getenv("CLASSPATH");
    m_class_path.prepend("-Djava.class.path=");

    const int NOPTIONS = 3;
    JavaVMOption javaVMOptions[NOPTIONS];
    javaVMOptions[0].optionString = "-verbose:jni";
    javaVMOptions[1].optionString = "-Xcheck:jni";
    javaVMOptions[2].optionString = m_class_path.data();


    m_vm_args.version = JNI_VERSION_1_4;
    m_vm_args.ignoreUnrecognized = JNI_FALSE;
    m_vm_args.nOptions = NOPTIONS;
    m_vm_args.options = javaVMOptions;

    if (ptrGetDefaultJavaVMInitArgs(&m_vm_args)) {
        qWarning("QtJambi: failed to get vm arguments");
        return;
    }

    if (ptrCreateJavaVM(&m_vm, (void**) &m_env, &m_vm_args)) {
        qWarning("QtJambi: failed to create vm");
        return;
    }

    // Make sure we have loaded qtjambi, core and gui...
    const char *initializers[] = {
        "com/trolltech/qt/QtJambi_LibraryInitializer",
        "com/trolltech/qt/core/QtJambi_LibraryInitializer",
        "com/trolltech/qt/gui/QtJambi_LibraryInitializer",
        0
    };

    for (int i=0; initializers[i]; ++i) {
        jclass cl = m_env->FindClass(initializers[i]);
        if (qtjambi_exception_check(m_env)) {
            qWarning("QtJambi: failed to initialize qt jambi java libraries");
            break;
        }

//         jobject initializer = m_env->AllocObject(cl);

        printf("found: %s\n", qPrintable(qtjambi_class_name(m_env, cl)));
    }

    qtjambi_set_jvm(m_vm);
}


QtJambi::VirtualMachineInterface::~VirtualMachineInterface()
{
// ## fixme...
//     if (m_vm)
//         m_vm->DestroyJavaVM();
}


QtJambi::VirtualMachineInterface *QtJambi::VirtualMachineInterface::instance()
{
    static VirtualMachineInterface *iface;

    if (!iface) {
        iface = new VirtualMachineInterface;
    }

    return iface->m_vm ? iface : 0;
}


JNIEnv *QtJambi::VirtualMachineInterface::environment()
{
    return m_env;
}


jint QtJambi::VirtualMachineInterface::GetDefaultJavaVMInitArgs(void *)
{
    return 0;
}

jint QtJambi::VirtualMachineInterface::CreateJavaVM(JavaVM **, void **, void *)
{
    return 0;
}

jint QtJambi::VirtualMachineInterface::GetCreatedJavaVMs(JavaVM **, jsize, jsize *)
{
    return 0;
}

jint QtJambi::VirtualMachineInterface::OnLoad(JavaVM *, void *)
{
    return 0;
}

void QtJambi::VirtualMachineInterface::OnUnload(JavaVM *, void *)
{

}



#ifdef Q_OS_LINUX
static bool locate_vm(const QString &javaDir, const QString &mach)
{
    QString jpath = javadir;
    QString jmach = mach;

    if (jpath.isEmpty())
        jpath = qgetenv("JAVADIR");

    if (jpath.isEmpty()) // ### warning?
        return false;

    if (jmach.isEmpty())
        jmach = QLatin1String("i386");

    jpath += QLatin1String("/jre/lib/");
    jpath += jmach;

    jpath = QDir::cleanPath(jpath);

    if (! jpath.endsWith(QLatin1Char('/')))
        jpath += QLatin1Char('/');

    QLibrary libverify_so(jpath + "/libverify.so");
    QLibrary libjava_so(jpath + "/libjava.so");
    QLibrary libjvm_so(jpath + "/client/libjvm.so");

    libverify_so.load();
    libjava_so.load();
    libjvm_so.load();

    GetDefaultJavaVMInitArgs = (GetDefaultJavaVMInitArgs_signature) libjava_so.resolve("JNI_GetDefaultJavaVMInitArgs");
    if (! GetDefaultJavaVMInitArgs)
        return false;

    // ### check the pointers
    CreateJavaVM = (CreateJavaVM_signature) libjava_so.resolve("JNI_CreateJavaVM");
    GetCreatedJavaVMs = (GetCreatedJavaVMs_signature) libjava_so.resolve("JNI_GetCreatedJavaVMs");
    OnLoad = (OnLoad_signature) libjava_so.resolve("JNI_OnLoad");
    OnUnload = (OnUnload_signature) libjava_so.resolve("JNI_OnUnload");

}

#elif defined(Q_OS_WIN)

// Windows version
static QString locate_vm()
{

    QString javaHome;

    {
        QStringList roots, locations;
        roots << "HKEY_LOCAL_MACHINE"
              << "HKEY_CURRENT_USER";

        locations << "Java Runtime Environment"
                  << "Java Development Kit";

        QString currentVersion("CurrentVersion");

        for (QStringList::const_iterator root = roots.constBegin();
             root < roots.constEnd() && javaHome.isEmpty(); ++root) {

            for (QStringList::const_iterator loc = locations.constBegin();
                 loc < locations.constEnd() && javaHome.isEmpty(); ++loc) {

                QSettings reg(*root + "\\Software\\JavaSoft\\" + *loc, QSettings::NativeFormat);

                if (reg.contains(currentVersion)) {
                    QString version = reg.value(currentVersion).toString();
                    QSettings set(reg.fileName() + "\\" + version, QSettings::NativeFormat);
                    javaHome = set.value("JavaHome").toString();
                }
            }
        }

        if (javaHome.isEmpty()) {
            qWarning("Jambi: Failed to locate jvm.dll");
            return false;
        }
    }

    {
        QStringList libs;
        libs << "jre/bin/client"
             << "jre/lib/client"
             << "jre/bin/server"
             << "jre/lib/server";

        for (int i=0; i<libs.size(); ++i) {
            QFileInfo fi(javaHome + "/" + libs.at(i) + "/jvm.dll");
            if (fi.exists())
                return fi.absoluteFilePath();
        }
    }

    return QString();
}

#else

#error implement VM location on arbitrary machines...

#endif
