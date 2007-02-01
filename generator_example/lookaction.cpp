#include "lookaction.h"

bool LookAction::perform(GameScene *scene) 
{
    if (objects.size() > 0) {
        return false;
    } else {
        return GameAction::perform(scene);
    }
}

GameAction *LookAction::clone() const
{
    LookAction *action = new LookAction;
    action->subjects = subjects;
    action->objects = objects;

    return action;
}
