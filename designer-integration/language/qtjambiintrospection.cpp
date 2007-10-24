/****************************************************************************
**
** Copyright (C) 2007-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#include "qtjambiintrospection_p.h"
#include "qtdynamicmetaobject.h"
#include "qtjambi_core.h"
#include "qtjambi_cache.h"

#include <QtCore/QObject>
#include <QtCore/QMetaEnum>
#include <QtCore/QMetaProperty>
#include <QtCore/QMetaMethod>
#include <QtCore/QMetaObject>
#include <QtCore/QHash>
#include <QtCore/QStringList>

#if defined(signal)
#  undef signal
#endif

class QtJambiMetaObject;

class QtJambiMetaEnumerator: public QDesignerMetaEnumInterface
{
public:
    QtJambiMetaEnumerator(const QMetaEnum &regularEnum, const QtJambiMetaObject *jambiMetaObject);

    virtual bool isFlag() const;
    virtual QString key(int index) const;
    virtual int keyCount() const;
    virtual int keyToValue(const QString &key) const;
    virtual int keysToValue(const QString &keys) const;
    virtual QString name() const;
    virtual QString scope() const;
    virtual QString separator() const;
    virtual int value(int index) const;
    virtual QString valueToKey(int value) const;
    virtual QString valueToKeys(int value) const ;

private:
    QMetaEnum m_regular_enum;
    const QtJambiMetaObject *m_jambi_meta_object;
};

class QtJambiMetaProperty: public QDesignerMetaPropertyInterface
{
public:
    QtJambiMetaProperty(const QMetaProperty &regularProperty, const QtJambiMetaObject *jambiMetaObject);
    virtual ~QtJambiMetaProperty();

    virtual const QDesignerMetaEnumInterface *enumerator() const;

    virtual Kind kind() const;
    virtual AccessFlags accessFlags() const;
    virtual Attributes attributes(const QObject *object = 0) const;

    virtual QVariant::Type type() const;
    virtual QString name() const;
    virtual QString typeName() const;
    virtual int userType() const;
    virtual bool hasSetter() const;

    virtual QVariant read(const QObject *object) const;
    virtual bool reset(QObject *object) const;
    virtual bool write(QObject *object, const QVariant &value) const;

private:
    QMetaProperty m_regular_property;
    const QtJambiMetaObject *m_jambi_meta_object;
    QtJambiMetaEnumerator *m_enumerator;
};

class QtJambiMetaMethod: public QDesignerMetaMethodInterface
{
public:
    QtJambiMetaMethod(const QMetaMethod &regularMethod, const QtJambiMetaObject *jambiMetaObject, int index);

    virtual Access access() const ;
    virtual MethodType methodType() const;
    virtual QStringList parameterNames() const;
    virtual QStringList parameterTypes() const;
    virtual QString signature() const;
    virtual QString normalizedSignature() const;
    virtual QString tag() const;
    virtual QString typeName() const ;

private:
    QStringList byteArraysToStrings(const QList<QByteArray> &) const;

    QString m_java_signature;
    QMetaMethod m_regular_method;
    const QtJambiMetaObject *m_jambi_meta_object;
};

class QtJambiMetaObject: public QDesignerMetaObjectInterface 
{
public:
    QtJambiMetaObject(const QMetaObject *regularMetaObject);
    virtual ~QtJambiMetaObject();

    virtual QString className() const;
    virtual const QDesignerMetaEnumInterface *enumerator(int index) const;
    virtual int enumeratorCount() const;
    virtual int enumeratorOffset() const;

    virtual int indexOfEnumerator(const QString &name) const;
    virtual int indexOfMethod(const QString &method) const;
    virtual int indexOfProperty(const QString &name) const;
    virtual int indexOfSignal(const QString &signal) const;
    virtual int indexOfSlot(const QString &slot) const;

    virtual const QDesignerMetaMethodInterface *method(int index) const;
    virtual int methodCount() const;
    virtual int methodOffset() const;

    virtual const  QDesignerMetaPropertyInterface *property(int index) const;
    virtual int propertyCount() const;
    virtual int propertyOffset() const;

    virtual const QDesignerMetaObjectInterface *superClass() const;
    virtual const QDesignerMetaPropertyInterface *userProperty() const;

    const QMetaObject *metaObject() const;
    const bool metaObjectIsDynamic() const;
    QString fullClassName() const;
    void resolve();

private:
    const QMetaObject *m_regular_meta_object;

    QtJambiMetaEnumerator **m_enumerators;     
    QtJambiMetaProperty   **m_properties;
    QtJambiMetaMethod     **m_methods;

    uint m_meta_object_is_dynamic : 1;
    uint m_reserved               : 31;
};


/**
 * QtJambiMetaEnumerator
 */

