package net.binis.codegen.config;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 - 2024 Binis Belev
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

import net.binis.codegen.annotation.CodeConfiguration;
import net.binis.codegen.exception.MapperException;
import net.binis.codegen.factory.CodeFactory;
import net.binis.codegen.map.Mapper;
import net.binis.codegen.map.MapperFactory;
import net.binis.codegen.map.executor.DefaultMapperExecutor;
import net.binis.codegen.objects.base.enumeration.CodeEnum;
import net.binis.codegen.tools.Reflection;
import net.binis.codegen.tools.TypeUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.nonNull;

@SuppressWarnings("unchecked")
@CodeConfiguration
public abstract class DefaultMappings {

    protected static Map<Class, Function<Object, Map>> objectToMapCache = new ConcurrentHashMap<>();

    public static void initialize() {
        CodeFactory.registerType(MapperFactory.class, CodeFactory.singleton(new DefaultMapperExecutor()));
        //Creation of primitive types and wrappers
        CodeFactory.registerType(int.class, () -> 0);
        CodeFactory.registerType(long.class, () -> 0L);
        CodeFactory.registerType(byte.class, () -> (byte) 0);
        CodeFactory.registerType(short.class, () -> (short) 0);
        CodeFactory.registerType(boolean.class, () -> false);
        CodeFactory.registerType(char.class, () -> (char) 0);
        CodeFactory.registerType(float.class, () -> (float) 0.0);
        CodeFactory.registerType(double.class, () -> 0.0);
        CodeFactory.registerType(Integer.class, () -> 0);
        CodeFactory.registerType(Long.class, () -> 0L);
        CodeFactory.registerType(Byte.class, () -> (byte) 0);
        CodeFactory.registerType(Short.class, () -> (short) 0);
        CodeFactory.registerType(Boolean.class, () -> false);
        CodeFactory.registerType(Character.class, () -> (char) 0);
        CodeFactory.registerType(Float.class, () -> (float) 0.0);
        CodeFactory.registerType(Double.class, () -> 0.0);
        CodeFactory.registerType(String.class, () -> "");
        //Default Collections
        CodeFactory.registerType(Map.class, () -> new HashMap<>());
        CodeFactory.registerType(Set.class, () -> new HashSet<>());
        CodeFactory.registerType(List.class, () -> new ArrayList<>());

        //Number conversions
        Mapper.registerProducerMapper(Number.class, int.class, (s, d) -> s.intValue());
        Mapper.registerProducerMapper(Number.class, Integer.class, (s, d) -> s.intValue());
        Mapper.registerProducerMapper(Number.class, long.class, (s, d) -> s.longValue());
        Mapper.registerProducerMapper(Number.class, Long.class, (s, d) -> s.longValue());
        Mapper.registerProducerMapper(Number.class, byte.class, (s, d) -> s.byteValue());
        Mapper.registerProducerMapper(Number.class, Byte.class, (s, d) -> s.byteValue());
        Mapper.registerProducerMapper(Number.class, short.class, (s, d) -> s.shortValue());
        Mapper.registerProducerMapper(Number.class, Short.class, (s, d) -> s.shortValue());
        Mapper.registerProducerMapper(Number.class, boolean.class, (s, d) -> s.intValue() != 0);
        Mapper.registerProducerMapper(Number.class, Boolean.class, (s, d) -> s.intValue() != 0);
        Mapper.registerProducerMapper(Number.class, char.class, (s, d) -> (char) s.intValue());
        Mapper.registerProducerMapper(Number.class, Character.class, (s, d) -> (char) s.intValue());
        Mapper.registerProducerMapper(Number.class, float.class, (s, d) -> s.floatValue());
        Mapper.registerProducerMapper(Number.class, Float.class, (s, d) -> s.floatValue());
        Mapper.registerProducerMapper(Number.class, double.class, (s, d) -> s.doubleValue());
        Mapper.registerProducerMapper(Number.class, Double.class, (s, d) -> s.doubleValue());
        //Boolean conversion
        Mapper.registerProducerMapper(Boolean.class, boolean.class, (s, d) -> s);
        Mapper.registerProducerMapper(boolean.class, Boolean.class, (s, d) -> s);
        Mapper.registerProducerMapper(Boolean.class, int.class, (s, d) -> s ? 1 : 0);
        Mapper.registerProducerMapper(Boolean.class, Integer.class, (s, d) -> s ? 1 : 0);
        Mapper.registerProducerMapper(Boolean.class, long.class, (s, d) -> s ? 1L : 0);
        Mapper.registerProducerMapper(Boolean.class, Long.class, (s, d) -> s ? 1L : 0);
        Mapper.registerProducerMapper(Boolean.class, byte.class, (s, d) -> s ? (byte) 1 : 0);
        Mapper.registerProducerMapper(Boolean.class, Byte.class, (s, d) -> s ? (byte) 1 : 0);
        Mapper.registerProducerMapper(Boolean.class, short.class, (s, d) -> s ? (short) 1 : 0);
        Mapper.registerProducerMapper(Boolean.class, Short.class, (s, d) -> s ? (short) 1 : 0);
        Mapper.registerProducerMapper(Boolean.class, char.class, (s, d) -> s ? (char) 1 : 0);
        Mapper.registerProducerMapper(Boolean.class, Character.class, (s, d) -> s ? (char) 1 : 0);
        Mapper.registerProducerMapper(Boolean.class, float.class, (s, d) -> s ? 1.0f : 0);
        Mapper.registerProducerMapper(Boolean.class, Float.class, (s, d) -> s ? 1.0f : 0);
        Mapper.registerProducerMapper(Boolean.class, double.class, (s, d) -> s ? 1.0 : 0);
        Mapper.registerProducerMapper(Boolean.class, Double.class, (s, d) -> s ? 1.0 : 0);
        //String conversion
        Mapper.registerProducerMapper(Object.class, String.class, (s, d) -> s.toString());
        //Enum conversion
        Mapper.registerMapperClass(String.class, Enum.class, (s, d) -> Enum.valueOf(d, s));
        Mapper.registerMapperClass(String.class, CodeEnum.class, (s, d) -> CodeFactory.enumValueOf(d, s));
        Mapper.registerMapperClass(Number.class, CodeEnum.class, (s, d) -> CodeFactory.enumValueOf(d, s.intValue()));
        //UUID
        Mapper.registerMapperClass(String.class, UUID.class, (s, d) -> UUID.fromString(s));
        //Collections
        Mapper.registerMapper(Collection.class, List.class, (s, d) -> {
            d.addAll(s);;
            return d;
        });
        Mapper.registerMapper(Collection.class, Set.class, (s, d) -> {
            d.addAll(s);;
            return d;
        });
        Mapper.registerMapper(Object.class, List.class, (s, d) -> {
            d.add(s);;
            return d;
        });
        Mapper.registerMapper(Object.class, Set.class, (s, d) -> {
            d.add(s);;
            return d;
        });
        Mapper.registerMapper(Map.class, Map.class, (s, d) -> {
            d.putAll(s);
            return d;
        });
        Mapper.registerMapper(int[].class, List.class, (s, d) -> {
            for (var item : s) {
                d.add(item);
            }
            return d;
        });
        Mapper.registerMapper(long[].class, List.class, (s, d) -> {
            for (var item : s) {
                d.add(item);
            }
            return d;
        });
        Mapper.registerMapper(byte[].class, List.class, (s, d) -> {
            for (var item : s) {
                d.add(item);
            }
            return d;
        });
        Mapper.registerMapper(int[].class, List.class, (s, d) -> {
            for (var item : s) {
                d.add(item);
            }
            return d;
        });
        Mapper.registerMapper(short[].class, List.class, (s, d) -> {
            for (var item : s) {
                d.add(item);
            }
            return d;
        });
        Mapper.registerMapper(boolean[].class, List.class, (s, d) -> {
            for (var item : s) {
                d.add(item);
            }
            return d;
        });
        Mapper.registerMapper(char[].class, List.class, (s, d) -> {
            for (var item : s) {
                d.add(item);
            }
            return d;
        });
        Mapper.registerMapper(double[].class, List.class, (s, d) -> {
            for (var item : s) {
                d.add(item);
            }
            return d;
        });
        Mapper.registerProducerMapper(Collection.class, int[].class, (s, d) -> {
            var result = new int[s.size()];
            var source = s.toArray();
            for (var i = 0; i < s.size(); i++) {
                result[i] = Mapper.convert(source[i], int.class);
            }
            return result;
        });
        Mapper.registerProducerMapper(Collection.class, long[].class, (s, d) -> {
            var result = new long[s.size()];
            var source = s.toArray();
            for (var i = 0; i < s.size(); i++) {
                result[i] = Mapper.convert(source[i], long.class);
            }
            return result;
        });
        Mapper.registerProducerMapper(Collection.class, byte[].class, (s, d) -> {
            var result = new byte[s.size()];
            var source = s.toArray();
            for (var i = 0; i < s.size(); i++) {
                result[i] = Mapper.convert(source[i], byte.class);
            }
            return result;
        });
        Mapper.registerProducerMapper(Collection.class, short[].class, (s, d) -> {
            var result = new short[s.size()];
            var source = s.toArray();
            for (var i = 0; i < s.size(); i++) {
                result[i] = Mapper.convert(source[i], short.class);
            }
            return result;
        });
        Mapper.registerProducerMapper(Collection.class, boolean[].class, (s, d) -> {
            var result = new boolean[s.size()];
            var source = s.toArray();
            for (var i = 0; i < s.size(); i++) {
                result[i] = Mapper.convert(source[i], boolean.class);
            }
            return result;
        });
        Mapper.registerProducerMapper(Collection.class, char[].class, (s, d) -> {
            var result = new char[s.size()];
            var source = s.toArray();
            for (var i = 0; i < s.size(); i++) {
                result[i] = Mapper.convert(source[i], char.class);
            }
            return result;
        });
        Mapper.registerProducerMapper(Collection.class, float[].class, (s, d) -> {
            var result = new float[s.size()];
            var source = s.toArray();
            for (var i = 0; i < s.size(); i++) {
                result[i] = Mapper.convert(source[i], float.class);
            }
            return result;
        });
        Mapper.registerProducerMapper(Collection.class, double[].class, (s, d) -> {
            var result = new double[s.size()];
            var source = s.toArray();
            for (var i = 0; i < s.size(); i++) {
                result[i] = Mapper.convert(source[i], double.class);
            }
            return result;
        });
        //Java Serialization
        Mapper.registerProducerMapperClass(byte[].class, Serializable.class, (s, d) -> {
            try (ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(s))) {
                return (Serializable) is.readObject();
            } catch (Exception e) {
                throw new MapperException(e);
            }
        });
        Mapper.registerProducerMapperClass(Serializable.class, byte[].class, (s, d) -> {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream os = new ObjectOutputStream(bos)) {
                os.writeObject(s);
                return bos.toByteArray();
            } catch (Exception e) {
                throw new MapperException(e);
            }
        });
        Mapper.registerProducerMapperClass(Object.class, Map.class, ObjectToMap());

        //Java Time
        Mapper.registerProducerMapperClass(String.class, OffsetDateTime.class, (s, d) -> {
            try {
                return OffsetDateTime.parse(s);
            } catch (Exception e) {
                return null;
            }
        });
        Mapper.registerProducerMapperClass(String.class, OffsetTime.class, (s, d) -> {
            try {
                return OffsetTime.parse(s);
            } catch (Exception e) {
                return null;
            }
        });
        Mapper.registerProducerMapperClass(String.class, LocalDateTime.class, (s, d) -> {
            try {
                return LocalDateTime.parse(s);
            } catch (Exception e) {
                return null;
            }
        });
        Mapper.registerProducerMapperClass(String.class, LocalDate.class, (s, d) -> {
            try {
                return LocalDate.parse(s);
            } catch (Exception e) {
                return null;
            }
        });
        Mapper.registerProducerMapperClass(String.class, LocalTime.class, (s, d) -> {
            try {
                return LocalTime.parse(s);
            } catch (Exception e) {
                return null;
            }
        });
    }

    protected static BiFunction<Object, Class<Map>, Map> ObjectToMap() {
        return (s, d) -> {
            if (s instanceof Map map) {
                return map;
            }
            return objectToMapCache.computeIfAbsent(s.getClass(), k -> {
                var methods = Reflection.findMethods(s.getClass(), m ->
                        !"getClass".equals(m.getName()) &&
                                m.getParameterCount() == 0 &&
                                !void.class.equals(m.getReturnType()) &&
                                (m.getModifiers() & Modifier.PUBLIC) != 0 &&
                                (m.getModifiers() & Modifier.STATIC) == 0 &&
                                (m.getName().startsWith("get") || m.getName().startsWith("is")))
                        .stream().collect(HashMap<String, Function<Object, Object>>::new, (m, v) -> {
                            var name = v.getName().substring(v.getName().startsWith("i") ? 2 : 3);
                            if (!name.isEmpty() && Character.isUpperCase(name.charAt(0))) {
                                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                                m.put(name, mapValue(v));
                            }
                        }, HashMap::putAll);
                return object ->
                        methods.entrySet().stream().collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue().apply(object)), HashMap::putAll);
            }).apply(s);
        };
    }

    protected static Function<Object, Object> mapValue(Method method) {
        var cls = method.getReturnType();
        if (cls.isPrimitive() || TypeUtils.isWrapperType(cls) || String.class.equals(cls) || Enum.class.isAssignableFrom(cls)) {
            return object ->
                Reflection.invoke(method, object);
        } else {
            return object -> {
                var value = Reflection.invoke(method, object);
                if (nonNull(value)) {
                    return Mapper.map(value, Map.class);
                }
                return null;
            };
        }
    }

    private DefaultMappings() {
        //Do nothing
    }

}

