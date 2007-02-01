#ifndef PICKUPACTION_H
#define PICKUPACTION_H

#include "gameaction.h"

class PickUpAction: public GameAction
{
public:
    inline PickUpAction() : GameAction(Game::PickUp) 
    {
    }
    virtual ~PickUpAction(){}

    virtual bool perform(GameScene *scene);
    virtual GameAction *clone() const;  
};

#endif
