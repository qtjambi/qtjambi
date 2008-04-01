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

#ifndef LOOKACTION_H
#define LOOKACTION_H

#include "gameaction.h"

class LookAction: public GameAction
{
public:
    inline LookAction() : GameAction(Game::Look)
    {
    }
    virtual ~LookAction(){}

    virtual bool perform(GameScene *scene);
    virtual GameAction *clone() const;
};

#endif
