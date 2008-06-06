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

package com.trolltech.benchmarks;

import com.trolltech.qt.core.*;

public class SetObjectName {
    public static void main(String args[]) {

        int runningTime = 8000;
        int timesPerRound = 100;

        {
            QTime t = new QTime();
            QObject o = new QObject();
            long counter = 0;
            t.start();
            while (t.elapsed() < runningTime) {
                for (int i=0; i<timesPerRound; ++i) {
                    o.setObjectName("abcde");
                }
                counter += timesPerRound;
            }

            double ops = ((double) counter) / t.elapsed() * 1000;
            System.out.printf("Calling QObject::setObjectName(): %.2fM times pr second\n", ops / 1000000.);
        }

        {
            QTime t = new QTime();
            QFile f = new QFile();
            long counter = 0;
            t.start();
            while (t.elapsed() < runningTime) {
                for (int i=0; i<timesPerRound; ++i) {
                    f.unsetError();
                }
                counter += timesPerRound;
            }

            double ops = ((double) counter) / t.elapsed() * 1000;
            System.out.printf("Calling QFile::unsetError(): %.2fM times pr second\n", ops / 1000000.);
        }


    }
}