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

#ifndef SIGNALSANDSLOTS_H
#define SIGNALSANDSLOTS_H

#include <QtCore/QObject>
#include <QtCore/QVariant>
#include <QtCore/QMetaProperty>
#include <QtCore/QStringList>

#ifndef SIGNAL
#  define SIGNAL(A) #A
#endif

#ifndef SLOT
#  define SLOT(A) #A
#endif

#ifndef emit
#  define emit
#endif

class SignalsAndSlots: public QObject
{
    Q_OBJECT

    Q_PROPERTY(QByteArray cppProperty READ cppProperty WRITE setCppProperty RESET resetCppProperty);
public:
    SignalsAndSlots()
    {
        slot1_1_called = 0;
        slot1_2_called = 0;
        slot1_3_called = 0;
        slot2_called = 0;
        slot3_called = 0;
    }

    void disconnectSignals(SignalsAndSlots *obj)
    {
        disconnect(this, SIGNAL(signal1()), obj, SLOT(slot1_1()));
        disconnect(this, SIGNAL(signal3(const QString &)), obj, SLOT(slot3(const QString &)));
    }

    void setupSignals(SignalsAndSlots *obj, int i)
    {
        connect(this, SIGNAL(signal1()), obj, SLOT(slot1_1()));
        if (i > 0)
            connect(this, SIGNAL(signal1()), obj, SLOT(slot1_2()));
        if (i > 1)
            connect(this, SIGNAL(signal1()), obj, SLOT(slot1_3()));

        connect(this, SIGNAL(signal2(int)), obj, SLOT(slot2(int)));
        connect(this, SIGNAL(signal3(const QString &)), obj, SLOT(slot3(const QString &)));
    }

    void emit_signal_1() { emit signal1(); }
    void emit_signal_2(int i) { emit signal2(i); }
    void emit_signal_3(const QString &str) { emit signal3(str); }

    bool connectSignal1ToSlot4() {
        return connect(this, SIGNAL(signal1()), this, SLOT(slot4()));
    }

    bool connectSignal4ToSlot1_1() {
        return connect(this, SIGNAL(signal4), this, SLOT(slot1_1()));
    }

    bool connectSignal4ToSlot4() {
        return connect(this, SIGNAL(signal4), this, SLOT(slot4()));
    }

    bool connectSignal5ToSlot3()
    {
        return connect(this, SIGNAL(signal5), this, SLOT(slot3(const QString &)));
    }

    void connectSignal1ToSlot1_1()
    {
        connectSignal1ToSlot1_1In(this);
    }

    void connectSignal1ToSlot1_1In(QObject *other)
    {
        connect(this, SIGNAL(signal1()), other, SLOT(slot1_1()));
    }

    void disconnectSignal1FromSlot1_1()
    {
        disconnect(this, SIGNAL(signal1()), this, SLOT(slot1_1()));
    }

    void connectSignal2ToSlot2()
    {
        connect(this, SIGNAL(signal2(int)), this, SLOT(slot2(int)));
    }

    void connectSignal6ToUnnormalizedSignature()
    {
        connect(this, SIGNAL(  signal6( const QString &, int   ) ), this, SLOT(          unnormalized_signature(   String,   int   )));
    }

    void javaSignalToCppSlot() 
    {
        connect(this, SIGNAL(aJavaSignal(const QString &, const QByteArray &)), this, SLOT(aCppSlot(const QString &, const QByteArray &)));
    }

    void disconnectAllFromObject()
    {
        disconnect();
    }

    void disconnectAllFromSignal1()
    {
        disconnect(SIGNAL(signal1()));
    }

    void disconnectReceiverFromSignal1(QObject *receiver)
    {
        disconnect(SIGNAL(signal1()), receiver);
    }

    void disconnectAllFromReceiver(QObject *receiver)
    {
        disconnect(0, receiver);
    }

    static SignalsAndSlots *createConnectedObject()
    {
        SignalsAndSlots *sas = new SignalsAndSlots;
        QObject::connect(sas, SIGNAL(signal1()), sas, SLOT(slot1_1()));
        return sas;
    }

    bool setByteArrayProperty(const QString &propertyName, const QByteArray &byteArray) {
        return setProperty(propertyName.toLatin1(), QVariant(byteArray));
    }

    QByteArray byteArrayProperty(const QString &propertyName) {
        return property(propertyName.toLatin1()).toByteArray();
    }

    void resetProperty(const QString &propertyName) {
        QMetaProperty prop = metaObject()->property(metaObject()->indexOfProperty(propertyName.toLatin1()));
        prop.reset(this);
    }

    QString classNameFromMetaObject() {
        return metaObject()->className();
    }

    QString classNameOfSuperClassFromMetaObject() {
        return metaObject()->superClass()->className();
    }

    QStringList propertyNamesFromMetaObject() {
        QStringList list;
        for (int i=0; i<metaObject()->propertyCount(); ++i)
            list.append(QLatin1String(metaObject()->property(i).name()));

        return list;
    }

    int propertyCountFromMetaObject() {
        return metaObject()->propertyCount();
    }

    int propertyCountOfSuperClassFromMetaObject() {
        return metaObject()->superClass()->propertyCount();
    }

    QByteArray cppProperty() const { return m_cppProperty; }

    void setCppProperty(const QByteArray &ba) { m_cppProperty = ba; }

    void resetCppProperty() { m_cppProperty = "it was the darkest and stormiest night evar"; }

    int slot1_1_called;
    int slot1_2_called;
    int slot1_3_called;
    int slot2_called;
    int slot3_called;

    QString received_string;
    QByteArray received_bytearray;

signals:
    void signal1();
    void signal2(int);
    void signal3(const QString &);
    void signal6(const QString &, int);

public slots:
    void slot1_1() { slot1_1_called++; }
    virtual void slot1_2() { slot1_2_called++; }
    QByteArray slot1_3() { slot1_3_called++; return QByteArray(); }
    virtual void slot2(int i) { slot2_called += i; }
    void slot3(const QString &str) { slot3_called += str.toInt(); }

private slots:
    void aCppSlot(const QString &str, const QByteArray &ba) { received_string = str; received_bytearray = ba; }

private:
    QByteArray m_cppProperty;
};

#endif // SIGNALSANDSLOTS_H
