package com.trolltech.qt;

import com.trolltech.qt.gui.*;

import java.lang.reflect.*;
import java.util.*;

public class QtPropertyManager {
    
    private static Class QT_PROPERTY_ANNOTATION_CLASSES[] = new Class[] {
            QtPropertyReader.class,
            QtPropertyWriter.class,
            QtPropertyResetter.class
    };
    
    @SuppressWarnings("unused")
    private static void __qt_default_true() { }
    
    @SuppressWarnings("unused")
    private static void __qt_default_false() { }
    
    private static Method DEFAULT_TRUE = null;
    private static Method DEFAULT_FALSE = null;
    
    {        
        try {
            DEFAULT_TRUE = QtPropertyManager.class.getDeclaredMethod("__qt_default_true");
            DEFAULT_FALSE = QtPropertyManager.class.getDeclaredMethod("__qt_default_false");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Entry {        
        public Entry(String name) { this.name = name; }
        
        public String name;
        
        public Method read;
        public Method write;
        public Method reset;        
        
        public Method designable;
        
        public int sortOrder;
        
        boolean readable = true;
        boolean writable = true;
        
        public boolean isDesignable(Object o) {
            if (designable == null || designable == DEFAULT_TRUE)
                return true;
            else if (designable == DEFAULT_FALSE)
                return false;            
            try {
                return (Boolean) designable.invoke(o);
            } catch (Exception e) {
                e.printStackTrace();
            }                
            return false;
        }
        
        public Class type() {
            if (read != null) return read.getReturnType();
            if (write != null) return write.getParameterTypes()[0];
            return null;
        }
    }

    private static String matchAndLowercase(String s, String prefix) {
        if (s.length() == prefix.length() + 1) 
            return String.valueOf(Character.toLowerCase(s.charAt(prefix.length()))); 
        else 
            return Character.toLowerCase(s.charAt(prefix.length())) + s.substring(prefix.length()+1);
    }
    
    private static String propertyNameForRead(QtPropertyReader r, Method m) {
        String name = r.name();        
        if (name.length() == 0) {
            name = m.getName();
            if (name.startsWith(("get"))) 
                name = matchAndLowercase(name, "get");
            else if (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class) {
                if (name.startsWith("is"))
                    name = matchAndLowercase(name, "is");
                else if (name.startsWith("has"))
                    name = matchAndLowercase(name, "has");
            }            
        }
        return name;
    }
    
    private static String stripAndLowercase(String s, int length) {
        if (s.length() == length + 1) 
            return String.valueOf(Character.toLowerCase(s.charAt(length))); 
        else 
            return Character.toLowerCase(s.charAt(length)) + s.substring(length+1);
    }
    
    private static String propertyNameForWrite(QtPropertyWriter r, Method m) {
        String name = r.name();        
        if (name.length() == 0) {
            name = m.getName();
            if (name.startsWith(("set"))) 
                name = stripAndLowercase(name, 3);
        }
        return name;
    }
    
    private static String propertyNameForReset(QtPropertyResetter r, Method m) {
        String name = r.name();        
        if (name.length() == 0) {
            name = m.getName();
            if (name.startsWith(("reset"))) 
                name = stripAndLowercase(name, 5);
        }
        return name;        
    }
    
    private static Method findMethod(Class<?> c, String name, Class ... args) {
        try { return c.getMethod(name, args); } catch (Exception e) { }
        return null;
    }  
    
    @SuppressWarnings("unchecked")
    private static boolean hasOtherAnnotations(Method m, Class expectedAnnotation) {
        for (Class c : QT_PROPERTY_ANNOTATION_CLASSES) {
            if (c != expectedAnnotation && m.isAnnotationPresent(c))
                return true;
        }
        
        return false;    
    }
    
    private static Method findReadMethodForProperty(Class cl, String name, Class type) 
        throws QMalformedQtPropertyException {
        
        // setX() / x() pair; Qt style
        Method getMethod = findMethod(cl, name);
        
        if (getMethod == null) {
            name = upcaseFirst(name);
            
            // setX() / getX(); Java style
            getMethod = findMethod(cl, "get" + name);

            // setX() / isX(); Qt and Java style for booleans
            if (getMethod == null && (type == Boolean.class || type == boolean.class)) {
                getMethod = findMethod(cl, "is" + name);
                if (getMethod == null)
                    getMethod = findMethod(cl, "has" + name);
            }
        }
        
        // Verify the type of the property...
        if (getMethod != null) {
            if (type != null && !type.equals(getMethod.getReturnType())) {
                throw new QMalformedQtPropertyException(name,
                        "Wrong type on property read method, expected: " + type, getMethod);
            }
            if (hasOtherAnnotations(getMethod, QtPropertyReader.class))
                return null;
            QtPropertyReader rp = getMethod.getAnnotation(QtPropertyReader.class);
            if (rp != null && !rp.name().equals(name))
                return null;
        }

        return getMethod;
    }
    
    
    
    private static Method findWriteMethodForProperty(Class cl, String name, Class type) 
        throws QMalformedQtPropertyException {  
        name = upcaseFirst(name);
        Method m = findMethod(cl, "set" + name, type);
        
        if (m != null) {
            // Verify that we don't have other annotation tags on this method.
            if (hasOtherAnnotations(m, QtPropertyWriter.class))
                return null;
        
            // Verify that the writer annotation matches this one...
            QtPropertyWriter wp = m.getAnnotation(QtPropertyWriter.class);
            if (wp != null && !wp.name().equals(name))
                return null;
            
            Class args[] = m.getParameterTypes();
            if (args.length != 1 || !args[0].equals(type))
                return null;
        }
        
        return m;
    }

    private static String upcaseFirst(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
    
    private static void checkDesignable(Entry e) {
        QtPropertyDesignable dp = e.read.getAnnotation(QtPropertyDesignable.class);
        if (dp != null) {
            String name = dp.value();
            if (name.equals("true"))
                e.designable = DEFAULT_TRUE;
            else if (name.equals("false"))
                e.designable = DEFAULT_FALSE;
            else {
                try {
                    Method m = e.read.getDeclaringClass().getMethod(name);
                    if (m.getReturnType() == boolean.class)
                        e.designable = m;
                } catch (Exception ex) { 
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private static int sortOrder(Method m) {
        QtPropertyOrder o = m.getAnnotation(QtPropertyOrder.class);
        return o != null ? o.value() : 0;
    }

    public static HashMap<String, Entry> findPropertiesRecursive(Class cl) throws QMalformedQtPropertyException {
        HashMap<String, Entry> map = findProperties(cl);

        cl = cl.getSuperclass();
        while (cl != null) {

            HashMap<String, Entry> more = findProperties(cl);

            for (Entry e : more.values()) {
                if (!map.containsKey(e.name))
                    map.put(e.name, e);
            }

            cl = cl.getSuperclass();
        }

        return map;
    }
    
    public static HashMap<String, Entry> findProperties(Class cl) throws QMalformedQtPropertyException {
        HashMap<String, Entry> entries = new HashMap<String, Entry>();
        Method methods[] = cl.getDeclaredMethods();
        findReadAnnotatedProperties(entries, methods);
        findWriteAnnotatedProperties(entries, methods);
        findResetAnnotatedProperties(entries, methods);
        findNamePatternProperties(entries, cl, methods);

        // Match them up...
        for (Entry e : entries.values()) {
            
            if (e.readable) {
                if (e.read == null)
                    e.read = findReadMethodForProperty(cl, e.name, e.type());
            } else {
                e.read = null;
            }
            
            if (e.read != null) {
                checkDesignable(e);
                e.sortOrder = sortOrder(e.read);
            }
            
            
            if (e.writable) {
                if (e.write == null)
                    e.write = findWriteMethodForProperty(cl, e.name, e.type());    
            } else {
                e.write = null;
            }
        }
        
//            Entry e = entries.get(s);
//            System.out.printf("entry: %s\n"
//                    + " - readable=%s\n"
//                    + " - writable=%s\n"
//                    + " - reset=%s\n"
//                    + " - designable=%s\n",
//                    e.name, e.read, e.write, e.reset, e.designable);
//        }        
//

        // Hardcode some classes for now...
        List<Entry> set = customSetForClass(cl);
        if (set != null) {
            for (Entry e : set) {
                entries.put(e.name, e);
            }
        }

        return entries;
    }

    private static void findResetAnnotatedProperties(HashMap<String, Entry> entries, Method[] methods) {
        for (Method method : methods) {
            QtPropertyResetter reset = method.getAnnotation(QtPropertyResetter.class);
            if (reset == null)
                continue;
            
            String propertyName = propertyNameForReset(reset, method);
            
            Entry e = entries.get(propertyName);
            if (e == null) {
                e = new Entry(propertyName);
                entries.put(propertyName, e);
            }
            
            e.reset = method;
        }
    }

    private static void findWriteAnnotatedProperties(HashMap<String, Entry> entries, Method[] methods) {
        for (Method method : methods) {
            QtPropertyWriter write = method.getAnnotation(QtPropertyWriter.class);
            if (write == null)
                continue;
            String propertyName = propertyNameForWrite(write, method);
            
            Entry e = entries.get(propertyName);
            if (e == null) {
                e = new Entry(propertyName);
                entries.put(propertyName, e);
            }
            
            e.writable = write.enabled();
            e.write = method;
        }
    }

    private static void findReadAnnotatedProperties(HashMap<String, Entry> entries, Method[] methods) throws QMalformedQtPropertyException {
        // Find all the annotated read methods
        for (Method method : methods) {
            QtPropertyReader read = method.getAnnotation(QtPropertyReader.class);
            if (read == null) 
                continue;
            String propertyName = propertyNameForRead(read, method);
            
            Entry e = entries.get(propertyName);
            if (e != null) 
                throw new QMalformedQtPropertyException(propertyName, "Duplicate property: " + e.read, method);
            
            e = new Entry(propertyName);
            e.readable = read.enabled();
            e.read = method;
            
            entries.put(propertyName, e);
        }
    }
    
    /**
     * Find and add all get/set pairs that match one of the following patterns.
     * 
     * T getX() <-> setX(T)
     * T x()    <-> setX(T)
     * 
     * and in addition if T is boolean:
     * 
     * T isX()  <-> setX(T)
     * T hasX() <-> setX(T)
     *
     *
     * @param entries The entryset to add to
     * @param cl The class in which to look
     * @param methods The methods for the class...
     */
    private static void findNamePatternProperties(HashMap<String, Entry> entries, Class cl, Method methods[]) {
        for (Method method : methods) {
            // Traditional set / [get|is|has] pair...
            String n = method.getName();
            if (n.startsWith("set")
                && method.getParameterTypes().length == 1
                && !method.getParameterTypes()[0].isArray()
                && n.length() > 3
                && Character.isUpperCase(n.charAt(3))) {
                String propertyName = Character.toLowerCase(n.charAt(3)) + n.substring(4);
                if (entries.containsKey(propertyName))
                    continue;

                // setX() / x() pair; Qt style
                Method getMethod = findMethod(cl, propertyName);

                // setX() / getX(); Java style
                if (getMethod == null)
                    getMethod = findMethod(cl, "get" + n.substring(3));

                // setX() / isX(); Qt and Java style for booleans
                if (getMethod == null
                    && (method.getParameterTypes()[0] == Boolean.class
                        || method.getParameterTypes()[0] == boolean.class)) {
                    getMethod = findMethod(cl, "is" + n.substring(3));
                    if (getMethod == null)
                        getMethod = findMethod(cl, "has" + n.substring(3));
                }

                if (getMethod != null) {
                    Entry e = new Entry(propertyName);
                    e.read = getMethod;
                    e.write = method;

                    e.readable = true;
                    e.writable = true;

                    entries.put(propertyName, e);
                }
            }
        }
    }

    private static List<Entry> customSetForClass(Class<?> cl) {
        try {
            if (cl == QTextEdit.class) {
                List<Entry> list = new ArrayList<Entry>();

                Entry html = new Entry("html");
                html.read = cl.getMethod("toHtml");
                html.write = cl.getMethod("setHtml", String.class);
                html.designable = DEFAULT_TRUE;

                list.add(html);

                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String args[]) throws Exception {
        if (args.length == 0) {
            System.out.println("Please specify class name");
            return;
        }

        HashMap<String, Entry> entries = findPropertiesRecursive(Class.forName(args[0]));

        for (Entry e : entries.values()) {

            System.out.print(e.read != null ? " R" : "  ");
            System.out.print(e.write != null ? " W" : "  ");
            System.out.print(e.reset != null ? " =" : "  ");
            System.out.print(e.isDesignable(null) ? " D" : "  ");

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
