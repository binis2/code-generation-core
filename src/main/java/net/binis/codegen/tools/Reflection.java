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
import java.lang.reflect.Modifier;
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
    public static void initialize(String cls, Object... params) {
        instantiate(loadClass(cls), params);
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

}
