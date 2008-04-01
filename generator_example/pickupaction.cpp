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

#include "pickupaction.h"

#include "gameobject.h"
#include "gamescene.h"

bool PickUpAction::perform(GameScene *scene)
{
    if (objects.size() > 0) {
        return false;
    } else {
        QList<AbstractGameObject *> cant_pick_up;
        for (int i=0; i<subjects.size(); ++i) {
            if (!(subjects.at(i)->objectFlags() & Game::CanPickUp)) {
                cant_pick_up.append(subjects.at(i));
            } else {
                scene->addToEgoInventory(subjects.at(i));
            }
        }

        QString msg;
        for (int i=0; i<cant_pick_up.size(); ++i) {
            if (i == 0) {
                msg += "You can't pick up ";
            } else if (i == cant_pick_up.size() - 1) {
                msg += " and ";
            } else {
                msg += ", ";
            }

            msg += cant_pick_up.at(i)->name();
        }

        if (!msg.isEmpty())
            scene->message(msg);

        return cant_pick_up.size() != subjects.size();
    }
}

GameAction *PickUpAction::clone() const
{
    PickUpAction *action = new PickUpAction;
    action->subjects = subjects;
    action->objects = objects;

    return action;
}

