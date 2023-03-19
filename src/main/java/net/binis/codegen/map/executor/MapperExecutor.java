package net.binis.codegen.map.executor;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 - 2023 Binis Belev
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

import lombok.extern.slf4j.Slf4j;
import net.binis.codegen.exception.MapperException;
import net.binis.codegen.factory.CodeFactory;
import net.binis.codegen.map.MapperFactory;
import net.binis.codegen.map.Mapping;
import net.binis.codegen.tools.Reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@SuppressWarnings("unchecked")
public class MapperExecutor<T> implements Mapping<Object, T> {

    protected BiFunction<Object, T, T> mapper;
    protected final Class<?> source;
    protected final Class<T> destination;
    protected final boolean convert;

    public MapperExecutor(Object source, T destination, boolean convert) {
        this.source = source.getClass();
        this.destination = (Class) destination.getClass();
        this.convert = convert;
        build();
    }

    public MapperExecutor(Class<?> source, Class<T> destination, boolean convert) {
        this.source = source;
        this.destination = destination;
        this.convert = convert;
        build();
    }

    @Override
    public Class getSource() {
        return source;
    }

    @Override
    public Class getDestination() {
        return destination;
    }

    public T map(Object source, T destination) {
        return mapper.apply(source, destination);
    }

    protected void build() {

        var accessors = new HashMap<String, TriFunction>();
        List<TriFunction> list;

        if (convert) {
            buildConverter(accessors);

            if (accessors.isEmpty()) {
                buildMatcher(accessors);
            }
        } else {
            buildMatcher(accessors);

            if (accessors.isEmpty()) {
                buildConverter(accessors);
            }
        }

        if (accessors.isEmpty()) {
            mapper = (s, d) -> d;
        } else {
            list = accessors.values().stream().toList();

            mapper = (s, d) -> {
                Object wither = null;
                Object result = d;
                for (var accessor : list) {
                    var res = accessor.apply(s, result, wither);
                    if (res instanceof WitherHolder holder) {
                        wither = holder.get();
                    } else {
                        result = res;
                    }
                }
                return (T) result;
            };
        }
    }

    private void buildMatcher(HashMap<String, TriFunction> accessors) {
        if (net.binis.codegen.modifier.Modifier.class.isAssignableFrom(destination)) {
            matchGettersModifier(accessors, source, destination);
        } else {
            matchGettersWithers(accessors, source, destination);
            matchGettersSetters(accessors, source, destination);
        }
    }

    private void buildConverter(HashMap<String, TriFunction> accessors) {
        List<Mapping> mappings = (List) CodeFactory.create(MapperFactory.class).findMappings(source, destination);

        if (!mappings.isEmpty()) {
            var first = mappings.get(0);
            if (first.getSource().isInterface()) {
                var list = new ArrayList<Mapping>();
                for (var item : mappings) {
                    list.add(item);
                    if (!item.getSource().isInterface()) {
                        break;
                    }
                }
                accessors.put(first.getSource().getCanonicalName(), (s, d, w) -> {
                    Object result = d;
                    for (var m : list) {
                        if (m instanceof ClassMapping c && c.isClass()) {
                            result = m.map(s, destination);
                        } else {
                            result = m.map(s, result);
                        }
                    }
                    return result;
                });

            } else {
                if (first instanceof ClassMapping c && c.isClass()) {
                    accessors.put(first.getSource().getCanonicalName(), (s, d, w) -> {
                        return first.map(s, destination);
                    });
                } else {
                    accessors.put(first.getSource().getCanonicalName(), (s, d, w) -> {
                        return first.map(s, d);
                    });
                }
            }
        }
    }

