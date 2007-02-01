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
