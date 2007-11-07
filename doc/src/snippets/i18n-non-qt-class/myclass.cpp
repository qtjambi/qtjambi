#include <iostream>
#include "myclass.h"

MyClass::MyClass()
{
    std::cout << tr("Hello Qt!\n").toLocal8Bit().constData();
}