    protected void matchGettersSetters(Map<String, TriFunction> accessors, Class<?> source, Class<?> destination) {
        var getters = Arrays.stream(source.getMethods())
                .filter(Reflection::isGetter)
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .collect(Collectors.toMap(k -> getFieldName(k.getName()), v -> v));
        var setters = Arrays.stream(destination.getMethods())
                .filter(Reflection::isSetter)
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .collect(Collectors.toMap(k -> getFieldName(k.getName()), v -> v));

        if (!setters.isEmpty()) {
            for (var entry : getters.entrySet()) {
                if (!accessors.containsKey(entry.getKey())) {
                    var setter = setters.get(entry.getKey());
                    var getter = entry.getValue();
                    try {
                        getter.setAccessible(true);
                        if (nonNull(setter)) {
                            var name = entry.getKey();
                            var srcType = getter.getReturnType();
                            var destType = setter.getParameterTypes()[0];
                            if (destType.isAssignableFrom(srcType) || isNonNullableToNullable(srcType, destType)) {
                                addPlainGetterSetterMapping(accessors, destination, getter, setter, name);
                            } else if (isNullableToNonNullable(srcType, destType)) {
                                addNullProtectedGetterSetterMapping(accessors, destination, getter, setter, name);
                            } else {
                                addConverter(accessors, source, destination, getter, setter, name);
                            }
                        }
                    } catch (Exception e) {
                        log.info("Getter ({}) on {} is not accessible!", getter.getName(), source.getCanonicalName());
                    }
                }
            }
        }
    }

