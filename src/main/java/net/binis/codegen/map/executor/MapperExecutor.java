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
import net.binis.codegen.tools.Reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
public class MapperExecutor<T> {

    protected BiFunction<Object, T, T> mapper;

    public MapperExecutor(Object source, Object destination) {
        build(source.getClass(), destination.getClass());
    }

    public T map(Object source, T destination) {
        return mapper.apply(source, destination);
    }

    protected void build(Class<?> source, Class<?> destination) {

        var accessors = new HashMap<String, TriFunction>();
        if (net.binis.codegen.modifier.Modifier.class.isAssignableFrom(destination)) {
            matchGettersModifier(accessors, source, destination);
        } else {
            matchGettersWithers(accessors, source, destination);
            matchGettersSetters(accessors, source, destination);
        }

        var list = accessors.values().stream().toList();

        mapper = (s, d) -> {
            Object wither = null;
            for (var accessor : list) {
                wither = accessor.apply(s, d, wither);
            }
            return d;
        };

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
                            }
                            //TODO: Implement conversions
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
                                if (destType.isAssignableFrom(srcType) || isNonNullableToNullable(srcType, destType)) {
                                    if (!witherAdded) {
                                        addWither(accessors, wither);
                                        witherAdded = true;
                                    }
                                    addPlainGetterWitherMapping(accessors, destination, getter, setter, name);
                                } else if (isNullableToNonNullable(srcType, destType)) {
                                    if (!witherAdded) {
                                        addWither(accessors, wither);
                                        witherAdded = true;
                                    }
                                    addNullProtectedGetterWitherMapping(accessors, destination, getter, setter, name);
                                }
                                //TODO: Implement conversions
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
                                }
                                //TODO: Implement conversions
                            }
                        } catch (Exception e) {
                            log.info("Getter ({}) on {} is not accessible!", getter.getName(), source.getCanonicalName());
                        }
                    }
                }
            }
    }


    private void addWither(Map<String, TriFunction> accessors, Method wither) {
        accessors.put("?!?wither?!?", (s, d, w) -> {
            try {
                return wither.invoke(d);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private boolean isNonNullableToNullable(Class<?> srcType, Class<?> destType) {
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

    private boolean isNullableToNonNullable(Class<?> srcType, Class<?> destType) {
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
                    return null;
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
                    return w;
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
    private interface TriConsumer {
        void accept(Object dest, Object value, Method setter);
    }

    private interface TriFunction {
        Object apply(Object source, Object destination, Object wither);
    }

}
