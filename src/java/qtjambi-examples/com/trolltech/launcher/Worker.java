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

package com.trolltech.launcher;

import com.trolltech.qt.core.*;

public abstract class Worker extends QObject {

    protected abstract void execute();

    public Worker(QObject parent) {
        super(parent);
    }

    public void start() {
    if (m_is_running)
        stop();
    m_is_running = true;
    m_timer_id = startTimer(m_delay);
    }

    public void stop() {
    killTimer(m_timer_id);
    m_is_running = false;
    }

    public void setDelay(int delay) {
    m_delay = delay;
    }

    public int delay() {
    return m_delay;
    }

    @Override
    protected void timerEvent(QTimerEvent e) {
    if (e.timerId() == m_timer_id) {
        execute();
        stop();
    }
    }

    private int m_delay = 250;
    private int m_timer_id;
    private boolean m_is_running;
}
