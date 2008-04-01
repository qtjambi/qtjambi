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

#ifndef GAMEACTION_H
#define GAMEACTION_H

#include "gamenamespace.h"

#include <QtCore/QVector>

class AbstractGameObject ;
class GameScene ;

class GameAction
{
public:
    inline GameAction(Game::ActionType type) : m_type(type)
    {
    }
    virtual ~GameAction(){}

    virtual bool perform(GameScene *scene);
    virtual GameAction *clone() const = 0;

    inline void addObject(AbstractGameObject *o) { objects.append(o); }
    inline void addSubject(AbstractGameObject *o) { subjects.append(o); }

    inline Game::ActionType type() const { return m_type; }

protected:
    QVector<AbstractGameObject *> subjects;
    QVector<AbstractGameObject *> objects;

private:
    Game::ActionType m_type;

};

#endif
