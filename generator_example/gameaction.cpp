#include "gameaction.h"
#include "gamescene.h"
#include "abstractgameobject.h"

bool GameAction::perform(GameScene *scene) 
{
    if (subjects.size() > 0) {        
        AbstractGameObject **objects = this->objects.size() > 0 ? this->objects.data() : 0;
        for (int i=0; i<subjects.size(); ++i) {
            subjects.at(i)->perform(type(), objects, this->objects.size());
        }
    } else {
        scene->lookAround();
    }

    return true;
}
