package com.trolltech.tools.designer;

import java.lang.reflect.*;
import java.util.*;

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;

class NamedIntSet {
    public int value;
    public Map<String, Integer> names = new TreeMap<String, Integer>();
    public boolean isEnum;
}

public class PropertySheet {

    private static Set<String> INVISIBLE_PROPERTIES;
    static {
        INVISIBLE_PROPERTIES = new HashSet<String>();
        INVISIBLE_PROPERTIES.add("backgroundRole");
        INVISIBLE_PROPERTIES.add("focusProxy");
        INVISIBLE_PROPERTIES.add("foregroundRole");
        INVISIBLE_PROPERTIES.add("hidden");
        INVISIBLE_PROPERTIES.add("inputContext");
        INVISIBLE_PROPERTIES.add("layout");
        INVISIBLE_PROPERTIES.add("maximumHeight");
        INVISIBLE_PROPERTIES.add("maximumWidth");
        INVISIBLE_PROPERTIES.add("minimumHeight");
        INVISIBLE_PROPERTIES.add("minimumWidth");
        INVISIBLE_PROPERTIES.add("updatesEnabled");
        INVISIBLE_PROPERTIES.add("visible");
        INVISIBLE_PROPERTIES.add("windowFlags");
        INVISIBLE_PROPERTIES.add("windowIcon");
        INVISIBLE_PROPERTIES.add("windowIconText");
        INVISIBLE_PROPERTIES.add("windowModality");
        INVISIBLE_PROPERTIES.add("windowModified");
        INVISIBLE_PROPERTIES.add("windowOpacity");
        INVISIBLE_PROPERTIES.add("windowRole");
        INVISIBLE_PROPERTIES.add("windowState");
        INVISIBLE_PROPERTIES.add("windowTitle");
    }

    private static class Property implements Comparable {
        QtPropertyManager.Entry entry;

        String groupName;
        int subclassLevel;
        boolean changed;

        public int compareTo(Object arg0) {
            assert arg0 instanceof Property;
            Property p = (Property) arg0;

            if (subclassLevel > p.subclassLevel)
                return -1;
            else if (subclassLevel < p.subclassLevel)
                return 1;
            else {
                int order = entry.sortOrder - p.entry.sortOrder;
                if (order == 0)
                    return entry.name.compareTo(p.entry.name);
                return order;
            }
        }
        
        public String toString() {
            return "{" + groupName + ":" + entry.name + "}";            
        }
    }


    private PropertySheet(QObject object) {
        this.object = object;
        build();
    };

    public static PropertySheet createPropertySheet(QObject object) {        
        return new PropertySheet(object);
    }

    public int count() {
        return properties.size();
    }

    public boolean hasReset(int index) {
        return false;
    }

    public int indexOf(String name) {
        for (int i=0; i<properties.size(); ++i) {
            Property p = properties.get(i);
            if (p.entry.name.equals(name))
                return i;
        }
        return -1;
    }

    public boolean isAttribute(int index) {
        return false;
    }

    public boolean isChanged(int index) {
        return properties.get(index).changed;
    }

    public boolean isVisible(int index) {
        return properties.get(index).entry.isDesignable(object);
    }

    public Object property(int index) {
        try {
            Method getter = properties.get(index).entry.read;
            Object result = getter.invoke(object);

            if (QtEnumerator.class.isAssignableFrom(getter.getReturnType())
                    && Enum.class.isAssignableFrom(getter.getReturnType())) {
                return translateEnum((QtEnumerator) result);
            } else if (QFlags.class.isAssignableFrom(getter.getReturnType())) {
                return translateFlags((QFlags) result);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private final NamedIntSet createAndFillNamedIntSet(Class cl) {
        Object values[] = null;
        try {
            Method valuesMethod = cl.getMethod("values");
            values = (Object[]) valuesMethod.invoke(cl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NamedIntSet set = new NamedIntSet();
        set.isEnum = true;

        for (Object o : values) {
            set.names.put(((Enum) o).name(), ((QtEnumerator) o).value());
        }

        return set;
    }

    private Object translateFlags(QFlags result) {
        Class flagsClass = result.getClass();
        ParameterizedType superClass = (ParameterizedType) flagsClass.getGenericSuperclass();
        Class enumClass = (Class) superClass.getActualTypeArguments()[0];

        NamedIntSet set = createAndFillNamedIntSet(enumClass);
        set.value = result.value();
        set.isEnum = false;
        return set;
    }

    private Object translateEnum(QtEnumerator result) {
        NamedIntSet set = createAndFillNamedIntSet(result.getClass());
        set.value = result.value();
        set.isEnum = true;
        return set;
    }

    public String propertyGroup(int index) {
        return properties.get(index).groupName;
    }

    public String propertyName(int index) {
        return properties.get(index).entry.name;
    }

    public boolean reset(int index) {
        return false;
    }

    public void setAttribute(int index, boolean attribute) {

    }

    public void setChanged(int index, boolean changed) {
        if (index < 0)
            return;
        properties.get(index).changed = changed;
    }

    // @SuppressWarnings("all")
    public void setProperty(int index, Object value) {
        try {
            Method m = properties.get(index).entry.write;
            Class argClass = m.getParameterTypes()[0];
            if (QFlags.class.isAssignableFrom(argClass)) {
                Constructor c = argClass.getConstructor(int.class);
                value = c.newInstance(((Integer) value).intValue());
            } else if (Enum.class.isAssignableFrom(argClass)) {
                Method resolve = argClass.getMethod("resolve", int.class);
                value = resolve.invoke(argClass, ((Integer) value).intValue());
            }
            properties.get(index).entry.write.invoke(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPropertyGroup(int index, String group) {

    }

    public void setVisible(int index, boolean visible) {
        // ignore...
    }

    @SuppressWarnings("unused")
    private static Method findMethod(Class c, String name) {
        try {
            return c.getMethod(name);
        } catch (Exception e) { }
        return null;
    }

    private void build(Class cl, Set<String> names, int level, Collection<Property> properties) {
        if (cl == null)
            return;
        build(cl.getSuperclass(), names, level + 1, properties);

        String groupName = groupNameForClass(cl);
        try {
            Map<String, QtPropertyManager.Entry> entries = QtPropertyManager.findProperties(cl);
            for (QtPropertyManager.Entry e : entries.values()) {
                Property p = new Property();
                p.entry = e;
                p.groupName = groupName;
                p.subclassLevel = level;
                properties.add(p);
            }
        } catch (QMalformedQtPropertyException ex) {
            ex.printStackTrace();
        }
    }

    private static String groupNameForClass(Class cl) {
        String className = cl.getName();
        
        if (className.indexOf('.') < 0) {
            return className;
        } else {
            int index = className.lastIndexOf('.');
            String name = className.substring(index + 1);
            String groupName = className.substring(0, index);
            if (groupName.startsWith("com.trolltech.qt."))
                return name;
            return name + "; " + groupName; 
        }
    }

    private void build() {
        TreeSet<Property> properties = new TreeSet<Property>();
        build(object.getClass(), new HashSet<String>(), 0, properties);

        this.properties = new ArrayList<Property>();
        this.properties.addAll(properties);
    }

    private List<Property> properties;

    private QObject object;
}
