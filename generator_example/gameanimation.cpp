#include "gameanimation.h"

GameAnimation::GameAnimation(Game::AnimationType type) 
    : m_type(type), m_speed(0), m_looping(false), m_current_frame(0)
{
}

GameAnimation::~GameAnimation()
{
}

bool GameAnimation::update()
{
    int elapsed = m_time.elapsed();
    if (m_speed > 0 && !m_frames.isEmpty()) {
        int frames = int(elapsed / double(m_speed));       

        if (!m_looping && frames + m_current_frame >= m_frames.size())
            m_current_frame = m_frames.size() - 1;
        else
            m_current_frame = (m_current_frame + frames) % m_frames.size();
        
        if (frames > 0) {
            m_time.restart();
            return true;
        }
    }
    return false;
}
