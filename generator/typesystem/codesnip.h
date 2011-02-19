
#ifndef CODESNIP_H_
#define CODESNIP_H_

#include <QMap>
#include <QTextStream>
#include "typesystem.h"

class Indentor;
typedef QMap<int, QString> ArgumentMap;

class CodeSnipFragment {
    private:
        const QString m_code;
        TemplateInstance *m_instance;

    public:
        CodeSnipFragment(const QString &code)
                : m_code(code),
                m_instance(0) {}

        CodeSnipFragment(TemplateInstance *instance)
                : m_instance(instance) {}

        QString code() const;
};

class CodeSnipAbstract {
    public:
        QString code() const;

        void addCode(const QString &code) {
            codeList.append(new CodeSnipFragment(code));
        }

        void addTemplateInstance(TemplateInstance *ti) {
            codeList.append(new CodeSnipFragment(ti));
        }

        QList<CodeSnipFragment*> codeList;
};

class CustomFunction : public CodeSnipAbstract {
    public:
        CustomFunction(const QString &n = QString()) : name(n) { }

        QString name;
        QString param_name;
};

class TemplateEntry : public CodeSnipAbstract {
    public:
        TemplateEntry(const QString &name)
                : m_name(name) {
        };

        QString name() const {
            return m_name;
        };

    private:
        QString m_name;
};

class CodeSnip : public CodeSnipAbstract {
    public:
        enum Position {
            Beginning,
            End,
            AfterThis
        };

        CodeSnip() : language(TypeSystem::TargetLangCode) { }
        CodeSnip(TypeSystem::Language lang) : language(lang) { }

        // Very simple, easy to make code ugly if you try
        QTextStream &formattedCode(QTextStream &s, Indentor &indentor) const;

        TypeSystem::Language language;
        Position position;
        ArgumentMap argumentMap;
};
typedef QList<CodeSnip> CodeSnipList;

#endif
