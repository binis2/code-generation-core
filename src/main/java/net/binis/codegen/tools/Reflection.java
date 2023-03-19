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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
public abstract class Reflection {

    private static ClassLoader loader;

    private static Unsafe unsafe;

    public static Class<?> loadClass(String className) {
        try {
            return Objects.nonNull(loader) ? loader.loadClass(className) : Class.forName(className);
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
                    if ((isNull(params[i]) && types[i].isPrimitive()) || !types[i].isAssignableFrom(params[i].getClass())) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    if (!Modifier.isPublic(constructor.getModifiers())) {
                        constructor.setAccessible(true);
                    }
                    return constructor;
                }
            }
        }
        throw new UnsupportedOperationException("Unable to find proper constructor for class " + cls.getCanonicalName());
    }

    @SneakyThrows
    public static Class<?> initialize(String cls, Object... params) {
        return instantiate(loadClass(cls), params).getClass();
    }

    public static Field findField(Class<?> cls, String name) {
        try {
            return cls.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValueUnsafe(Object obj, String name) {
        try {
            if (isNull(unsafe)) {
                var f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                unsafe = (Unsafe) f.get(null);
            }

            var field = findField(obj.getClass(), name);
            return (T) unsafe.getObject(obj, unsafe.objectFieldOffset(field));
        } catch (Exception e) {
            log.error("Unable to get value for field {} of {}", name, obj.getClass().getName(), e);
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object obj, String name) {
        try {
            var field = findField(obj.getClass(), name);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (Exception e) {
            log.error("Unable to get value for field {} of {}", name, obj.getClass().getName(), e);
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

    public static boolean isWrapperType(Class<?> type) {
        return (type == Double.class || type == Float.class || type == Long.class ||
                type == Integer.class || type == Short.class || type == Character.class ||
                type == Byte.class || type == Boolean.class);
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

}
