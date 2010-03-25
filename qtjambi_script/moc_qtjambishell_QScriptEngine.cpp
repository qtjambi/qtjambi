/****************************************************************************
** Meta object code from reading C++ file 'qtjambishell_QScriptEngine.h'
**
** Created: Thu Mar 25 22:54:09 2010
**      by: The Qt Meta Object Compiler version 62 (Qt 4.6.2)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include "../cpp/com_trolltech_qt_script/qtjambishell_QScriptEngine.h"
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'qtjambishell_QScriptEngine.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 62
#error "This file was generated using the moc from 4.6.2. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
static const uint qt_meta_data_QtJambi_SignalWrapper_QScriptEngine[] = {

 // content:
       4,       // revision
       0,       // classname
       0,    0, // classinfo
       1,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       0,       // signalCount

 // slots: signature, parameters, type, tag, flags
      48,   37,   36,   36, 0x0a,

       0        // eod
};

static const char qt_meta_stringdata_QtJambi_SignalWrapper_QScriptEngine[] = {
    "QtJambi_SignalWrapper_QScriptEngine\0"
    "\0exception0\0"
    "__qt_signalwrapper_signalHandlerException(QScriptValue)\0"
};

const QMetaObject QtJambi_SignalWrapper_QScriptEngine::staticMetaObject = {
    { &QObject::staticMetaObject, qt_meta_stringdata_QtJambi_SignalWrapper_QScriptEngine,
      qt_meta_data_QtJambi_SignalWrapper_QScriptEngine, 0 }
};

#ifdef Q_NO_DATA_RELOCATION
const QMetaObject &QtJambi_SignalWrapper_QScriptEngine::getStaticMetaObject() { return staticMetaObject; }
#endif //Q_NO_DATA_RELOCATION

const QMetaObject *QtJambi_SignalWrapper_QScriptEngine::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->metaObject : &staticMetaObject;
}

void *QtJambi_SignalWrapper_QScriptEngine::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_QtJambi_SignalWrapper_QScriptEngine))
        return static_cast<void*>(const_cast< QtJambi_SignalWrapper_QScriptEngine*>(this));
    return QObject::qt_metacast(_clname);
}

int QtJambi_SignalWrapper_QScriptEngine::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QObject::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        switch (_id) {
        case 0: __qt_signalwrapper_signalHandlerException((*reinterpret_cast< const QScriptValue(*)>(_a[1]))); break;
        default: ;
        }
        _id -= 1;
    }
    return _id;
}
QT_END_MOC_NAMESPACE
