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

#ifndef USEACTION_H
#define USEACTION_H

#include "gameaction.h"

class UseAction: public GameAction
{
public:
    inline UseAction() : GameAction(Game::Use)
    {
    }
    virtual ~UseAction(){}

    virtual GameAction *clone() const;
};

#endif
