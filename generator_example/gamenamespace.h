#ifndef GAMENAMESPACE_H
#define GAMENAMESPACE_H

#include <QtCore/QFlags>

namespace Game {
    enum ObjectFlag {
        Blocking = 0x01,
        CanPickUp = 0x02,
        Flipped = 0x04
    };
    typedef QFlags<ObjectFlag> ObjectFlags;

    enum WalkingDirection {
        NoDirection,
        Left,
        Right,
        Up,
        Down
    };

    enum AnimationType {
        NoAnimation             = 0x000,
        WalkingHorizontally     = 0x001,
        WalkingFromScreen       = 0x002, 
        WalkingToScreen         = 0x003,
        StandingStill           = 0x004,

        UserAnimation           = 0xf00
    };

    enum ActionType {
        NoAction                = 0x000,
        Use                     = 0x001,
        Look                    = 0x002,
        Take                    = 0x003,
        PickUp                  = 0x003,

        UserAction              = 0xf00
    };
};

#endif