    protected void matchGettersWithers(Map<String, TriFunction> accessors, Class<?> source, Class<?> destination) {
        try {
            var wither = destination.getDeclaredMethod("with");
            wither.setAccessible(true);
            var witherAdded = false;

            var getters = Arrays.stream(source.getMethods())
                    .filter(Reflection::isGetter)
                    .filter(m -> Modifier.isPublic(m.getModifiers()))
                    .collect(Collectors.toMap(k -> getFieldName(k.getName()), v -> v));
            var withers = Arrays.stream(wither.getReturnType().getMethods())
                    .filter(m -> Modifier.isPublic(m.getModifiers()))
                    .filter(m -> m.getParameterCount() == 1)
                    .collect(Collectors.toMap(Method::getName, v -> v));

            if (!withers.isEmpty()) {
                for (var entry : getters.entrySet()) {
                    if (!accessors.containsKey(entry.getKey())) {
                        var setter = withers.get(entry.getKey());
                        var getter = entry.getValue();
                        try {
                            getter.setAccessible(true);
                            if (nonNull(setter)) {
                                var name = entry.getKey();
                                var srcType = getter.getReturnType();
                                var destType = setter.getParameterTypes()[0];
                                if (!witherAdded) {
                                    addWither(accessors, wither);
                                    witherAdded = true;
                                }
                                if (destType.isAssignableFrom(srcType) || isNonNullableToNullable(srcType, destType)) {
                                    addPlainGetterWitherMapping(accessors, destination, getter, setter, name);
                                } else if (isNullableToNonNullable(srcType, destType)) {
                                    addNullProtectedGetterWitherMapping(accessors, destination, getter, setter, name);
                                } else {
                                    addConverterWither(accessors, source, destination, getter, setter, name);
                                }
                            }
                        } catch (Exception e) {
                            log.info("Getter ({}) on {} is not accessible!", getter.getName(), source.getCanonicalName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            //Do nothing
        }
    }

    protected void matchGettersModifier(Map<String, TriFunction> accessors, Class<?> source, Class<?> destination) {
            var getters = Arrays.stream(source.getMethods())
                    .filter(Reflection::isGetter)
                    .filter(m -> Modifier.isPublic(m.getModifiers()))
                    .collect(Collectors.toMap(k -> getFieldName(k.getName()), v -> v));
            var withers = Arrays.stream(destination.getMethods())
                    .filter(m -> m.getParameterCount() == 1)
                    .filter(m -> m.getReturnType().isInterface())
                    .filter(m -> m.getReturnType().isAssignableFrom(destination))
                    .collect(Collectors.toMap(Method::getName, v -> v));

            if (!withers.isEmpty()) {
                for (var entry : getters.entrySet()) {
                    if (!accessors.containsKey(entry.getKey())) {
                        var setter = withers.get(entry.getKey());
                        var getter = entry.getValue();
                        try {
                            getter.setAccessible(true);
                            if (nonNull(setter)) {
                                var name = entry.getKey();
                                var srcType = getter.getReturnType();
                                var destType = setter.getParameterTypes()[0];
                                if (destType.isAssignableFrom(srcType) || isNonNullableToNullable(srcType, destType)) {
                                    addPlainGetterSetterMapping(accessors, destination, getter, setter, name);
                                } else if (isNullableToNonNullable(srcType, destType)) {
                                    addNullProtectedGetterSetterMapping(accessors, destination, getter, setter, name);
                                } else {
                                    addConverter(accessors, source, destination, getter, setter, name);
                                }
                            }
                        } catch (Exception e) {
                            log.info("Getter ({}) on {} is not accessible!", getter.getName(), source.getCanonicalName());
                        }
                    }
                }
            }
    }

    protected void addConverter(Map<String, TriFunction> accessors, Class<?> source, Class<?> destination, Method getter, Method setter, String name) {
        try {
            setter.setAccessible(true);
            var type = setter.getParameterTypes()[0];
            accessors.put(name, (s, d, w) -> {
                try {
                    var value = getter.invoke(s);
                    if (convert) {
                        setter.invoke(d, CodeFactory.create(MapperFactory.class).convert(value, type));
                    } else {
                        setter.invoke(d, CodeFactory.create(MapperFactory.class).map(value, type));
                    }
                    return d;
                } catch (Exception e) {
                    throw new MapperException("Unable to map value for field (" + name + ") for mapping (" + s.getClass().getCanonicalName() + "->" + d.getClass().getCanonicalName() + ")!", e);
                }
            });
        } catch (Exception e) {
            log.info("Setter ({}) on {} is not accessible!", setter.getName(), destination.getCanonicalName());
        }
    }

    protected void addConverterWither(Map<String, TriFunction> accessors, Class<?> source, Class<?> destination, Method getter, Method setter, String name) {
        try {
            setter.setAccessible(true);
            var type = setter.getParameterTypes()[0];
            accessors.put(name, (s, d, w) -> {
                try {
                    var value = getter.invoke(s);
                    if (convert) {
                        setter.invoke(w, CodeFactory.create(MapperFactory.class).convert(value, type));
                    } else {
                        setter.invoke(w, CodeFactory.create(MapperFactory.class).map(value, type));
                    }
                    return d;
                } catch (Exception e) {
                    throw new MapperException("Unable to map value for field (" + name + ") for mapping (" + s.getClass().getCanonicalName() + "->" + d.getClass().getCanonicalName() + ")!", e);
                }
            });
        } catch (Exception e) {
            log.info("Setter ({}) on {} is not accessible!", setter.getName(), destination.getCanonicalName());
        }
    }


    protected void addWither(Map<String, TriFunction> accessors, Method wither) {
        accessors.put("?!?wither?!?", (s, d, w) -> {
            try {
                return new WitherHolder(wither.invoke(d));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected boolean isNonNullableToNullable(Class<?> srcType, Class<?> destType) {
        if (srcType.isPrimitive()) {
            if (destType.equals(Double.class)) {
                return double.class.equals(srcType);
            } else if (destType.equals(Float.class)) {
                return float.class.equals(srcType);
            } else if (destType.equals(Long.class)) {
                return long.class.equals(srcType);
            } else if (destType.equals(Integer.class)) {
                return int.class.equals(srcType);
            } else if (destType.equals(Short.class)) {
                return short.class.equals(srcType);
            } else if (destType.equals(Character.class)) {
                return char.class.equals(srcType);
            } else if (destType.equals(Byte.class)) {
                return byte.class.equals(srcType);
            } else if (destType.equals(Boolean.class)) {
                return boolean.class.equals(srcType);
            }
        }
        return false;
    }

    protected boolean isNullableToNonNullable(Class<?> srcType, Class<?> destType) {
        if (destType.isPrimitive()) {
            if (srcType.equals(Double.class)) {
                return double.class.equals(destType);
            } else if (srcType.equals(Float.class)) {
                return float.class.equals(destType);
            } else if (srcType.equals(Long.class)) {
                return long.class.equals(destType);
            } else if (srcType.equals(Integer.class)) {
                return int.class.equals(destType);
            } else if (srcType.equals(Short.class)) {
                return short.class.equals(destType);
            } else if (srcType.equals(Character.class)) {
                return char.class.equals(destType);
            } else if (srcType.equals(Byte.class)) {
                return byte.class.equals(destType);
            } else if (srcType.equals(Boolean.class)) {
                return boolean.class.equals(destType);
            }
        }
        return false;
    }

    protected void addPlainGetterSetterMapping(Map<String, TriFunction> accessors, Class<?> destination, Method getter, Method setter, String name) {
        addGetterSetterMapping(accessors, destination, getter, setter, name, (v, d, s) -> {
            try {
                s.invoke(d, v);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected void addPlainGetterWitherMapping(Map<String, TriFunction> accessors, Class<?> destination, Method getter, Method setter, String name) {
        addGetterWitherMapping(accessors, destination, getter, setter, name, (v, d, s) -> {
            try {
                s.invoke(d, v);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected void addNullProtectedGetterSetterMapping(Map<String, TriFunction> accessors, Class<?> destination, Method getter, Method setter, String name) {
        addGetterSetterMapping(accessors, destination, getter, setter, name, (v, d, s) -> {
            try {
                if (nonNull(v)) {
                    s.invoke(d, v);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected void addNullProtectedGetterWitherMapping(Map<String, TriFunction> accessors, Class<?> destination, Method getter, Method setter, String name) {
        addGetterWitherMapping(accessors, destination, getter, setter, name, (v, d, s) -> {
            try {
                if (nonNull(v)) {
                    s.invoke(d, v);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected void addGetterSetterMapping(Map<String, TriFunction> accessors, Class<?> destination, Method getter, Method setter, String name, TriConsumer func) {
        try {
            setter.setAccessible(true);
            accessors.put(name, (s, d, w) -> {
                try {
                    var value = getter.invoke(s);
                    func.accept(value, d, setter);
                    return d;
                } catch (Exception e) {
                    throw new MapperException("Unable to map value for field (" + name + ") for mapping (" + s.getClass().getCanonicalName() + "->" + d.getClass().getCanonicalName() + ")!", e);
                }
            });
        } catch (Exception e) {
            log.info("Setter ({}) on {} is not accessible!", setter.getName(), destination.getCanonicalName());
        }
    }

    protected void addGetterWitherMapping(Map<String, TriFunction> accessors, Class<?> destination, Method getter, Method setter, String name, TriConsumer func) {
        try {
            setter.setAccessible(true);
            accessors.put(name, (s, d, w) -> {
                try {
                    var value = getter.invoke(s);
                    func.accept(value, w, setter);
                    return d;
                } catch (Exception e) {
                    throw new MapperException("Unable to map value for field (" + name + ") for mapping (" + s.getClass().getCanonicalName() + "->" + d.getClass().getCanonicalName() + ")!", e);
                }
            });
        } catch (Exception e) {
            log.info("Setter ({}) on {} is not accessible!", setter.getName(), destination.getCanonicalName());
        }
    }


    protected String getFieldName(String name) {
        var start = 3;
        if (name.charAt(0) == 'i') {
            start = 2;
        }

        var result = new StringBuilder(name.substring(start));
        result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
        return result.toString();
    }

    @FunctionalInterface
    protected interface TriConsumer {
        void accept(Object dest, Object value, Method setter);
    }

    protected interface TriFunction {
        Object apply(Object source, Object destination, Object wither);
    }

    protected static class WitherHolder {
        Object wither;

        public WitherHolder(Object wither) {
            this.wither = wither;
        }

        public Object get() {
            return wither;
        }
    }

}