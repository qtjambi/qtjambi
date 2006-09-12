
#include <QtCore/QDir>
#include <QtCore/QLibrary>

#include "jambivm.h"

JambiVM::~JambiVM()
{
    if (m_javaVM)
        m_javaVM->DestroyJavaVM ();
}

JambiVM *JambiVM::jambi()
{
    static JambiVM jambi_vm;

    if (! jambi_vm.m_env)
        jambi_vm.intializeJavaVM();

    return &jambi_vm;
}

JNIEnv *JambiVM::environment()
{
    return jambi()->m_env;
}

JambiVM::JambiVM():
    m_javaVM(0),
    m_env(0)
{
}

bool JambiVM::intializeJavaVM(const QString javadir, const QString &mach)
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

    const int NOPTIONS = 1;
    JavaVMOption javaVMOptions[1];
    javaVMOptions[0].optionString = "-verbose:jni";

    m_javaVMArgs.version = JNI_VERSION_1_4;
    m_javaVMArgs.ignoreUnrecognized = JNI_FALSE;
    m_javaVMArgs.nOptions = NOPTIONS;
    m_javaVMArgs.options = javaVMOptions;

    if (GetDefaultJavaVMInitArgs(&m_javaVMArgs))
        return false;

    if (CreateJavaVM(&m_javaVM, (void**) &m_env, &m_javaVMArgs))
        return false;

    return true;
}

