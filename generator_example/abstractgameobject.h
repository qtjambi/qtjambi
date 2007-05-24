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

#ifndef ABSTRACTGAMEOBJECT_H
#define ABSTRACTGAMEOBJECT_H

#include "gamenamespace.h"
#include "point3d.h"

#include <QtGui/QGraphicsItem>

class AbstractGameObject: public QGraphicsItem
{
public:
    virtual void perform(Game::ActionType action, AbstractGameObject **args, int num_args) = 0;
    virtual Game::WalkingDirection direction() const = 0;
    virtual QString name() const = 0;
    virtual QStringList otherNames() const = 0;
    virtual Point3D position() const = 0;
    virtual void walk(Game::WalkingDirection direction) = 0;
    virtual Game::ObjectFlags objectFlags() const = 0;
};


#endif
