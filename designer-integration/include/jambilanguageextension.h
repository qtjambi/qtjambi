
#ifndef JAMBI_LANGUAGE_EXTENSION_H
#define JAMBI_LANGUAGE_EXTENSION_H

#include <QtDesigner/QtDesigner>
#include <jni.h>

class JambiLanguageExtension: public QDesignerLanguageExtension
{
public:
    virtual ~JambiLanguageExtension() {}

    virtual JNIEnv *environment() const = 0;
};

Q_DECLARE_EXTENSION_INTERFACE(JambiLanguageExtension, "com.trolltech.jambi.Language")

#endif // JAMBI_LANGUAGE_EXTENSION_H
