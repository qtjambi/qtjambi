package com.trolltech.tools.designer;

import com.trolltech.qt.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;

import java.lang.reflect.*;
import java.util.*;

class NamedIntSet {
    public int value;
    public Map<String, Integer> names = new TreeMap<String, Integer>();
    public boolean isEnum;

    public String toString() {
        String s = "{";
        for (Map.Entry<String, Integer> e : names.entrySet()) {
            s += e.getKey() + ":" + e.getValue() + ",";
        }
        return s + "; " + value + "}";
    }
}

public class PropertySheet extends JambiPropertySheet {

    private static Set<String> INVISIBLE_PROPERTIES;
    static {
        INVISIBLE_PROPERTIES = new HashSet<String>();
        INVISIBLE_PROPERTIES.add("backgroundRole");
        INVISIBLE_PROPERTIES.add("focusProxy");
        INVISIBLE_PROPERTIES.add("foregroundRole");
        INVISIBLE_PROPERTIES.add("hidden");
        INVISIBLE_PROPERTIES.add("inputContext");
        INVISIBLE_PROPERTIES.add("layout");
        INVISIBLE_PROPERTIES.add("windowFlags");
        INVISIBLE_PROPERTIES.add("windowIconText");
        INVISIBLE_PROPERTIES.add("windowModified");
        INVISIBLE_PROPERTIES.add("windowRole");
        INVISIBLE_PROPERTIES.add("windowState");
        INVISIBLE_PROPERTIES.add("parent");
        INVISIBLE_PROPERTIES.add("cornerWidget");
        INVISIBLE_PROPERTIES.add("menu");
        INVISIBLE_PROPERTIES.add("activeWindow");
        INVISIBLE_PROPERTIES.add("currentWidget");
        INVISIBLE_PROPERTIES.add("rootModelIndex");
        INVISIBLE_PROPERTIES.add("validator");
        INVISIBLE_PROPERTIES.add("actionGroup");
    }

    private static String LAYOUT_LEFT_MARGIN = "layoutLeftMargin";
    private static String LAYOUT_RIGHT_MARGIN = "layoutRightMargin";
    private static String LAYOUT_BOTTOM_MARGIN = "layoutBottomMargin";
    private static String LAYOUT_TOP_MARGIN = "layoutTopMargin";
    private static String LAYOUT_HORIZONTAL_SPACING = "layoutHorizontalSpacing";
    private static String LAYOUT_VERTICAL_SPACING = "layoutVerticalSpacing";

    private static class Property implements Comparable {
        QtPropertyManager.Entry entry;

        String groupName;
        int subclassLevel;
        boolean changed;
        boolean visible;

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