QtJambiMetaEnumerator::QtJambiMetaEnumerator(const QMetaEnum &regularEnum, const QtJambiMetaObject *jambiMetaObject)
    : m_regular_enum(regularEnum), m_jambi_meta_object(jambiMetaObject)
{
}

bool QtJambiMetaEnumerator::isFlag() const
{
    return m_regular_enum.isFlag();
}

QString QtJambiMetaEnumerator::key(int index) const
{
    return m_regular_enum.key(index);
}

int QtJambiMetaEnumerator::keyCount() const
{
    return m_regular_enum.keyCount();
}

int QtJambiMetaEnumerator::keyToValue(const QString &key) const
{
    return m_regular_enum.keyToValue(key.toLatin1());
}

int QtJambiMetaEnumerator::keysToValue(const QString &keys) const
{
    return m_regular_enum.keysToValue(keys.toLatin1());
}

QString QtJambiMetaEnumerator::name() const
{
    QString full_name = m_regular_enum.name();
    int pos = full_name.lastIndexOf(QLatin1String("::"));
    if (pos >= 0) 
        return full_name.right(full_name.length() - pos - 2);
    else
        return full_name;
}

QString QtJambiMetaEnumerator::scope() const
{
    JNIEnv *env = qtjambi_current_environment();
    Q_ASSERT(env != 0);

    QString scope = m_jambi_meta_object->fullClassName() + QLatin1String("$") + name();

    // The scope for the enum values needs to be the enum type, not the flag type
    // so we need to look this up in Java.
    if (isFlag())
        scope = qtjambi_enum_name_for_flags_name(env, scope.replace(QLatin1String("."), QLatin1String("/")));

    return scope.replace(QLatin1String("$"), QLatin1String("."));
}

QString QtJambiMetaEnumerator::separator() const
{
    return QLatin1String(".");
}

int QtJambiMetaEnumerator::value(int index) const
{
    return m_regular_enum.value(index);
}

QString QtJambiMetaEnumerator::valueToKey(int value) const
{
    return m_regular_enum.valueToKey(value);
}

QString QtJambiMetaEnumerator::valueToKeys(int value) const 
{
    return m_regular_enum.valueToKeys(value);
}

static const QtJambiMetaObject *qtjambi_meta_object_stash(const QMetaObject *metaObject) 
{
    if (metaObject == 0) // it could happen to anyone
        return 0;

    static QHash<QString, const QtJambiMetaObject *> meta_object_stash;
    QLatin1String className(metaObject->className());

    const QtJambiMetaObject *returned = meta_object_stash.value(className, 0);
    if (returned == 0) {
        QtJambiMetaObject *temp = new QtJambiMetaObject(metaObject);
        meta_object_stash.insert(className, temp);
        temp->resolve();

        returned = temp;
    }

    return returned;
}

/**
 * QtJambiMetaProperty
 */
QtJambiMetaProperty::QtJambiMetaProperty(const QMetaProperty &regularProperty, const QtJambiMetaObject *jambiMetaObject)
    : m_regular_property(regularProperty), 
      m_jambi_meta_object(jambiMetaObject), 
      m_enumerator(0)
{
    Q_ASSERT(m_jambi_meta_object != 0);

    if (regularProperty.isEnumType() || regularProperty.isFlagType()) {
        QMetaEnum property_enumerator = regularProperty.enumerator();
        const QMetaObject *enumerator_owner = property_enumerator.enclosingMetaObject();
        m_enumerator = new QtJambiMetaEnumerator(regularProperty.enumerator(), qtjambi_meta_object_stash(enumerator_owner));
    }
}

QtJambiMetaProperty::~QtJambiMetaProperty()
{
    delete m_enumerator;
}

const QDesignerMetaEnumInterface *QtJambiMetaProperty::enumerator() const
{
    return m_enumerator;
}

QDesignerMetaPropertyInterface::Kind QtJambiMetaProperty::kind() const
{
    if (m_regular_property.isFlagType()) return FlagKind;
    if (m_regular_property.isEnumType()) return EnumKind;
    return OtherKind;
}

QDesignerMetaPropertyInterface::AccessFlags QtJambiMetaProperty::accessFlags() const
{
    return AccessFlags( (m_regular_property.isReadable()   ? ReadAccess  : 0)
                     |  (m_regular_property.isWritable()   ? WriteAccess : 0)
                     |  (m_regular_property.isResettable() ? ResetAccess : 0));
}

QDesignerMetaPropertyInterface::Attributes QtJambiMetaProperty::attributes(const QObject *object) const
{
    return Attributes( (m_regular_property.isDesignable(object) ? DesignableAttribute : 0)
                    |  (m_regular_property.isScriptable(object) ? ScriptableAttribute : 0) 
                    |  (m_regular_property.isStored(object)     ? StoredAttribute     : 0)
                    |  (m_regular_property.isUser(object)       ? UserAttribute       : 0));
}

