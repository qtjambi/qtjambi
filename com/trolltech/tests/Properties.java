package com.trolltech.tests;

import com.trolltech.qt.*;;
import java.lang.reflect.*;
import java.util.*;

public class Properties {
    public static void main(String args[]) throws Exception {
        if (args.length == 0) {
            System.out.println("Please specify class name");
            return;
        }

        HashMap<String, QtPropertyManager.Entry> entries =
            QtPropertyManager.findPropertiesRecursive(Class.forName(args[0]));

        Method DEFAULT_TRUE = null;
        Method DEFAULT_FALSE = null;

        try {
            DEFAULT_TRUE = QtPropertyManager.class.getDeclaredMethod("__qt_default_true");
            DEFAULT_FALSE = QtPropertyManager.class.getDeclaredMethod("__qt_default_false");
        } catch (Exception e) { }


        for (QtPropertyManager.Entry e : entries.values()) {

            System.out.print(e.read != null ? " R" : "  ");
            System.out.print(e.write != null ? " W" : "  ");
            System.out.print(e.reset != null ? " =" : "  ");

            if (e.designable == DEFAULT_TRUE || e.designable == null)
                System.out.print(" D");
            else if (e.designable == DEFAULT_FALSE)
                System.out.print("  ");
            else
                System.out.print(" *");

            Class cl = null;
            if (e.read != null)
                cl = e.read.getDeclaringClass();
            else if (e.write != null)
                cl = e.write.getDeclaringClass();

            System.out.printf(" :: %30s ; " + cl, e.name);

            System.out.println();
        }
    }
}
