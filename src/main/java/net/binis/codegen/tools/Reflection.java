package net.binis.codegen.tools;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 Binis Belev
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
public abstract class Reflection {

    private static ClassLoader loader;

    private static final Unsafe unsafe = getUnsafe();

    private static final Method declaredFields = findMethod("getDeclaredFields0", Class.class, boolean.class);

    private static final Long offset = getFirstFieldOffset();

    private Reflection() {
        //Do nothing
    }

    public static Class<?> loadClass(String className) {
        try {
            return Objects.nonNull(loader) ? loader.loadClass(className) : Class.forName(className);
        } catch (Throwable e) {
            return null;
        }
    }

    public static Class<?> loadClass(String className, ClassLoader loader) {
        try {
            return loader.loadClass(className);
        } catch (Throwable e) {
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static <T> T instantiate(Class<T> cls, Object... params) {
        return (T) findConstructor(cls, params).newInstance(params);
    }

    public static Constructor findConstructor(Class<?> cls, Object... params) {
        for (var constructor : cls.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == params.length) {
                var types = constructor.getParameterTypes();
                var match = true;
                for (var i = 0; i < params.length; i++) {
                    if ((isNull(params[i]) && types[i].isPrimitive()) || !compatible(types[i], params[i])) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    if (!Modifier.isPublic(constructor.getModifiers())) {
                        if (!constructor.trySetAccessible()) {
                            setAccessible(constructor);
                        }
                    }
                    return constructor;
                }
            }
        }
        throw new UnsupportedOperationException("Unable to find proper constructor for class " + cls.getCanonicalName());
    }

    public static boolean compatible(Class<?> type, Object obj) {
        if (nonNull(obj)) {
            var aClass = obj instanceof Class<?> c ? c : obj.getClass();
            if (type.isPrimitive()) {
                return type.equals(TypeUtils.getPrimitiveType(aClass));
            } else {
                return type.isAssignableFrom(aClass);
            }
        } else {
            return !type.isPrimitive();
        }
    }

    @SneakyThrows
    public static Class<?> initialize(String cls, Object... params) {
        return instantiate(loadClass(cls), params).getClass();
    }

    public static Field findField(Class<?> cls, String name) {
        try {
            return cls.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            try {
                return cls.getField(name);
            } catch (NoSuchFieldException ex) {
                var fields = (Field[]) invoke(declaredFields, cls, false);
                for (var field : fields) {
                    if (field.getName().equals(name)) {
                        return field;
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValueUnsafe(Object obj, String name) {
        try {
            var field = findField(obj.getClass(), name);
            return (T) unsafe.getObject(obj, unsafe.objectFieldOffset(field));
        } catch (Exception e) {
            log.error("Unable to get value for field {} of {}", name, obj.getClass().getName(), e);
            return null;
        }
    }

    public static <T> T getFieldValue(Object obj, String name) {
        return getFieldValue(obj.getClass(), obj, name);
    }

    public static void setFieldValue(Class cls, Object obj, String name, Object value) {
        try {
            var field = findField(cls, name);
            if (!field.trySetAccessible()) {
                setAccessible(field);
            }
            field.set(obj, value);
        } catch (Exception e) {
            log.error("Unable to set value for field {} of {}", name, obj.getClass().getName(), e);
        }
    }

    public static void setFieldValue(Object obj, String name, Object value) {
        setFieldValue(obj.getClass(), obj, name, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Class cls, Object obj, String name) {
        try {
            var field = findField(cls, name);
            if (!field.trySetAccessible()) {
                setAccessible(field);
            }
            return (T) field.get(obj);
        } catch (Exception e) {
            log.error("Unable to get value for field {} of {}", name, obj.getClass().getName(), e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getStaticFieldValue(Class cls, String name) {
        try {
            var field = findField(cls, name);
            if (!field.trySetAccessible()) {
                setAccessible(field);
            }
            return (T) field.get(null);
        } catch (Exception e) {
            log.error("Unable to get value for field {} of {}", name, cls.getName(), e);
            return null;
        }
    }


    public static void withLoader(ClassLoader loader, Runnable task) {
        try {
            Reflection.loader = loader;
            task.run();
        } finally {
            Reflection.loader = null;
        }
    }

    public static boolean isGetter(Method method) {
        var name = method.getName();
        return !method.getReturnType().equals(void.class) && method.getParameterCount() == 0 &&
                (((name.startsWith("get") && name.length() > 3) && (Character.isUpperCase(name.charAt(3)))) ||
                        ((name.startsWith("is") && name.length() > 2) && (Character.isUpperCase(name.charAt(2)))));
    }

    public static boolean isSetter(Method method) {
        var name = method.getName();
        return method.getReturnType().equals(void.class) && method.getParameterCount() == 1 &&
                (name.startsWith("set") && name.length() > 3) && (Character.isUpperCase(name.charAt(3)));
    }

    @SuppressWarnings("unchecked")
    public static Method findMethod(String name, Class cls, Class... params) {
        Method result = null;
        try {
            result = cls.getDeclaredMethod(name, params);
        } catch (Exception e) {
            try {
                result = cls.getMethod(name, params);
            } catch (Exception ex) {
                //Do nothing
            }
        }
        return result;
    }

    public static Object invoke(Method m, Object instance, Object... args) {
        try {
            if (!m.trySetAccessible()) {
                setAccessible(m);
            }
            return m.invoke(instance, args);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object invoke(String name, Object instance, Object... args) {
        try {
            var m = findMethod(name, instance.getClass(), Arrays.stream(args).map(Object::getClass).toArray(Class[]::new));
            if (nonNull(m)) {
                return invoke(m, instance, args);
            }
        } catch (Exception e) {
            //Do nothing
        }
        return null;
    }

    public static Object invokeStatic(Method m, Object... args) {
        try {
            return m.invoke(null, args);
        } catch (Exception e) {
            return null;
        }
    }

    @SneakyThrows
    public static Object invokeStaticWithException(Method m, Object... args) {
        try {
            return m.invoke(null, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw e;
        }
    }

    public static Object invokeStatic(String name, Class cls, Object... args) {
        try {
            return findMethod(name, cls, Arrays.stream(args).map(Object::getClass).toArray(Class[]::new)).invoke(null, args);
        } catch (Exception e) {
            return null;
        }
    }

    public static Unsafe getUnsafe() {
        try {
            var theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.trySetAccessible();
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setAccessible(Member m) {
        try {
            unsafe.putBooleanVolatile(m, offset, true);
        } catch (Exception e) {
        }
    }

    private static long getFirstFieldOffset() {
        try {
            return unsafe.objectFieldOffset(Parent.class.getDeclaredField("first"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    static class Parent {
        boolean first;

        private Parent() {
        }
    }

}