    public class LayoutProperty extends Property {
        public LayoutProperty(String name) {
            groupName = "Layout";
            subclassLevel = 1024; // Just an arbitrary high number...

            entry = new QtPropertyManager.Entry(name);
            try {
                entry.read = LayoutProperty.class.getMethod("read");
                entry.write = LayoutProperty.class.getMethod("write", int.class);
                entry.designable = LayoutProperty.class.getMethod("designable");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public int read() {
            if (!designable())
                return 0;
            QLayout layout = ((QWidget) object).layout();
            if (entry.name == LAYOUT_RIGHT_MARGIN) {
                QNativePointer np = new QNativePointer(QNativePointer.Type.Int);
                layout.getContentsMargins(null, null, np, null);
                return np.intValue();
            }
            if (entry.name == LAYOUT_LEFT_MARGIN) {
                QNativePointer np = new QNativePointer(QNativePointer.Type.Int);
                layout.getContentsMargins(np, null, null, null);
                return np.intValue();
            }
            if (entry.name == LAYOUT_BOTTOM_MARGIN) {
                QNativePointer np = new QNativePointer(QNativePointer.Type.Int);
                layout.getContentsMargins(null, null, null, np);
                return np.intValue();
            }
            if (entry.name == LAYOUT_TOP_MARGIN) {
                QNativePointer np = new QNativePointer(QNativePointer.Type.Int);
                layout.getContentsMargins(null, np, null, null);
                return np.intValue();
            }
            if (entry.name == LAYOUT_HORIZONTAL_SPACING) return ((QGridLayout) layout).horizontalSpacing();
            if (entry.name == LAYOUT_VERTICAL_SPACING) return ((QGridLayout) layout).verticalSpacing();
            return 0;
        }

        public void write(int x) {
            QLayout layout = ((QWidget) object).layout();
            if (entry.name.endsWith("Margin")) {
                QNativePointer left = new QNativePointer(QNativePointer.Type.Int);
                QNativePointer right = new QNativePointer(QNativePointer.Type.Int);
                QNativePointer top = new QNativePointer(QNativePointer.Type.Int);
                QNativePointer bottom = new QNativePointer(QNativePointer.Type.Int);

                layout.getContentsMargins(left, top, right, bottom);

                if (entry.name == LAYOUT_RIGHT_MARGIN) right.setIntValue(x);
                if (entry.name == LAYOUT_LEFT_MARGIN) left.setIntValue(x);
                if (entry.name == LAYOUT_TOP_MARGIN) top.setIntValue(x);
                if (entry.name == LAYOUT_BOTTOM_MARGIN) bottom.setIntValue(x);

                layout.setContentsMargins(left.intValue(), top.intValue(), right.intValue(), bottom.intValue());
            } else if (entry.name == LAYOUT_HORIZONTAL_SPACING) {
                ((QGridLayout) layout).setHorizontalSpacing(x);
            } else if (entry.name == LAYOUT_VERTICAL_SPACING) {
                ((QGridLayout) layout).setVerticalSpacing(x);
            }
        }

        public boolean designable() {
            if (object instanceof QWidget && ((QWidget) object).layout() != null) {
                QLayout l = ((QWidget) object).layout();
                return entry.name == LAYOUT_HORIZONTAL_SPACING
                    || entry.name == LAYOUT_VERTICAL_SPACING
                        ? l instanceof QGridLayout
                        : true;
            }
            return false;
        }
    }


    public PropertySheet(QObject object, QObject parent) {
        super(parent);
        this.object = object;
        build();


    }

    public boolean canAddDynamicProperty(String propertyName, Object value) {
        return false;
    }

    public boolean addDynamicProperty(String arg__1, Object arg__2) {
        return false;
    }

    public int count() {
        return properties.size();
    }

    public boolean dynamicPropertiesAllowed() {
        return false;
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
        if (index < 0)
            return false;
        return properties.get(index).changed;
    }

    public boolean isDynamicProperty(int arg__1) {
        return false;
    }

    public boolean isVisible(int index) {
        if (index < 0)
            return false;
        Property p = properties.get(index);
        return p.entry.isDesignable(invokationTarget(p)) && p.entry.write != null || p.visible;
    }

    public Object readProperty(int index) {
        if (index < 0)
            return null;
        try {
            Property p = properties.get(index);

            Object target = invokationTarget(p);

            Method getter = p.entry.read;
            Object result = getter.invoke(target);

            if (result == null)
                result = defaultConstruct(p.entry.type());

            if (QtEnumerator.class.isAssignableFrom(getter.getReturnType())
                    && Enum.class.isAssignableFrom(getter.getReturnType())) {
                result = translateEnum((QtEnumerator) result);
            } else if (QFlags.class.isAssignableFrom(getter.getReturnType())) {
                result = translateFlags((QFlags) result);
            }

            return result;
        } catch (Exception e) {
            System.err.printf("Failed to read property '%s' from '%s'\n",
                              propertyName(index),
                              object.getClass().getName());
            e.printStackTrace();
        }
        return null;
    }

    private Object defaultConstruct(Class c) {
        try {
            return c.newInstance();
        } catch (Exception e) {
        }
        return null;
    }

    public boolean removeDynamicProperty(String arg__1) {
        return false;
    }

    private NamedIntSet createAndFillNamedIntSet(Class<?> cl) {
        Object values[];
        try {
            Method valuesMethod = cl.getMethod("values");
            values = (Object[]) valuesMethod.invoke(cl);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        NamedIntSet set = new NamedIntSet();
        set.isEnum = true;

        for (Object o : values) {
            set.names.put(o.getClass().getName().replace('$', '.') + "." + ((Enum) o).name(), ((QtEnumerator) o).value());
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
        if (index < 0)
            return null;
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
    public void writeProperty(int index, Object value) {
        if (index < 0)
            return;
        try {
            Method m = properties.get(index).entry.write;
            Class<?> argClass = m.getParameterTypes()[0];
            if (QFlags.class.isAssignableFrom(argClass)) {
                Constructor c = argClass.getConstructor(int.class);
                value = c.newInstance((Integer) value);
            } else if (Enum.class.isAssignableFrom(argClass)) {
                Method resolve = argClass.getMethod("resolve", int.class);
                if (value == null)
                    throw new NullPointerException("Enum value cannot be null");
                value = resolve.invoke(null, (Integer) value);
            }
            Property p = properties.get(index);
            p.entry.write.invoke(invokationTarget(p), value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPropertyGroup(int index, String group) {
    }

    public void setVisible(int index, boolean visible) {
        if (index < 0)
            return;
        properties.get(index).visible = visible;
    }

    private void build(Class cl, int level, Collection<Property> properties) {
        if (cl == null)
            return;
        build(cl.getSuperclass(), level + 1, properties);

        String groupName = groupNameForClass(cl);
        try {
            Map<String, QtPropertyManager.Entry> entries = QtPropertyManager.findProperties(cl);
            for (QtPropertyManager.Entry e : entries.values()) {
                if (INVISIBLE_PROPERTIES.contains(e.name))
                    continue;
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

    private Object invokationTarget(Property p) {
        if (p instanceof LayoutProperty)
            return p;
        return object;
    }

    private void build() {
        Class cl = object.getClass();
        TreeSet<Property> properties = new TreeSet<Property>();
        build(object.getClass(), 0, properties);

        this.properties = new ArrayList<Property>();
        this.properties.addAll(properties);

        this.properties.add(new LayoutProperty("layoutLeftMargin"));
        this.properties.add(new LayoutProperty("layoutRightMargin"));
        this.properties.add(new LayoutProperty("layoutBottomMargin"));
        this.properties.add(new LayoutProperty("layoutTopMargin"));
        this.properties.add(new LayoutProperty("layoutHorizontalSpacing"));
        this.properties.add(new LayoutProperty("layoutVerticalSpacing"));
    }

    private List<Property> properties;

    private QObject object;
}
