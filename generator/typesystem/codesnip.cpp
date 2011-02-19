
#include "codesnip.h"

QString CodeSnipAbstract::code() const {
    QString res;
    foreach(CodeSnipFragment *codeFrag, codeList) {
        res.append(codeFrag->code());
    }
    return res;
}

QString CodeSnipFragment::code() const {
    if (m_instance)
        return m_instance->expandCode();
    else
        return m_code;
}
