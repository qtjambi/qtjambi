
#ifndef JAMBI_LANGUAGE_EXTENSION_H
#define JAMBI_LANGUAGE_EXTENSION_H

#include <QtDesigner/QtDesigner>

class JambiLanguageExtension: public QDesignerLanguageExtension
{
public:
    virtual ~JambiLanguageExtension() {}
};

Q_DECLARE_EXTENSION_INTERFACE(JambiLanguageExtension, "com.trolltech.jambi.Language")

#endif // JAMBI_LANGUAGE_EXTENSION_H
