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
