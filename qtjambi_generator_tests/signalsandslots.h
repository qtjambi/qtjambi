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
public:
    SignalsAndSlots()
    {
        slot1_1_called = 0;
        slot1_2_called = 0;
        slot1_3_called = 0;
        slot2_called = 0;
        slot3_called = 0;
    }

    void disconnectAll()
    {
        disconnect(this);
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

    int slot1_1_called;
    int slot1_2_called;
    int slot1_3_called;
    int slot2_called;
    int slot3_called;

signals:
    void signal1();
    void signal2(int);
    void signal3(const QString &);

public slots:
    void slot1_1() { slot1_1_called++; }
    virtual void slot1_2() { slot1_2_called++; }
    void slot1_3() { slot1_3_called++; }
    virtual void slot2(int i) { slot2_called += i; }
    void slot3(const QString &str) { slot3_called += str.toInt(); }
};

#endif // SIGNALSANDSLOTS_H