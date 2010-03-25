/****************************************************************************
** Meta object code from reading C++ file 'cppheadergenerator.h'
**
** Created: Thu Mar 25 20:49:07 2010
**      by: The Qt Meta Object Compiler version 62 (Qt 4.6.2)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include "../cppheadergenerator.h"
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'cppheadergenerator.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 62
#error "This file was generated using the moc from 4.6.2. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
static const uint qt_meta_data_CppHeaderGenerator[] = {

 // content:
       4,       // revision
       0,       // classname
       0,    0, // classinfo
       0,    0, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       0,       // signalCount

       0        // eod
};

static const char qt_meta_stringdata_CppHeaderGenerator[] = {
    "CppHeaderGenerator\0"
};

const QMetaObject CppHeaderGenerator::staticMetaObject = {
    { &CppGenerator::staticMetaObject, qt_meta_stringdata_CppHeaderGenerator,
      qt_meta_data_CppHeaderGenerator, 0 }
};

#ifdef Q_NO_DATA_RELOCATION
const QMetaObject &CppHeaderGenerator::getStaticMetaObject() { return staticMetaObject; }
#endif //Q_NO_DATA_RELOCATION

const QMetaObject *CppHeaderGenerator::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->metaObject : &staticMetaObject;
}

void *CppHeaderGenerator::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_CppHeaderGenerator))
        return static_cast<void*>(const_cast< CppHeaderGenerator*>(this));
    return CppGenerator::qt_metacast(_clname);
}

int CppHeaderGenerator::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = CppGenerator::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    return _id;
}
QT_END_MOC_NAMESPACE
