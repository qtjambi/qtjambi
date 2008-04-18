package com.trolltech.qt.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import com.trolltech.qt.QFlags;
import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.QtBlockedEnum;
import com.trolltech.qt.QtBlockedSlot;
import com.trolltech.qt.QtEnumerator;
import com.trolltech.qt.QtJambiInternal;
import com.trolltech.qt.QtPropertyDesignable;
import com.trolltech.qt.QtPropertyReader;
import com.trolltech.qt.QtPropertyResetter;
import com.trolltech.qt.QtPropertyUser;
import com.trolltech.qt.QtPropertyWriter;
import com.trolltech.qt.QtJambiInternal.ResolvedSignal;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.Qt;


/**
 * Methods to help construct the fake meta object.
 */
public class MetaObjectTools {

    private static class Container {
        private enum AnnotationType {
            Reader,
            Writer,
            Resetter
        }

        private Method method;
        private String name = null;
        private boolean enabled;
        private AnnotationType type;


        private Container(String name, Method method, boolean enabled, AnnotationType type) {
            this.name = name;
            this.method = method;
            this.enabled = enabled;
            this.type = type;
        }

        private Container(QtPropertyReader reader, Method method) {
            this(reader.name(), method, reader.enabled(), AnnotationType.Reader);
        }

        private Container(QtPropertyWriter writer, Method method) {
            this(writer.name(), method, writer.enabled(), AnnotationType.Writer);
        }

        private Container(QtPropertyResetter resetter, Method method) {
            this(resetter.name(), method, resetter.enabled(), AnnotationType.Resetter);
        }

        private static String removeAndLowercaseFirst(String name, int count) {
            return Character.toLowerCase(name.charAt(count)) + name.substring(count + 1);
        }

        private String getNameFromMethod(Method method) {
            if (type == AnnotationType.Resetter) {
                return "";
            } else if (type == AnnotationType.Reader) {
                String name = method.getName();
                if (name.startsWith("get"))
                    name = removeAndLowercaseFirst(name, 3);
                else if (isBoolean(method.getReturnType()) && name.startsWith("is"))
                    name = removeAndLowercaseFirst(name, 2);
                else if (isBoolean(method.getReturnType()) && name.startsWith("has"))
                    name = removeAndLowercaseFirst(name, 3);

                return name;
            } else { // starts with "set"
                String name = method.getName();
                if (!name.startsWith("set")) {
                    throw new IllegalArgumentException("The correct pattern for setter accessor names is setXxx where Xxx is the property name with upper case initial.");
                }

                name = removeAndLowercaseFirst(name, 3);
                return name;
            }
        }

        private String name() {
            if (name == null || name.length() == 0)
                name = getNameFromMethod(method);

            return name;
        }

        private boolean enabled() {
            return enabled;
        }

        private static Container readerAnnotation(Method method) {
            QtPropertyReader reader = method.getAnnotation(QtPropertyReader.class);
            return reader == null ? null : new Container(reader, method);
        }

        private static Container writerAnnotation(Method method) {
            QtPropertyWriter writer = method.getAnnotation(QtPropertyWriter.class);
            return writer == null ? null : new Container(writer, method);
        }

        private static Container resetterAnnotation(Method method) {
            QtPropertyResetter resetter = method.getAnnotation(QtPropertyResetter.class);
            return resetter == null ? null : new Container(resetter, method);
        }

    }

    private static class MetaData {
        public int metaData[];
        public byte stringData[];

        public Field signalsArray[];
        public Method slotsArray[];

        public Method propertyReadersArray[];
        public Method propertyWritersArray[];
        public Method propertyResettersArray[];
        public Method propertyDesignablesArray[];
        public Class<?> extraDataArray[];

        public String originalSignatures[];
    }

    private final static int MethodAccessPrivate                    = 0x0;
    private final static int MethodAccessProtected                  = 0x1;
    private final static int MethodAccessPublic                     = 0x2;
    private final static int MethodSignal                           = 0x4;
    private final static int MethodSlot                             = 0x8;

