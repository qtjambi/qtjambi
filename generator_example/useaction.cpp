#include "useaction.h"

GameAction *UseAction::clone() const
{
    UseAction *action = new UseAction;
    action->subjects = subjects;
    action->objects = objects;

    return action;
}
