package com.trolltech.examples.qtconcurrent;

import java.lang.reflect.*;
import com.trolltech.qt.core.*;

public class RunFunction {
    public static void hello(String name) {
        System.out.println("Hello " + name + " from " + Thread.currentThread());
    }

    public static String helloReturned(String name) {
        return "Hello " + name + " from " + Thread.currentThread();
    }

    public static void main(String args[]) {
        Method hello = null;
        Method helloReturned = null;
        try {
            hello = RunFunction.class.getMethod("hello", String.class);
            helloReturned = RunFunction.class.getMethod("helloReturned", String.class);
        } catch (Exception e) {
            return ;
        }

        QFutureVoid f1 = QtConcurrent.runVoidMethod(null, hello, "James");
        QFutureVoid f2 = QtConcurrent.runVoidMethod(null, hello, "William");
        QFuture<String> f3 = QtConcurrent.run(null, helloReturned, "Virginia");
        f1.waitForFinished();
        f2.waitForFinished();
        f3.waitForFinished();

        System.out.println(f3.results().get(0));
    }

}