    private final static int PropertyReadable                       = 0x00000001;
    private final static int PropertyWritable                       = 0x00000002;
    private final static int PropertyResettable                     = 0x00000004;
    private final static int PropertyEnumOrFlag                     = 0x00000008;
    private final static int PropertyDesignable                     = 0x00001000;
    private final static int PropertyResolveDesignable              = 0x00002000;
    private final static int PropertyStored                         = 0x00010000;
    private final static int PropertyUser                           = 0x00100000;

    private static Method notBogus(Method method, String propertyName, Class<?> paramType) {
        if (method == null)
            return null;

        Container reader = Container.readerAnnotation(method);
        if (reader != null
            && (!reader.name().equals(propertyName)
                || !reader.enabled()
                || !method.getReturnType().isAssignableFrom(paramType))) {
            return null;
        } else {
            return method;
        }
    }

    private static int queryEnums(Class<?> clazz, Hashtable<String, Class<?>> enums) {
        int enumConstantCount = 0;

        Class<?> declaredClasses[] = clazz.getDeclaredClasses();
        for (Class<?> declaredClass : declaredClasses)
            enumConstantCount += putEnumTypeInHash(declaredClass, enums);

        return enumConstantCount;
    }

    private static Class<?> getEnumForQFlags(Class<?> flagsType) {
        Type t = flagsType.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type typeArguments[] = ((ParameterizedType)t).getActualTypeArguments();
            return ((Class<?>) typeArguments[0]);
        }