QVariant::Type QtJambiMetaProperty::type() const
{
    return m_regular_property.type();
}

QString QtJambiMetaProperty::name() const
{
    return QLatin1String(m_regular_property.name());
}

QString QtJambiMetaProperty::typeName() const
{
    return QLatin1String(m_regular_property.typeName());
}

int QtJambiMetaProperty::userType() const
{
    return m_regular_property.userType();
}

bool QtJambiMetaProperty::hasSetter() const
{
    return m_regular_property.hasStdCppSet();
}

QVariant QtJambiMetaProperty::read(const QObject *object) const
{
    return m_regular_property.read(object);
}

bool QtJambiMetaProperty::reset(QObject *object) const
{
    return m_regular_property.reset(object);
}

bool QtJambiMetaProperty::write(QObject *object, const QVariant &value) const
{
    return m_regular_property.write(object, value);
}

/**
 * QtJambiMetaMethod
 */
QtJambiMetaMethod::QtJambiMetaMethod(const QMetaMethod &regularMethod, const QtJambiMetaObject *jambiMetaObject, int index)
    : m_regular_method(regularMethod), m_jambi_meta_object(jambiMetaObject)
{
    Q_ASSERT(jambiMetaObject != 0);

    if (m_jambi_meta_object->metaObjectIsDynamic()) {
        // If the meta object is dynamic, then we can query it for the original
        // signature of the method
        const QtDynamicMetaObject *dynamic_meta_object = static_cast<const QtDynamicMetaObject *>(jambiMetaObject->metaObject());
        dynamic_meta_object->originalSignalOrSlotSignature(qtjambi_current_environment(), index, &m_java_signature);
    } else {
        // If it's not dynamic, then we can query the meta info
        QString qt_signature = QLatin1String(m_regular_method.enclosingMetaObject()->className()) + QLatin1String("::") + m_regular_method.signature();
        m_java_signature = getJavaName(qt_signature.toLatin1());

    }

    if (methodType() == Signal) {
        m_java_signature.replace(QLatin1String("()"), QLatin1String(""))
                        .replace(QLatin1String("("), QLatin1String("<"))
                        .replace(QLatin1String(")"), QLatin1String(">"));
    }

    int pos = methodType() == Signal ? m_java_signature.lastIndexOf(QLatin1String("<")) : m_java_signature.lastIndexOf(QLatin1String("("));
    pos = m_java_signature.lastIndexOf(QLatin1String("."), pos);

    if (pos >= 0)
        m_java_signature = m_java_signature.right(m_java_signature.length() - pos - 1);

    m_java_signature = m_java_signature.trimmed();
}

QDesignerMetaMethodInterface::Access QtJambiMetaMethod::access() const
{
    QMetaMethod::Access a = m_regular_method.access();
    if (a == QMetaMethod::Private) return Private;
    else if (a == QMetaMethod::Public) return Public;
    else return Protected;
}

QDesignerMetaMethodInterface::MethodType QtJambiMetaMethod::methodType() const
{
    QMetaMethod::MethodType m = m_regular_method.methodType();
    if (m == QMetaMethod::Signal) return Signal;
    else if (m == QMetaMethod::Slot) return Slot;
    else return Method;
}

QStringList QtJambiMetaMethod::byteArraysToStrings(const QList<QByteArray> &byteArrays) const
{
    QStringList strings;
    foreach (QByteArray byteArray, byteArrays) 
        strings.append(QLatin1String(byteArray));
    
    return strings;
}

QStringList QtJambiMetaMethod::parameterNames() const
{
    return byteArraysToStrings(m_regular_method.parameterNames());
}

QStringList QtJambiMetaMethod::parameterTypes() const
{
    return byteArraysToStrings(m_regular_method.parameterTypes());
}

QString QtJambiMetaMethod::signature() const
{
    qDebug("returning %s", qPrintable(m_java_signature));
    return m_java_signature;
}

QString QtJambiMetaMethod::normalizedSignature() const
{
    return m_java_signature;
}

QString QtJambiMetaMethod::tag() const
{
    return QLatin1String(m_regular_method.tag());
}

QString QtJambiMetaMethod::typeName() const
{
    return QLatin1String(m_regular_method.typeName());
}

/**
 * QtJambiMetaObject
 */

QtJambiMetaObject::QtJambiMetaObject(const QMetaObject *regularMetaObject) 
    : m_regular_meta_object(regularMetaObject), m_enumerators(0), m_properties(0), 
      m_methods(0), m_meta_object_is_dynamic(qtjambi_metaobject_is_dynamic(regularMetaObject))
{ 
    Q_ASSERT(m_regular_meta_object != 0);
}


