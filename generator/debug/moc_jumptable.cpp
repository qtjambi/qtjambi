/****************************************************************************
** Meta object code from reading C++ file 'jumptable.h'
**
** Created: Wed Mar 31 00:48:02 2010
**      by: The Qt Meta Object Compiler version 62 (Qt 4.6.2)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include "../jumptable.h"
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'jumptable.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 62
#error "This file was generated using the moc from 4.6.2. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
static const uint qt_meta_data_JumpTablePreprocessor[] = {

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

static const char qt_meta_stringdata_JumpTablePreprocessor[] = {
    "JumpTablePreprocessor\0"
};

const QMetaObject JumpTablePreprocessor::staticMetaObject = {
    { &Generator::staticMetaObject, qt_meta_stringdata_JumpTablePreprocessor,
      qt_meta_data_JumpTablePreprocessor, 0 }
};

#ifdef Q_NO_DATA_RELOCATION
const QMetaObject &JumpTablePreprocessor::getStaticMetaObject() { return staticMetaObject; }
#endif //Q_NO_DATA_RELOCATION

const QMetaObject *JumpTablePreprocessor::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->metaObject : &staticMetaObject;
}

void *JumpTablePreprocessor::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_JumpTablePreprocessor))
        return static_cast<void*>(const_cast< JumpTablePreprocessor*>(this));
    return Generator::qt_metacast(_clname);
}

int JumpTablePreprocessor::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = Generator::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    return _id;
}
static const uint qt_meta_data_JumpTableGenerator[] = {

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

static const char qt_meta_stringdata_JumpTableGenerator[] = {
    "JumpTableGenerator\0"
};

const QMetaObject JumpTableGenerator::staticMetaObject = {
    { &Generator::staticMetaObject, qt_meta_stringdata_JumpTableGenerator,
      qt_meta_data_JumpTableGenerator, 0 }
};

#ifdef Q_NO_DATA_RELOCATION
const QMetaObject &JumpTableGenerator::getStaticMetaObject() { return staticMetaObject; }
#endif //Q_NO_DATA_RELOCATION

const QMetaObject *JumpTableGenerator::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->metaObject : &staticMetaObject;
}

void *JumpTableGenerator::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_JumpTableGenerator))
        return static_cast<void*>(const_cast< JumpTableGenerator*>(this));
    return Generator::qt_metacast(_clname);
}

int JumpTableGenerator::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = Generator::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    return _id;
}
QT_END_MOC_NAMESPACE
