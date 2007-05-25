/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.qt;

/**
 * @exclude
 */
public class QThreadManager {
    
    private static class NativeResourcesReleaseThread extends Thread {
        private int m_sleepTime = 100;
        
        public NativeResourcesReleaseThread() { 
            setDaemon(true);
        }
        
        public void run() {
            while (true) {
                try { sleep(m_sleepTime); } catch (Exception e) { };
                boolean release = releaseNativeResources();
                m_sleepTime = release ? 100 : Math.min(m_sleepTime * 2, 60 * 1000);
            }
        }
    }
    
    public static void initialize() {
        new NativeResourcesReleaseThread().start();
    }
    
    public synchronized static native boolean releaseNativeResources(); 
}
