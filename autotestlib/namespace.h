/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $CPP_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

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

    namespace NameSpace2 
    {

        namespace NameSpace3 
        {
            class ObjectD;

            class ObjectC: public ObjectA
            {
            public:
                ObjectC(const QString &s) : m_fooBar(s) {}
                ObjectC *fooBar(ObjectD *obj);

                QString str() { return m_fooBar; }

            private:
                QString m_fooBar;
            };

            class InterfaceA
            {
            public:
                virtual ObjectD *fooBar2(ObjectC *) = 0;
                virtual InterfaceA *fooBar(InterfaceA *) = 0;
            };

            class ObjectD: public ObjectC, InterfaceA
            {
            public:
                ObjectD(const QString &s) : ObjectC(s) {} 

                ObjectD *fooBar2(ObjectC *obj)
                {
                    return new ObjectD(obj->str());
                }

                InterfaceA *fooBar(InterfaceA *obj)
                {
                    return obj->fooBar(fooBar(this));
                }
            };

            class ValueB
            {
            public:
                ValueB() : m_i(0) {}
                ValueB(int i) : m_i(i) {}
                ValueB(const ValueB &myValue) : m_i(myValue.m_i) {}
               
            private:
                int m_i;
            };
        };

    };

};



inline NameSpace::NameSpace2::NameSpace3::ObjectC *NameSpace::NameSpace2::NameSpace3::ObjectC::fooBar(NameSpace::NameSpace2::NameSpace3::ObjectD *obj)
{                
    return new NameSpace::NameSpace2::NameSpace3::ObjectC(obj->str());
}
inline NameSpace::ObjectA *NameSpace::ObjectA::aFunc(NameSpace::ObjectA *a) { return a; }
inline NameSpace::ObjectB *NameSpace::ObjectA::bFunc(NameSpace::ObjectB *b) { return b; }

inline NameSpace::ValueA NameSpace::ObjectA::vFunc(const NameSpace::ValueA &a) {
    return a;
}

inline NameSpace::ObjectA *NameSpace::ObjectA::aFuncPrefixed(NameSpace::ObjectA *a) { return a; }
inline NameSpace::ObjectB *NameSpace::ObjectA::bFuncPrefixed(NameSpace::ObjectB *b) { return b; }
inline NameSpace::ValueA NameSpace::ObjectA::vFuncPrefixed(const NameSpace::ValueA &a) { return a; }

#endif // NAMESPACE_H
