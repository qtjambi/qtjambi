/****************************************************************************
**
** Copyright (C) 2012 Darryl L. Miles.  All rights reserved.
** Copyright (C) 2012 D L Miles Consulting Ltd.  All rights reserved.
**
** This file is part of Qt Jambi.
**
**
** $BEGIN_LICENSE$
** GNU Lesser General Public License Usage
** This file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
**
** In addition, as a special exception, the copyright holders grant you
** certain additional rights. These rights are described in the Nokia Qt
** LGPL Exception version 1.0, included in the file LGPL_EXCEPTION.txt in
** this package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 2.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL2 included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 2.0 requirements will be
** met: http://www.gnu.org/licenses/gpl-2.0.html
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL3 included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html
** $END_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef GENERATORFEATURE_H
#define GENERATORFEATURE_H

// The purpose of this file is to test and verify generator code generation
//  and features.

#include <QtCore/QObject>
#include <QtCore/QString>
#include <QtCore/QStringList>
#include <QtCore/QPair>
#include <QtCore/QHash>
#include <QtCore/QSet>
#include <QtCore/QList>

#include <QtCore/QDebug>

extern uint qHash(const QList<int>& list);


class GenFeatSimple
{
public:
    GenFeatSimple() { }
};

class GenFeatTemplate
{
public:
    GenFeatTemplate() { }

    void methodSet(const QStringList &newList) {
        list = newList;
    }
    const QStringList &methodGet() const {
        return list;
    }

    QStringList list;
};

// QList<int>
class GenFeatTemplate1
{
public:
    GenFeatTemplate1() { }

    void methodSet(const QList<int> &newValue) {
        value = newValue;
    }
    QList<int> methodGet() {
        return value;
    }

    QList<int> value;
};

// QSet<QList<int> >
class GenFeatTemplate2
{
public:
    GenFeatTemplate2() { }

    void methodSet(QSet<QList<int> > &newValue) {
        value = newValue; // CHECKME this needs to be a deep-copy
    }
    QSet<QList<int> > methodGet() {
        return value;
    }

    QSet<QList<int> > value;
};

// QHash<int,QSet<QList<int> > >
class GenFeatTemplate3
{
public:
    GenFeatTemplate3() { }

    void methodSet(const QHash<int,QSet<QList<int> > > &newValue) {
        value = newValue;
    }
    QHash<int,QSet<QList<int> > > methodGet() {
        return value;
    }

    QHash<int,QSet<QList<int> > > value;
};

// QList<QList<int> >
class GenFeatTemplate4
{
public:
    GenFeatTemplate4() { }

    void methodSet(QList<QList<int> > &newValue) {
        value = newValue; // CHECKME this needs to be a deep-copy
    }
    QList<QList<int> > methodGet() {
        return value;
    }

    QList<QList<int> > value;
};

// QList<QList<QList<int> > >
class GenFeatTemplate5
{
public:
    GenFeatTemplate5() { }

    void methodSet(QList<QList<QList<int> > > &newValue) {
        value = newValue; // CHECKME this needs to be a deep-copy
    }
    QList<QList<QList<int> > > methodGet() {
        return value;
    }

    QList<QList<QList<int> > > value;
};

// QList<QList<QList<QList<int> > > >
class GenFeatTemplate6
{
public:
    GenFeatTemplate6() { }

    void methodSet(QList<QList<QList<QList<int> > > > &newValue) {
        value = newValue; // CHECKME this needs to be a deep-copy
    }
    QList<QList<QList<QList<int> > > > methodGet() {
        return value;
    }

    QList<QList<QList<QList<int> > > > value;
};

// QList<QList<QList<QList<QList<int> > > > >
class GenFeatTemplate7
{
public:
    GenFeatTemplate7() { }

    void methodSet(QList<QList<QList<QList<QList<int> > > > > &newValue) {
        value = newValue; // CHECKME this needs to be a deep-copy
    }
    QList<QList<QList<QList<QList<int> > > > > methodGet() {
        return value;
    }
    bool doCppThing() {
        if(value.size() != 1)
            return false;

        const QList<QList<QList<QList<int> > > > &l1 = value[0];
        if(l1.size() != 1)
            return false;

        const QList<QList<QList<int> > > &l2 = l1[0];
        if(l2.size() != 1)
            return false;

        const QList<QList<int> > &l3 = l2[0];
        if(l3.size() != 1)
            return false;

        const QList<int> &l4 = l3[0];
        if(l4.size() != 1)
            return false;

        const int &l5 = l4[0];
        if(l5 != 42)
            return false;

        return true;
    }

    QList<QList<QList<QList<QList<int> > > > > value;
};

class GenFeatTemplate8
{
public:
    GenFeatTemplate8() { }

    QPair<int, QPair<int, int> > methodTest() const {
        return qMakePair(1, qMakePair(2, 3));
    }
};

#endif // GENERATORFEATURE_H
