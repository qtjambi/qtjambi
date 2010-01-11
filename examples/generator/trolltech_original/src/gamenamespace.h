/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** $BEGIN_LICENSE$
** GNU Lesser General Public License Usage
** This file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

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
