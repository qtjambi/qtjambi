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

package com.trolltech.benchmarks.signalslot;

import com.trolltech.qt.core.*;

public class EmitVsDirect extends QObject {
	public static final int TIME = 10000;

    public Signal2<Integer, QByteArray> signal_int_QByteArray;
    public Signal3<Integer, QByteArray, Double> signal_int_QByteArray_double;

    @SuppressWarnings("unused")
    private int x;

    @SuppressWarnings("unused")
    private void private_slot_int_QByteArray(int i, QByteArray ar) {
        x = i % ar.size();
    }

    protected void protected_slot_int_QByteArray(int i, QByteArray ar) {
        x = i % ar.size();
    }

    public void public_slot_int_QByteArray(int i, QByteArray ar) {
        x = i % ar.size();
    }

	public static void main(String args[]) {
	    QByteArray ar = new QByteArray("abcdefghijklmnopqrstuvwxyz");

	    {
		    EmitVsDirect obj = new EmitVsDirect();
		    obj.signal_int_QByteArray.connect(obj, "private_slot_int_QByteArray(int,QByteArray)");

	        QTime t = new QTime();
	        t.start();
	        long callTimes = 0;
	        while (t.elapsed() < TIME) {
	            for (int i=0; i<1000; ++i) {
	                obj.signal_int_QByteArray.emit(i, ar);
	            }
	            callTimes += 1000;
	        }
	        System.out.printf("emittions to private slot took %f ms / op\n", t.elapsed() / (double) callTimes);
	    }

	    {
		    EmitVsDirect obj = new EmitVsDirect();
		    obj.signal_int_QByteArray.connect(obj, "protected_slot_int_QByteArray(int,QByteArray)");

	        QTime t = new QTime();
	        t.start();
	        long callTimes = 0;
	        while (t.elapsed() < TIME) {
	            for (int i=0; i<1000; ++i) {
	                obj.signal_int_QByteArray.emit(i, ar);
	            }
	            callTimes += 1000;
	        }
	        System.out.printf("emittions to protected slots took %f ms / op\n",
	        			      t.elapsed() / (double) callTimes);
	    }

        {
            EmitVsDirect obj = new EmitVsDirect();
            obj.signal_int_QByteArray.connect(obj, "public_slot_int_QByteArray(int,QByteArray)");

            QTime t = new QTime();
            t.start();
            long callTimes = 0;
            while (t.elapsed() < TIME) {
                for (int i=0; i<1000; ++i) {
                    obj.signal_int_QByteArray.emit(i, ar);
                }
                callTimes += 1000;
            }
            System.out.printf("emittions to public slots took %f ms / op\n", t.elapsed() / (double) callTimes);
        }

        {
            EmitVsDirect obj = new EmitVsDirect();
            obj.signal_int_QByteArray_double.connect(obj, "public_slot_int_QByteArray(int,QByteArray)");

            QTime t = new QTime();
            t.start();
            long callTimes = 0;
            while (t.elapsed() < TIME) {
                for (int i=0; i<1000; ++i) {
                    obj.signal_int_QByteArray_double.emit(i, ar, 3.2);
                }
                callTimes += 1000;
            }
            System.out.printf("emittions to public slots, (wrong argument count) took %f ms / op\n", t.elapsed() / (double) callTimes);
        }

	    {
		    EmitVsDirect obj = new EmitVsDirect();
	    	QTime t = new QTime();
	        t.start();
	        long callTimes = 0;
	        while (t.elapsed() < TIME) {
	            for (int i=0; i<1000; ++i) {
	                obj.public_slot_int_QByteArray(i, ar);
	            }
	            callTimes += 1000;
	        }
	        System.out.printf("direct call took %f ms / op\n", t.elapsed() / (double) callTimes);
	    }
	}
}
