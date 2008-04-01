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

#ifndef GAMEANIMATION_H
#define GAMEANIMATION_H

#include "gamenamespace.h"

#include <QtCore/QObject>
#include <QtCore/QTime>
#include <QtGui/QImage>

class GameAnimation: public QObject
{
    Q_OBJECT
public:
    GameAnimation(Game::AnimationType type);
    ~GameAnimation();

    inline void addFrame(const QImage &image) { m_frames.append(image); }

    inline void setSpeed(int speed)
    {
        m_speed = speed;
    }
    inline void setLooping(bool on)
    {
        m_looping = on;
    }
    inline void setCurrentFrame(int current_frame) { m_current_frame = current_frame; m_time.restart(); }

    inline Game::AnimationType type() const { return m_type; }
    inline int speed() const { return m_speed; }
    inline bool isLooping() const { return m_looping; }
    QImage currentFrame() const { return frame(m_current_frame); }
    inline QImage frame(int index) const
    {
        if (index >= 0 && index < m_frames.size())
            return m_frames.at(index);
        else
            return QImage();
    }

public slots:
    bool update();

signals:
    void finished();

private:
    QList<QImage> m_frames;
    Game::AnimationType m_type;
    int m_speed;
    bool m_looping;
    int m_current_frame;
    QTime m_time;
};

#endif
