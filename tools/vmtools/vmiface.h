#ifndef VMLOCATOR_H
#define VMLOCATOR_H

#include <QtCore/QByteArray>

#include <jni.h>


namespace QtJambi {

    class VirtualMachineInterface
    {
    public:
        ~VirtualMachineInterface();

    protected:
        VirtualMachineInterface();

    public: // JNI
        jint GetDefaultJavaVMInitArgs(void *);
        jint CreateJavaVM(JavaVM **, void **, void *);
        jint GetCreatedJavaVMs(JavaVM **, jsize, jsize *);
        jint OnLoad(JavaVM *, void *);
        void OnUnload(JavaVM *, void *);


        static VirtualMachineInterface *instance();

        JNIEnv *environment();

    private:
        JavaVMInitArgs m_vm_args;
        JavaVM *m_vm;
        JNIEnv *m_env;

        QByteArray m_class_path;
        QByteArray m_javaDebug;

    private:
        Q_DISABLE_COPY(VirtualMachineInterface);
    };

};

#endif
