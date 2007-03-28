package com.trolltech.tools.designer;

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.tools.designer.propertysheet.*;

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

    private static List<Class<? extends Property>> CUSTOM_PROPERTIES = new ArrayList<Class<? extends Property>>();
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
        INVISIBLE_PROPERTIES.add("buddy");

        addCustomProperty(WidgetLayoutProperty.class);
        addCustomProperty(LayoutProperty.class);
        addCustomProperty(TabWidgetProperty.class);
        addCustomProperty(BuddyProperty.class);
    }

    public static void addCustomProperty(Class<? extends Property> handler) {
        CUSTOM_PROPERTIES.add(handler);
    }

    private PropertySheet(QObject object, QObject parent) {
        super(parent);
        this.object = object;
        build();
    }

    public static PropertySheet get(QObject object) {
        return propertySheets.get(object);
    }

    public static PropertySheet create(QObject object, QObject parent) {
        PropertySheet sheet = propertySheets.get(object);
        if (sheet == null) {
            sheet = new PropertySheet(object, parent);
            propertySheets.put(object, sheet);
        }
        return sheet;
    }

    @Override
    protected void disposed() {
        propertySheets.remove(object);
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
        if (index < 0)
            return true;
        Property p = properties.get(index);
        return p.entry.reset != null;
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
        if (index < 0)
            return false;
        return properties.get(index).attribute;
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
        if (hasReset(index)) {
            try {
                Property p = properties.get(index);
                Object o = invokationTarget(p);
                p.entry.reset.invoke(o);
                p.changed = false;
                return true;
            } catch (Exception e) {
                System.err.println("Resetting property failed: " + properties.get(index).entry.name + " for " + object);
                e.printStackTrace();
            }
        }
        return false;
    }

    public void setAttribute(int index, boolean attribute) {
        if (index < 0)
            return;
        Property p = properties.get(index);
        p.attribute = true;
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
            p.changed = true;
        } catch (Exception e) {
            System.out.println("Writing property failed: " + properties.get(index).entry.name + " for " + object + " with " + value);
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
        } catch (QPropertyException ex) {
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
        if (p.isPropertyInvokationTarget())
            return p;
        return object;
    }

    private void build() {
        TreeSet<Property> properties = new TreeSet<Property>();
        build(object.getClass(), 0, properties);

        this.properties = new ArrayList<Property>();
        this.properties.addAll(properties);

        for (Class<? extends Property> c : CUSTOM_PROPERTIES) {
            try {
                Method m = c.getMethod("initialize", List.class, QObject.class);
                m.invoke(null, this.properties, object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<Property> properties;
    private QObject object;

    private static HashMap<QObject, PropertySheet> propertySheets = new HashMap<QObject, PropertySheet>();
}