        return null;
    }

    private static int putEnumTypeInHash(Class<?> type, Hashtable<String, Class<?>> enums) {
        Class<?> flagsType = QFlags.class.isAssignableFrom(type) ? type : null;
        Class<?> enumType = type.isEnum() ? type : null;
        if (enumType == null && flagsType != null) {
            enumType = getEnumForQFlags(flagsType);
        }

        if (enumType == null)
            return 0;

        // Since Qt supports enums that are not part of the meta object
        // we need to check whether the enum can actually be used in
        // a property.
        Class<?> enclosingClass = enumType.getEnclosingClass();
        if (   enclosingClass != null
            && ((!QObject.class.isAssignableFrom(enclosingClass) && !Qt.class.equals(enclosingClass))
               || enumType.isAnnotationPresent(QtBlockedEnum.class))) {
            return -1;
        }

        int enumConstantCount = 0;
        if (!enums.contains(enumType.getName())) {
            enums.put(enumType.getName(), enumType);

            enumConstantCount = enumType.getEnumConstants().length;
        }

        if (flagsType != null && !enums.contains(flagsType.getName()))
            enums.put(flagsType.getName(), flagsType);

        return enumConstantCount;
    }

    private static Object isDesignable(Method declaredMethod, Class<?> clazz) {
        QtPropertyDesignable designable = declaredMethod.getAnnotation(QtPropertyDesignable.class);

        if (designable != null) {
            String value = designable.value();

            if (value.equals("true")) {
                return Boolean.TRUE;
            } else if (value.equals("false")) {
                return Boolean.FALSE;
            } else try {
                Method m = clazz.getMethod(value, (Class<?>[]) null);
                if (isBoolean(m.getReturnType()))
                    return m;
                else
                    throw new RuntimeException("Wrong return type of designable method '" + m.getName() + "'");
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return Boolean.TRUE;
    }

    private static boolean isValidSetter(Method declaredMethod) {
        return (declaredMethod.getParameterTypes().length == 1
                && declaredMethod.getReturnType() == Void.TYPE
                && !internalTypeName(methodParameters(declaredMethod), 1).equals(""));
    }

    private static Method getMethod(Class<?> clazz, String name, Class<?> args[]) {
        try {
            return clazz.getMethod(name, args);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static String capitalizeFirst(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private static boolean isBoolean(Class<?> type) {
        return (type == Boolean.class || type == Boolean.TYPE);
    }

    private static Boolean isUser(Method m) {
        return (m.getAnnotation(QtPropertyUser.class) != null);
    }


    private static boolean isValidGetter(Method method) {
        return (method.getParameterTypes().length == 0
                && method.getReturnType() != Void.TYPE);
    }


    public static String bunchOfClassNamesInARow(Class<?> classes[]) {
        return bunchOfClassNamesInARow(classes, null);
    }

    public static String bunchOfClassNamesInARow(Class<?> classes[], int arrayDimensions[]) {
        String classNames = "";

        for (int i=0; i<classes.length; ++i) {
            Class<?> clazz = classes[i];
            if (classNames.length() > 0)
                classNames += ",";

            String className = clazz.getName();
            if (arrayDimensions != null) {
                for (int j=0; j<arrayDimensions[i]; ++j)
                    className = "java.lang.Object";
            }

            classNames += className;
        }

        return classNames;
    }

    private static String methodParameters(Method m) {
        return bunchOfClassNamesInARow(m.getParameterTypes());
    }

    /**
     * Returns the signature of the method m excluding the modifiers and the
     * return type.
     */
    private static String methodSignature(Method m, boolean includeReturnType) {
        return (includeReturnType ? m.getReturnType().getName() + " " : "")
               + m.getName() + "(" + methodParameters(m) + ")";
    }

    public static String methodSignature(Method m) {
        return methodSignature(m, false);
    }


    private static int addString(int metaData[],
                                 Hashtable<String, Integer> strings,
                                 List<String> stringsInOrder,
                                 String string, int offset, int metaDataOffset) {

                if (strings.containsKey(string)) {
                    metaData[metaDataOffset] = strings.get(string);
                    return 0;
                }

                metaData[metaDataOffset] = offset;
                strings.put(string, offset);
                stringsInOrder.add(string);
                return string.length() + 1;
    }

    public native static void emitNativeSignal(QObject object, String signalSignature, String signalCppSignature, Object args[]);

    public static String cppSignalSignature(QSignalEmitter signalEmitter, String signalName) {
        QSignalEmitter.AbstractSignal signal = QtJambiInternal.lookupSignal(signalEmitter, signalName);
        if (signal != null)
            return cppSignalSignature(signal);
        else
            return "";
    }

    public static String cppSignalSignature(QSignalEmitter.AbstractSignal signal) {
        String signalParameters = QtJambiInternal.signalParameters(signal);
        String params = MetaObjectTools.internalTypeName(signalParameters, 1);
        if (signalParameters.length() > 0 && params.length() == 0)
            return "";
        else
            return signal.name() + "(" + params + ")";
    }

    private static String signalParameters(ResolvedSignal resolvedSignal) {
        return MetaObjectTools.bunchOfClassNamesInARow(resolvedSignal.types, resolvedSignal.arrayDimensions);
    }

    private static String signalParameters(Field field, Class<?> declaringClass) {
        QtJambiInternal.ResolvedSignal resolvedSignal = QtJambiInternal.resolveSignal(field, declaringClass);
        return signalParameters(resolvedSignal);
    }


    public native static String internalTypeName(String s, int varContext);

    private static MetaData buildMetaData(Class<? extends QObject> clazz) {
        MetaData metaData = new MetaData();

        List<Method> slots = new ArrayList<Method>();

        Hashtable<String, Method> propertyReaders = new Hashtable<String, Method>();
        Hashtable<String, Method> propertyWriters = new Hashtable<String, Method>();
        Hashtable<String, Object> propertyDesignables = new Hashtable<String, Object>();
        Hashtable<String, Method> propertyResetters = new Hashtable<String, Method>();
        Hashtable<String, Boolean> propertyUser = new Hashtable<String, Boolean>();

        // First we get all enums actually declared in the class
        Hashtable<String, Class<?>> enums = new Hashtable<String, Class<?>>();
        int enumConstantCount = queryEnums(clazz, enums);
        int enumCount = enums.size(); // Get the size before we add external enums

        Method declaredMethods[] = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {

            if (!declaredMethod.isAnnotationPresent(QtBlockedSlot.class)
                    && ((declaredMethod.getModifiers() & Modifier.STATIC) != Modifier.STATIC)) {

                // If we can't convert the type, we don't list it
                String methodParameters = methodParameters(declaredMethod);
                String returnType = declaredMethod.getReturnType().getName();
                if ((methodParameters.equals("") || !internalTypeName(methodParameters, 1).equals(""))
                    &&(returnType.equals("") || returnType.equals("void") || !internalTypeName(returnType, 0).equals(""))) {
                    slots.add(declaredMethod);
                }
            }

            // Rules for readers:
            // 1. Zero arguments
            // 2. Return something other than void
            // 3. We can convert the type
            Container reader = Container.readerAnnotation(declaredMethod);
            {

                if (reader != null
                    && reader.enabled()
                    && isValidGetter(declaredMethod)
                    && !internalTypeName(declaredMethod.getReturnType().getName(), 0).equals("")) {

                    // If the return type of the property reader is not registered, then
                    // we need to register the owner class in the meta object (in which case
                    // it has to be a QObject)
                    Class<?> returnType = declaredMethod.getReturnType();

                    int count = putEnumTypeInHash(returnType, enums);
                    if (count < 0) {
                        System.err.println("Problem in property '" + reader.name() + "' in '" + clazz.getName()
                                           + "': Only enum types 1. declared inside QObject subclasses or the "
                                           + "Qt interface and 2. declared without the QtBlockedEnum annotation "
                                           + "are supported for properties");
                        continue;
                    }

                    propertyReaders.put(reader.name(), declaredMethod);
                    propertyDesignables.put(reader.name(), isDesignable(declaredMethod, clazz));
                    propertyUser.put(reader.name(), isUser(declaredMethod));
                }
            }

            // Rules for writers:
            // 1. Takes exactly one argument
            // 2. Return void
            // 3. We can convert the type
            Container writer = Container.writerAnnotation(declaredMethod);
            {

                if (writer != null
                    && writer.enabled()
                    && isValidSetter(declaredMethod)) {
                    propertyWriters.put(writer.name(), declaredMethod);
                }
            }

            // Rules for resetters:
            // 1. No arguments
            // 2. Return void
            {
                Container resetter = Container.resetterAnnotation(declaredMethod);

                if (resetter != null
                    && declaredMethod.getParameterTypes().length == 0
                    && declaredMethod.getReturnType() == Void.TYPE) {
                    propertyResetters.put(resetter.name(), declaredMethod);
                }
            }

            // Check naming convention by looking for setXxx patterns, but only if it hasn't already been
            // annotated as a writer
            if (writer == null
                && reader == null // reader can't be a writer, cause the signature doesn't match, just an optimization
                && declaredMethod.getName().startsWith("set")
                && Character.isUpperCase(declaredMethod.getName().charAt(3))
                && isValidSetter(declaredMethod)) {

                Class<?> paramType = declaredMethod.getParameterTypes()[0];
                String propertyName = Character.toLowerCase(declaredMethod.getName().charAt(3))
                                    + declaredMethod.getName().substring(4);

                if (!propertyReaders.containsKey(propertyName)) {
                    // We need a reader as well, and the reader must not be annotated as disabled
                    // The reader can be called 'xxx', 'getXxx', 'isXxx' or 'hasXxx'
                    // (just booleans for the last two)
                    Method readerMethod = notBogus(getMethod(clazz, propertyName, null), propertyName, paramType);
                    if (readerMethod == null)
                        readerMethod = notBogus(getMethod(clazz, "get" + capitalizeFirst(propertyName), null), propertyName, paramType);
                    if (readerMethod == null && isBoolean(paramType))
                        readerMethod = notBogus(getMethod(clazz, "is" + capitalizeFirst(propertyName), null), propertyName, paramType);
                    if (readerMethod == null && isBoolean(paramType))
                        readerMethod = notBogus(getMethod(clazz, "has" + capitalizeFirst(propertyName), null), propertyName, paramType);

                    if (readerMethod != null) { // yay
                        reader = Container.readerAnnotation(readerMethod);
                        if (reader == null) {
                            propertyReaders.put(propertyName, readerMethod);
                            propertyWriters.put(propertyName, declaredMethod);

                            propertyDesignables.put(propertyName, isDesignable(readerMethod, clazz));
                            propertyUser.put(propertyName, isUser(readerMethod));
                        }
                    }
                }
            }
        }

        Field declaredFields[] = clazz.getDeclaredFields();
        List<Field> signalFields = new ArrayList<Field>();
        List<QtJambiInternal.ResolvedSignal> resolvedSignals = new ArrayList<QtJambiInternal.ResolvedSignal>();
        for (Field declaredField : declaredFields) {
            if (QtJambiInternal.isSignal(declaredField.getType())) {
                // If we can't convert all the types we don't list the signal
                QtJambiInternal.ResolvedSignal resolvedSignal = QtJambiInternal.resolveSignal(declaredField, declaredField.getDeclaringClass());
                String signalParameters = signalParameters(resolvedSignal);
                if (signalParameters.length() == 0 || internalTypeName(signalParameters, 1).length() != 0) {
                    signalFields.add(declaredField);
                    resolvedSignals.add(resolvedSignal);
                }

            }
        }
        metaData.signalsArray = signalFields.toArray(new Field[0]);

        {
            int functionCount = slots.size() + signalFields.size();
            int propertyCount = propertyReaders.keySet().size();

            metaData.metaData = new int[12 + functionCount * 5 + 1 + propertyCount * 3 + enumCount * 4 + enumConstantCount * 2];

            // Add static header
            metaData.metaData[0] = 1; // Revision
            // metaData[1] = 0 // class name  (ints default to 0)

            metaData.metaData[2] = 1;  // class info count
            metaData.metaData[3] = 10; // class info offset

            // Functions always start at offset 12 (header has size 10, class info size 2)
            metaData.metaData[4] = functionCount;
            metaData.metaData[5] = functionCount > 0 ? 12 : 0;

            metaData.metaData[6] = propertyCount;
            metaData.metaData[7] = 12 + functionCount * 5; // Each function takes 5 ints

            // Enums
            metaData.metaData[8] = enumCount;
            metaData.metaData[9] = 12 + functionCount * 5 + propertyCount * 3; // Each property takes 3 ints

            int offset = 0;
            int metaDataOffset = 10; // Header is always 10 ints long
            Hashtable<String, Integer> strings = new Hashtable<String, Integer>();
            List<String> stringsInOrder = new ArrayList<String>();
            // Class name
            {
                String className = clazz.getName().replace(".", "::");
                stringsInOrder.add(className);
                strings.put(clazz.getName(), offset); offset += className.length() + 1;
            }

            // Class info
            {
                offset += addString(metaData.metaData, strings, stringsInOrder, "__qt__binding_shell_language", offset, metaDataOffset++);
                offset += addString(metaData.metaData, strings, stringsInOrder, "Qt Jambi", offset, metaDataOffset++);
            }

            metaData.originalSignatures = new String[signalFields.size() + slots.size()];

            // Signals (### make sure enum types are covered)
            for (int i=0; i<signalFields.size(); ++i) {
                Field signalField = signalFields.get(i);
                QtJambiInternal.ResolvedSignal resolvedSignal = resolvedSignals.get(i);

                String javaSignalParameters = signalParameters(resolvedSignal);
                metaData.originalSignatures[i] = resolvedSignal.name
                    + (javaSignalParameters.length() > 0 ? '<' + javaSignalParameters + '>' : "");

                String signalParameters = internalTypeName(javaSignalParameters, 1);

                // Signal name
                offset += addString(metaData.metaData, strings, stringsInOrder, resolvedSignal.name + "(" + signalParameters + ")", offset, metaDataOffset++);

                // Signal parameters
                offset += addString(metaData.metaData, strings, stringsInOrder, signalParameters, offset, metaDataOffset++);

                // Signal type (signals are always void in Qt Jambi)
                offset += addString(metaData.metaData, strings, stringsInOrder, "", offset, metaDataOffset++);

                // Signal tag (Currently not supported by the moc either)
                offset += addString(metaData.metaData, strings, stringsInOrder, "", offset, metaDataOffset++);

                // Signal flags
                int flags = MethodSignal;
                int modifiers = signalField.getModifiers();
                if ((modifiers & Modifier.PRIVATE) == Modifier.PRIVATE)
                    flags |= MethodAccessPrivate;
                else if ((modifiers & Modifier.PROTECTED) == Modifier.PROTECTED)
                    flags |= MethodAccessProtected;
                else if ((modifiers & Modifier.PUBLIC) == Modifier.PUBLIC)
                    flags |= MethodAccessPublic;
                metaData.metaData[metaDataOffset++] = flags;
            }

            // Slots (### make sure enum types are covered, ### also test QFlags)
            for (int i=0; i<slots.size(); ++i) {
                Method slot = slots.get(i);

                String javaMethodSignature = methodSignature(slot);
                metaData.originalSignatures[signalFields.size() + i] = javaMethodSignature;

                // Slot signature
                offset += addString(metaData.metaData, strings, stringsInOrder, internalTypeName(javaMethodSignature, 1), offset, metaDataOffset++);

                // Slot parameters
                offset += addString(metaData.metaData, strings, stringsInOrder, internalTypeName(methodParameters(slot), 1), offset, metaDataOffset++);

                // Slot type
                String returnType = slot.getReturnType().getName();
                if (returnType.equals("void")) returnType = "";
                offset += addString(metaData.metaData, strings, stringsInOrder, internalTypeName(returnType, 0), offset, metaDataOffset++);

                // Slot tag (Currently not supported by the moc either)
                offset += addString(metaData.metaData, strings, stringsInOrder, "", offset, metaDataOffset++);

                // Slot flags
                int flags = MethodSlot;
                int modifiers = slot.getModifiers();
                if ((modifiers & Modifier.PRIVATE) == Modifier.PRIVATE)
                    flags |= MethodAccessPrivate;
                else if ((modifiers & Modifier.PROTECTED) == Modifier.PROTECTED)
                    flags |= MethodAccessProtected;
                else if ((modifiers & Modifier.PUBLIC) == Modifier.PUBLIC)
                    flags |= MethodAccessPublic;

                metaData.metaData[metaDataOffset++] = flags;
            }
            metaData.slotsArray = slots.toArray(new Method[0]);

            String propertyNames[] = propertyReaders.keySet().toArray(new String[0]);
            metaData.propertyReadersArray = new Method[propertyNames.length];
            metaData.propertyResettersArray = new Method[propertyNames.length];
            metaData.propertyWritersArray = new Method[propertyNames.length];
            metaData.propertyDesignablesArray = new Method[propertyNames.length];
            for (int i=0; i<propertyNames.length; ++i) {
                Method reader = propertyReaders.get(propertyNames[i]);
                Method writer = propertyWriters.get(propertyNames[i]);
                Method resetter = propertyResetters.get(propertyNames[i]);
                Object designableVariant = propertyDesignables.get(propertyNames[i]);
                boolean isUser = propertyUser.get(propertyNames[i]);

                if (writer != null && !reader.getReturnType().isAssignableFrom(writer.getParameterTypes()[0])) {
                    System.err.println("QtJambiInternal.buildMetaData: Writer for property "
                            + propertyNames[i] + " takes a type which is incompatible with reader's return type.");
                    writer = null;
                }

                // Name
                offset += addString(metaData.metaData, strings, stringsInOrder, propertyNames[i], offset, metaDataOffset++);

                // Type (need to special case flags and enums)
                Class<?> t = reader.getReturnType();
                boolean isEnumOrFlags = Enum.class.isAssignableFrom(t) || QFlags.class.isAssignableFrom(t);

                String typeName = null;
                if (isEnumOrFlags && t.getDeclaringClass() != null && QObject.class.isAssignableFrom(t.getDeclaringClass())) {
                    // To avoid using JObjectWrapper for enums and flags (which is required in certain cases.)
                    typeName = t.getDeclaringClass().getName().replaceAll("\\.", "::") + "::" + t.getSimpleName();
                } else {
                    typeName = internalTypeName(t.getName(), 0);
                }
                offset += addString(metaData.metaData, strings, stringsInOrder, typeName, offset, metaDataOffset++);

                int designableFlags = 0;
                if (designableVariant instanceof Boolean) {
                    if ((Boolean) designableVariant) designableFlags = PropertyDesignable;
                } else if (designableVariant instanceof Method) {
                    designableFlags = PropertyResolveDesignable;
                    metaData.propertyDesignablesArray[i] = (Method) designableVariant;
                }

                // Flags
                metaData.metaData[metaDataOffset++] = PropertyReadable | PropertyStored | designableFlags
                    | (writer != null ? PropertyWritable : 0)
                    | (resetter != null ? PropertyResettable : 0)
                    | (isEnumOrFlags ? PropertyEnumOrFlag : 0)
                    | (isUser ? PropertyUser : 0);

                metaData.propertyReadersArray[i] = reader;
                metaData.propertyWritersArray[i] = writer;
                metaData.propertyResettersArray[i] = resetter;
            }

            // Enum types
            int enumConstantOffset = metaDataOffset + enumCount * 4;

            Hashtable<String, Class<?>> enclosingClasses = new Hashtable<String, Class<?>>();
            Collection<Class<?>> classes = enums.values();
            for (Class<?> cls : classes) {
                Class<?> enclosingClass = cls.getEnclosingClass();
                if (enclosingClass.equals(clazz)) {
                    // Name
                    offset += addString(metaData.metaData, strings, stringsInOrder, cls.getSimpleName(), offset, metaDataOffset++);

                    // Flags (1 == flags, 0 == no flags)
                    metaData.metaData[metaDataOffset++] = QFlags.class.isAssignableFrom(cls) ? 0x1 : 0x0;

                    // Get the enum class
                    Class<?> enumClass = Enum.class.isAssignableFrom(cls) ? cls : null;
                    if (enumClass == null) {
                        enumClass = getEnumForQFlags(cls);
                    }

                    enumConstantCount = enumClass.getEnumConstants().length;

                    // Count
                    metaData.metaData[metaDataOffset++] = enumConstantCount;

                    // Data
                    metaData.metaData[metaDataOffset++] = enumConstantOffset;

                    enumConstantOffset += 2 * enumConstantCount;
                } else if (!enclosingClass.isAssignableFrom(clazz) && !enclosingClasses.contains(enclosingClass.getName())) {
                    // If the enclosing class of an enum is not in the current class hierarchy, then
                    // the generated meta object needs to have a pointer to its meta object in the
                    // extra-data.
                    enclosingClasses.put(enclosingClass.getName(), enclosingClass);
                }
            }
            metaData.extraDataArray = enclosingClasses.values().toArray(new Class<?>[0]);

            // Enum constants
            for (Class<?> cls : classes) {

                if (cls.getEnclosingClass().equals(clazz)) {
                    // Get the enum class
                    Class<?> enumClass = Enum.class.isAssignableFrom(cls) ? cls : null;
                    if (enumClass == null) {
                        enumClass = getEnumForQFlags(cls);
                    }

                    Enum<?> enumConstants[] = (Enum[]) enumClass.getEnumConstants();

                    for (Enum<?> enumConstant : enumConstants) {
                        // Key
                        offset += addString(metaData.metaData, strings, stringsInOrder, enumConstant.name(), offset, metaDataOffset++);

                        // Value
                        metaData.metaData[metaDataOffset++] = enumConstant instanceof QtEnumerator
                                                              ? ((QtEnumerator) enumConstant).value()
                                                              : enumConstant.ordinal();
                    }
                }
            }

            // EOD
            metaData.metaData[metaDataOffset++] = 0;

            int stringDataOffset = 0;
            metaData.stringData = new byte[offset + 1];

            for (String s : stringsInOrder) {
                assert stringDataOffset == strings.get(s);
                System.arraycopy(s.getBytes(), 0, metaData.stringData, stringDataOffset, s.length());
                stringDataOffset += s.length();
                metaData.stringData[stringDataOffset++] = 0;
            }



        }

        return metaData;
    }

}
