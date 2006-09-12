#ifndef JAMBI_VM_H
#define JAMBI_VM_H

#include <QtCore/QString>
#include <QtCore/QByteArray>
#include <qtjambi_core.h>

class JambiVM
{
public:
    ~JambiVM();
    static JambiVM *jambi();
    static JNIEnv *environment();

protected:
    JambiVM();
    bool intializeJavaVM(const QString javadir = QString(), const QString &mach = QString());

public: // JNI
    typedef jint JNICALL (*GetDefaultJavaVMInitArgs_signature)(void *);
    typedef jint JNICALL (*CreateJavaVM_signature)(JavaVM **, void **, void *);
    typedef jint JNICALL (*GetCreatedJavaVMs_signature)(JavaVM **, jsize, jsize *);
    typedef jint JNICALL (*OnLoad_signature)(JavaVM *, void *);
    typedef void JNICALL (*OnUnload_signature)(JavaVM *, void *);

    GetDefaultJavaVMInitArgs_signature GetDefaultJavaVMInitArgs;
    CreateJavaVM_signature CreateJavaVM;
    GetCreatedJavaVMs_signature GetCreatedJavaVMs;
    OnLoad_signature OnLoad;
    OnUnload_signature OnUnload;

private:
    JavaVMInitArgs m_javaVMArgs;
    JavaVM *m_javaVM;
    JNIEnv *m_env;

    QByteArray m_jambiClasspath;
    QByteArray m_javaDebug;

private:
    Q_DISABLE_COPY(JambiVM)
};

#endif // JAMBI_VM_H
