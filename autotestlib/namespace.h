#ifndef NAMESPACE_H
#define NAMESPACE_H

#include <stdio.h>

namespace NameSpace
{

    class ObjectA;
    class ObjectB;
    class ValueA;

    class ObjectA
    {
    public:
        ObjectA *aFunc(ObjectA *);
        ObjectB *bFunc(ObjectB *);
        ValueA vFunc(const ValueA &);
        NameSpace::ObjectA *aFuncPrefixed(NameSpace::ObjectA *);
        NameSpace::ObjectB *bFuncPrefixed(NameSpace::ObjectB *);
        NameSpace::ValueA vFuncPrefixed(const NameSpace::ValueA &);
    };


    class ObjectB
    {

    };


    class ValueA
    {
    public:
        ValueA() { x = 42; }
        bool operator==(const ValueA &a) const { return a.x == x; }

        int getX() const { return x; }
    private:
        int x;
    };

};

#ifndef QT_JAMBI_RUN
inline NameSpace::ObjectA *NameSpace::ObjectA::aFunc(NameSpace::ObjectA *a) { return a; }
inline NameSpace::ObjectB *NameSpace::ObjectA::bFunc(NameSpace::ObjectB *b) { return b; }

inline NameSpace::ValueA NameSpace::ObjectA::vFunc(const NameSpace::ValueA &a) {
    return a;
}

inline NameSpace::ObjectA *NameSpace::ObjectA::aFuncPrefixed(NameSpace::ObjectA *a) { return a; }
inline NameSpace::ObjectB *NameSpace::ObjectA::bFuncPrefixed(NameSpace::ObjectB *b) { return b; }
inline NameSpace::ValueA NameSpace::ObjectA::vFuncPrefixed(const NameSpace::ValueA &a) { return a; }
#endif // QT_JAMBI_RUN

#endif // NAMESPACE_H
