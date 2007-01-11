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

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

public class QNoSuchSlotException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Object reciver;
    private String slotSignature;
    private String message;

    public QNoSuchSlotException(String message) {
        if (message != null)
            this.message = message;
        else
            this.message = "";
    }

    public QNoSuchSlotException(Object reciver, String slotSignature) {
        this.reciver = reciver;
        this.slotSignature = slotSignature;
    }

    public String getMessage() {
        if (message != null)
            return message;
        if (slotSignature.equals("")) {
            return "Slot can't be an empty string";
        } else {
            String res = "\n\nCould not find slot with signature: " + slotSignature + "\n";
            res += "Possible matching methods:\n";
            Vector<Method> possibleMethods = findPossibleFunctionRecursive(reciver, slotSignature);

            for (Iterator iter = possibleMethods.iterator(); iter.hasNext();) {
                Method method = (Method) iter.next();
                res += method.getName() + "(";
                Class[] parameters = method.getParameterTypes();
                for (int i = 0; i < parameters.length; i++) {
                    if (i > 0)
                        res += " ";
                    res += parameters[i].getName();
                }
                res += ")\n";
            }
            return res;
        }
    }

    private static Vector<Method> findPossibleFunctionRecursive(Object reciver, String signature) {
        Class cls = reciver.getClass();

        int pos = signature.indexOf('(');
        String name = signature.substring(0, pos).trim();
        int pos2 = signature.indexOf(')', pos);
        String strTypes = signature.substring(pos + 1, pos2).trim();

        String argumentTypes[];

        if (strTypes.length() == 0)
            argumentTypes = new String[0];
        else
            argumentTypes = strTypes.split(",");

        for (int i = 0; i < argumentTypes.length; ++i)
            argumentTypes[i] = argumentTypes[i].replace(" ", "");

        return findPossibleFunctionRecursiveHelper(cls, name, argumentTypes, new Vector<Method>());
    }

    private static Vector<Method> findPossibleFunctionRecursiveHelper(Class cls,
            String functionName, String argumentTypes[], Vector<Method> res) {
        Method methods[] = cls.getDeclaredMethods();

        for (Method m : methods) {
            if (!m.getName().equalsIgnoreCase(functionName))
                continue;

            Class a[] = m.getParameterTypes();
            if (a.length != argumentTypes.length)
                continue;

            res.add(m);
        }

        cls = cls.getSuperclass();
        if (cls == null)
            return res;
        else
            return findPossibleFunctionRecursiveHelper(cls, functionName, argumentTypes, res);
    }
}