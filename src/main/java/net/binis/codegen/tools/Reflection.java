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

import java.lang.reflect.Field;

@Slf4j
public class Reflection {

    private Reflection() {

    }

    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @SneakyThrows
    public static <T> T instantiate(Class<T> cls) {
        return cls.getDeclaredConstructor().newInstance();
    }

    public static Field findField(Class<?> cls, String name) {
        try {
            return cls.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
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

}
