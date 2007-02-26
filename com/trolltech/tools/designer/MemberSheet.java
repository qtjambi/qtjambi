/*************************************************************************
 *
 * Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
 *
 * This file is part of $PRODUCT$.
 *
 * $CPP_LICENSE$
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 *************************************************************************/

package com.trolltech.tools.designer;

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;

import java.lang.reflect.*;
import java.util.*;

public class MemberSheet extends JambiMemberSheet {

    private interface Entry {
        String name();
        String signature();
        String group();
    }

    private class SignalEntry implements Entry {
        Field signal;

        public String name() {
            return signal.getName();
        }

        public String signature() {
            StringBuilder s = new StringBuilder();
            s.append(signal.getName());
            Type type = signal.getGenericType();

            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                Type types[] = pt.getActualTypeArguments();

                if (types.length > 0)
                    s.append("<");

                for (int i = 0; i < types.length; ++i) {
                    Class signal_type = (Class) types[i];
                    if (i != 0)
                        s.append(",");
                    s.append(signal_type.getName());
                }

                if (types.length > 0)
                    s.append(">");
            }


            return s.toString();
        }

        public String group() {
            return signal.getDeclaringClass().getName();
        }
    }

    private class SlotEntry implements Entry {
        Method method;

        public String group() { return method.getClass().getName(); }
        public String name() { return method.getName(); }
        public String signature() {
            StringBuilder s = new StringBuilder();
            s.append(method.getName());
            s.append("(");

            Class cl[] = method.getParameterTypes();
            for (int i=0; i<cl.length; ++i) {
                if (i != 0)
                    s.append(",");
                s.append(cl[i].getName());
            }

            s.append(")");

            return s.toString();
        }
    }

    public MemberSheet(QObject object, QObject parent) {
        super(parent);
        this.object = object;
        build();
    }

    public int count() {
        return entries.size();
    }

    public String declaredInClass(int i) {
        return entries.get(i).group();
    }

    public int indexOf(String name) {
        System.out.println("index of: " + name);
        return 0;
    }

    public boolean inheritedFromWidget(int i) {
        return false;
    }

    public boolean isSignal(int i) {
        return !(entries.get(i) instanceof SlotEntry);
    }

    public boolean isSlot(int i) {
        return entries.get(i) instanceof SlotEntry;
    }

    public boolean isVisible(int i) {
        return true;
    }

    public String memberGroup(int i) {
        return entries.get(i).group();
    }

    public String memberName(int i) {
        return entries.get(i).name();
    }

    public List<QByteArray> parameterNames(int i) {
        return null;
    }

    public List<QByteArray> parameterTypes(int i) {
        return null;
    }

    public void setMemberGroup(int i, String arg__2) {
    }

    public void setVisible(int i, boolean arg__2) {
    }

    public String signature(int index) {
        return entries.get(index).signature();
    }

    public static boolean signalMatchesSlot(String signal, String slot) {

        // void slots always match...
        if (slot.contains("()"))
            return true;

        int signalIndex = signal.indexOf('<');

        // Match only if () slot which is covered above already...
        if (signalIndex < 0)
            return false;

        int slotIndex = slot.indexOf('(');
        if (slotIndex < 0) {
            throw new IllegalArgumentException("slot doesn't contain () as expected, '"
                                               + slot + "'");
        }

        String signalArguments[] = signal.substring(signalIndex + 1, signal.length() - 1).split(",");
        String slotArguments[] = slot.substring(slotIndex + 1, slot.length() - 1).split(",");

        if (slotArguments.length > signalArguments.length)
            return false;

        for (int i=0; i<slotArguments.length; ++i) {
            if (!matchTypes(signalArguments[i], slotArguments[i]))
                return false;
        }

        return true;
    }

    private static boolean matchTypes(String a, String b) {
        return (a.equals(b) || (typeMap.get(a) != null && typeMap.get(a).equals(b)));
    }

    private void build(Class cl, List<Entry> entries) {
        buildSlots(cl, entries);
        buildSignals(cl, entries);
    }

    private static boolean shouldReject(Method m) {
        if (m.isAnnotationPresent(QtBlockedSlot.class)) return true;
        if (Modifier.isStatic(m.getModifiers())) return true;

        // Assume a get'er function...
        if (m.getReturnType() != void.class && m.getParameterTypes().length == 0) return true;

        String n = m.getName();
        if (n.startsWith("__qt_")) return true;
        if (n.endsWith("Event")) return true;
        if (n.equals("event") || n.equals("eventFilter")) return true;

        return false;
    }

    private void buildSlots(Class cl, List<Entry> entries) {
        Method methods[] = cl.getMethods();
        for (Method m : methods) {
            if (shouldReject(m))
                continue;
            SlotEntry e = new SlotEntry();
            e.method = m;
            entries.add(e);
        }
    }

    private static boolean shouldReject(Field f) {
        return !AbstractSignal.class.isAssignableFrom(f.getType());
    }

    private void buildSignals(Class cl, List<Entry> entries) {
        Field fields[] = cl.getFields();
        for (Field f : fields) {
            if (shouldReject(f))
                continue;

            SignalEntry e = new SignalEntry();
            e.signal = f;
            entries.add(e);
        }
    }

    private void build() {
        Class cl = object.getClass();
        List<Entry> entries = new ArrayList<Entry>();
        build(cl, entries);

        this.entries = entries;
    }

    private List<Entry> entries;
    private QObject object;

    private static HashMap<String, String> typeMap;
    static {
        typeMap = new HashMap<String, String>();
        typeMap.put("java.lang.Boolean", "boolean");
        typeMap.put("java.lang.Byte", "byte");
        typeMap.put("java.lang.Char", "char");
        typeMap.put("java.lang.Short", "short");
        typeMap.put("java.lang.Integer", "int");
        typeMap.put("java.lang.Long", "long");
        typeMap.put("java.lang.Float", "float");
        typeMap.put("java.lang.Double", "double");
    }

}
