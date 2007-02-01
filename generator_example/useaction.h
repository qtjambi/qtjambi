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
