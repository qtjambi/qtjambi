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

package com.trolltech.tests;

import com.trolltech.qt.*;
import com.trolltech.extensions.signalhandler.*;

public class TestSignalHandler extends QSignalEmitter {

    public Signal0 zero = new Signal0();
    public Signal1<String> one = new Signal1<String>();
    public Signal2<String, String> two = new Signal2<String, String>();
    public Signal3<String, String, String> three = new Signal3<String, String, String>();
    public Signal4<String, String, String, String> four = new Signal4<String, String, String, String>();
    public Signal5<String, String, String, String, String> five = new Signal5<String, String, String, String, String>();
    public Signal6<String, String, String, String, String, String> six = new Signal6<String, String, String, String, String, String>();
    public Signal7<String, String, String, String, String, String, String> seven = new Signal7<String, String, String, String, String, String, String>();
    public Signal8<String, String, String, String, String, String, String, String> eight = new Signal8<String, String, String, String, String, String, String, String>();
    public Signal9<String, String, String, String, String, String, String, String, String> nine = new Signal9<String, String, String, String, String, String, String, String, String>();


    public static void main(String args[]) {
        TestSignalHandler tester = new TestSignalHandler();

        new QSignalHandler0(tester.zero) {
            public void handle() {
                System.out.println("got zero signal...");
            }
        };

        new QSignalHandler1<String>(tester.one) {
            public void handle(String arg) {
                System.out.println("got 'one' signal: " + arg);
            }
        };

        new QSignalHandler2<String, String>(tester.two) {
            public void handle(String arg1, String arg2) {
                System.out.println("got 'two' signal: " + arg1 + ", " + arg2);
            }
        };

        new QSignalHandler3<String, String, String>(tester.three) {
            public void handle(String arg1, String arg2, String arg3) {
                System.out.println("got 'three' signal: " + arg1 + ", " + arg2 + ", " + arg3);
            }
        };

        new QSignalHandler4<String, String, String, String>(tester.four) {
            public void handle(String a, String b, String c, String d) {
                System.out.println("got 'four' signal: " + a + b + c + d);
            }
        };

        new QSignalHandler5<String, String, String, String, String>(tester.five) {
            public void handle(String a, String b, String c, String d, String e) {
                System.out.println("got 'five' signal: " + a + b + c + d + e);
            }
        };

        new QSignalHandler6<String, String, String, String, String, String>(tester.six) {
            public void handle(String a, String b, String c, String d, String e, String f) {
                System.out.println("got 'six' signal: " + a + b + c + d + e + f);
            }
        };


        new QSignalHandler7<String, String, String, String, String, String, String>(tester.seven) {
            public void handle(String a, String b, String c, String d, String e, String f, String g) {
                System.out.println("got 'seven' signal: " + a + b + c + d + e + f + g);
            }
        };

        new QSignalHandler8<String, String, String, String, String, String, String, String>(tester.eight) {
            public void handle(String a, String b, String c, String d, String e, String f, String g, String h) {
                System.out.println("got 'eight' signal: " + a + b + c + d + e + f + g + h);
            }
        };


        new QSignalHandler9<String, String, String, String, String, String, String, String, String>(tester.nine) {
            public void handle(String a, String b, String c, String d, String e, String f, String g, String h, String i) {
                System.out.println("got 'nine' signal: " + a + b + c + d + e + f + g + h + i);
            }
        };

        // The actual test...
        tester.zero.emit();
        tester.one.emit("un");
        tester.two.emit("un", "deux");
        tester.three.emit("un", "deux", "trois");
        tester.four.emit("un", "deux", "trois", "quatre");
        tester.five.emit("un", "deux", "trois", "quatre", "cinq");
        tester.six.emit("un", "deux", "trois", "quatre", "cinq", "six");
        tester.seven.emit("un", "deux", "trois", "quatre", "cinq", "six", "sept");
        tester.eight.emit("un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit");
        tester.nine.emit("un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf");

    }
}