QtJambiMetaObject::~QtJambiMetaObject() 
{
    {
        int count = enumeratorCount();
        for (int i=0; i<count; ++i) 
            delete m_enumerators[i];
        delete[] m_enumerators;
    }

    {
        int count = propertyCount();
        for (int i=0; i<count; ++i) 
            delete m_properties[i];
        delete[] m_properties;
    }

    {
        int count = methodCount();
        for (int i=0; i<count; ++i) 
            delete m_methods[i];
        delete[] m_methods;
    }
}

void QtJambiMetaObject::resolve() 
{
    int count = enumeratorCount();
    if (count > 0) {
        m_enumerators = new QtJambiMetaEnumerator *[count];
        for (int i=0; i<count; ++i)
            m_enumerators[i] = new QtJambiMetaEnumerator(m_regular_meta_object->enumerator(i), this);
    } else {
        m_enumerators = 0;
    }

    count = propertyCount();
    if (count > 0) {
        m_properties = new QtJambiMetaProperty *[count];
        for (int i=0; i<count; ++i) 
            m_properties[i] = new QtJambiMetaProperty(m_regular_meta_object->property(i), this);
    } else {
        m_properties = 0;
    }

    count = methodCount();
    if (count > 0) {
        m_methods = new QtJambiMetaMethod *[count];
        for (int i=0; i<count; ++i) 
            m_methods[i] = new QtJambiMetaMethod(m_regular_meta_object->method(i), this, i);
    } else {
        m_methods = 0;
    }    
}

QString QtJambiMetaObject::fullClassName() const
{
    if (m_meta_object_is_dynamic)
        return className().replace(QLatin1String("::"), QLatin1String("."));
    else
        return getJavaName(className()).replace(QLatin1String("/"), QLatin1String("."));
}

QString QtJambiMetaObject::className() const 
{
    return QString::fromLatin1(m_regular_meta_object->className()).replace(QLatin1String("::"), QLatin1String("."));
}

const QDesignerMetaObjectInterface *QtJambiMetaObject::superClass() const
{
    return qtjambi_meta_object_stash(m_regular_meta_object->superClass());
}

const QDesignerMetaPropertyInterface *QtJambiMetaObject::userProperty() const
{ 
    return 0;
}

const QDesignerMetaEnumInterface *QtJambiMetaObject::enumerator(int index) const
{
    return m_enumerators[index];    
}

int QtJambiMetaObject::enumeratorCount() const
{
    return m_regular_meta_object->enumeratorCount();
}

int QtJambiMetaObject::enumeratorOffset() const
{
    return m_regular_meta_object->enumeratorOffset();
}

int QtJambiMetaObject::indexOfEnumerator(const QString &name) const
{
    return m_regular_meta_object->indexOfEnumerator(name.toLatin1());
}

int QtJambiMetaObject::indexOfMethod(const QString &method) const
{
    return m_regular_meta_object->indexOfMethod(method.toLatin1());
}

int QtJambiMetaObject::indexOfProperty(const QString &name) const
{
    return m_regular_meta_object->indexOfProperty(name.toLatin1());
}

int QtJambiMetaObject::indexOfSignal(const QString &signal) const
{
    return m_regular_meta_object->indexOfSignal(signal.toLatin1());
}

int QtJambiMetaObject::indexOfSlot(const QString &slot) const
{
    return m_regular_meta_object->indexOfSlot(slot.toLatin1());
}

const QDesignerMetaMethodInterface *QtJambiMetaObject::method(int index) const
{
    return m_methods[index];
}

int QtJambiMetaObject::methodCount() const
{
    return m_regular_meta_object->methodCount();
}

int QtJambiMetaObject::methodOffset() const
{
    return m_regular_meta_object->methodOffset();
}

const QDesignerMetaPropertyInterface *QtJambiMetaObject::property(int index) const
{
    return m_properties[index];
}

int QtJambiMetaObject::propertyCount() const 
{
    return m_regular_meta_object->propertyCount();
}

int QtJambiMetaObject::propertyOffset() const
{
    return m_regular_meta_object->propertyOffset();
}

const QMetaObject *QtJambiMetaObject::metaObject() const
{
    return m_regular_meta_object;
}

const bool QtJambiMetaObject::metaObjectIsDynamic() const
{
    return m_meta_object_is_dynamic;
}


/**
 * QtJambiIntrospection
 */
QtJambiIntrospection::QtJambiIntrospection() { }

QtJambiIntrospection::~QtJambiIntrospection() { }

const QDesignerMetaObjectInterface* QtJambiIntrospection::metaObject(const QObject *object) const
{
    return qtjambi_meta_object_stash(object->metaObject());
}
