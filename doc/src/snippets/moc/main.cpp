#include "myclass1.h"

MyClass::MyClass(QObject *) {}
MyClass::~MyClass() {}
void MyClass::mySlot() {}
#undef MyClass

#include "myclass2.h"

MyClass::MyClass(QObject *) {}
MyClass::~MyClass() {}
void MyClass::setPriority(Priority) {}
MyClass::Priority MyClass::priority() const { return High; }
#undef MyClass

#include "myclass3.h"

MyClass::MyClass(QObject *) {}
MyClass::~MyClass() {}
#undef MyClass

int main()
{
    return 0;
}
