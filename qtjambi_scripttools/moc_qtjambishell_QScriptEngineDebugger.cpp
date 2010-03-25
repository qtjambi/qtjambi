/****************************************************************************
** Meta object code from reading C++ file 'qtjambishell_QScriptEngineDebugger.h'
**
** Created: Thu Mar 25 22:04:27 2010
**      by: The Qt Meta Object Compiler version 62 (Qt 4.6.2)
**
** WARNING! All changes made in this file will be lost!
*****************************************************************************/

#include "../cpp/com_trolltech_qt_scripttools/qtjambishell_QScriptEngineDebugger.h"
#if !defined(Q_MOC_OUTPUT_REVISION)
#error "The header file 'qtjambishell_QScriptEngineDebugger.h' doesn't include <QObject>."
#elif Q_MOC_OUTPUT_REVISION != 62
#error "This file was generated using the moc from 4.6.2. It"
#error "cannot be used with the include files from this version of Qt."
#error "(The moc has changed too much.)"
#endif

QT_BEGIN_MOC_NAMESPACE
static const uint qt_meta_data_QtJambi_SignalWrapper_QScriptEngineDebugger[] = {

 // content:
       4,       // revision
       0,       // classname
       0,    0, // classinfo
       2,   14, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0,    0, // constructors
       0,       // flags
       0,       // signalCount

 // slots: signature, parameters, type, tag, flags
      45,   44,   44,   44, 0x0a,
      84,   44,   44,   44, 0x0a,

       0        // eod
};

static const char qt_meta_stringdata_QtJambi_SignalWrapper_QScriptEngineDebugger[] = {
    "QtJambi_SignalWrapper_QScriptEngineDebugger\0"
    "\0__qt_signalwrapper_evaluationResumed()\0"
    "__qt_signalwrapper_evaluationSuspended()\0"
};

const QMetaObject QtJambi_SignalWrapper_QScriptEngineDebugger::staticMetaObject = {
    { &QObject::staticMetaObject, qt_meta_stringdata_QtJambi_SignalWrapper_QScriptEngineDebugger,
      qt_meta_data_QtJambi_SignalWrapper_QScriptEngineDebugger, 0 }
};

#ifdef Q_NO_DATA_RELOCATION
const QMetaObject &QtJambi_SignalWrapper_QScriptEngineDebugger::getStaticMetaObject() { return staticMetaObject; }
#endif //Q_NO_DATA_RELOCATION

const QMetaObject *QtJambi_SignalWrapper_QScriptEngineDebugger::metaObject() const
{
    return QObject::d_ptr->metaObject ? QObject::d_ptr->metaObject : &staticMetaObject;
}

void *QtJambi_SignalWrapper_QScriptEngineDebugger::qt_metacast(const char *_clname)
{
    if (!_clname) return 0;
    if (!strcmp(_clname, qt_meta_stringdata_QtJambi_SignalWrapper_QScriptEngineDebugger))
        return static_cast<void*>(const_cast< QtJambi_SignalWrapper_QScriptEngineDebugger*>(this));
    return QObject::qt_metacast(_clname);
}

int QtJambi_SignalWrapper_QScriptEngineDebugger::qt_metacall(QMetaObject::Call _c, int _id, void **_a)
{
    _id = QObject::qt_metacall(_c, _id, _a);
    if (_id < 0)
        return _id;
    if (_c == QMetaObject::InvokeMetaMethod) {
        switch (_id) {
        case 0: __qt_signalwrapper_evaluationResumed(); break;
        case 1: __qt_signalwrapper_evaluationSuspended(); break;
        default: ;
        }
        _id -= 2;
    }
    return _id;
}
QT_END_MOC_NAMESPACE
