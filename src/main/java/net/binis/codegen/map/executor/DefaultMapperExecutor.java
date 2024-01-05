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

import net.binis.codegen.factory.CodeFactory;
import net.binis.codegen.map.MapperFactory;
import net.binis.codegen.map.Mapping;
import net.binis.codegen.map.MappingStrategy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@SuppressWarnings("unchecked")
public class DefaultMapperExecutor implements MapperFactory {

    protected static final String DESTINATION_CANNOT_BE_NULL = "Destination cannot be null";
    protected final Map<String, Map<Object, Mapping>> mappers = new ConcurrentHashMap<>();

    @Override
    public <T> T map(Object source, Class<T> destination) {
        return mapClass(source, destination, MappingStrategy.GETTERS_SETTERS, DEFAULT);
    }

    @Override
    public <T> T map(Object source, T destination) {
        return map(source, destination, MappingStrategy.GETTERS_SETTERS);
    }

    @Override
    public <T, K> T map(Object source, Class<T> destination, K key) {
        return mapClass(source, destination, MappingStrategy.GETTERS_SETTERS, key);
    }

    @Override
    public <T, K> T map(Object source, T destination, K key) {
        Objects.requireNonNull(destination, DESTINATION_CANNOT_BE_NULL);
        return map(source, destination, (Class<T>) destination.getClass(), MappingStrategy.GETTERS_SETTERS, key);
    }

    @Override
    public <T> T map(Object source, Class<T> destination, MappingStrategy strategy) {
        return mapClass(source, destination, strategy, DEFAULT);
    }

    @Override
    public <T> T map(Object source, T destination, MappingStrategy strategy) {
        Objects.requireNonNull(destination, DESTINATION_CANNOT_BE_NULL);
        return (T) map(source, destination, (Class) destination.getClass(), strategy, DEFAULT);
    }

    protected <T, K> T map(Object source, T destination, Class<T> cls, MappingStrategy strategy, K key) {
        if (isNull(source)) {
            return handleNullSource(destination, cls);
        }
        var mapper = getMapper(source.getClass(), cls, key);
        if (isNull(mapper)) {
            mapper = buildMapper(source, destination, false, strategy, key);
        }
        return (T) mapper.map(source, destination);
    }

    protected <T, K> T mapClass(Object source, Class<T> cls, MappingStrategy strategy, K key) {
        Objects.requireNonNull(cls, DESTINATION_CANNOT_BE_NULL);
        if (isNull(source)) {
            return handleNullSource(null, cls);
        }
        var mapper = getMapper(source.getClass(), cls, key);
        if (isNull(mapper)) {
            mapper = buildMapperClass(source.getClass(), cls, false, true, strategy, key);
        }
        return (T) mapper.map(source, null);
    }

    @Override
    public Mapping mapping(Class source, Class destination) {
        return buildMapperClass(source, destination, false, false, MappingStrategy.GETTERS_SETTERS, DEFAULT);
    }

    @Override
    public Mapping mapping(Class source, Class destination, MappingStrategy strategy) {
        return buildMapperClass(source, destination, false, false, strategy, DEFAULT);
    }

    @Override
    public <T> T convert(Object source, Class<T> destination) {
        return convert(source, destination, MappingStrategy.GETTERS_SETTERS);
    }

    @Override
    public <T, K> T convert(Object source, Class<T> destination, K key) {
        return convert(source, destination, MappingStrategy.GETTERS_SETTERS, key);
    }

    @Override
    public <T> T convert(Object source, Class<T> destination, Object... params) {
        return convert(source, CodeFactory.create(destination, params), destination, MappingStrategy.GETTERS_SETTERS, DEFAULT);
    }

    @Override
    public <T> T convert(Object source, T destination) {
        return convert(source, destination, MappingStrategy.GETTERS_SETTERS);
    }

    @Override
    public <T, K> T convert(Object source, T destination, K key) {
        return convert(source, destination, MappingStrategy.GETTERS_SETTERS, key);
    }

    @Override
    public <T> T convert(Object source, Class<T> destination, MappingStrategy strategy) {
        if (isNull(source)) {
            return handleNullSource(null, destination);
        }
        return convert(source, CodeFactory.create(destination), destination, strategy, DEFAULT);
    }

    @Override
    public <T, K> T convert(Object source, Class<T> destination, MappingStrategy strategy, K key) {
        return convert(source, CodeFactory.create(destination), strategy, key);
    }

    @Override
    public <T> T convert(Object source, Class<T> destination, MappingStrategy strategy, Object... params) {
        return convert(source, CodeFactory.create(destination, params), destination, strategy, DEFAULT);
    }

    @Override
    public <T> T convert(Object source, T destination, MappingStrategy strategy) {
        return (T) convert(source, destination, strategy, DEFAULT);
    }

    @Override
    public <T, K> T convert(Object source, T destination, MappingStrategy strategy, K key) {
        Objects.requireNonNull(destination, DESTINATION_CANNOT_BE_NULL);
        if (isNull(source)) {
            return handleNullSource(destination, null);
        }
        var mapper = getMapper(source.getClass(), destination.getClass(), key);
        if (isNull(mapper)) {
            mapper = buildMapper(source, destination, true, strategy, key);
        }
        return (T) mapper.map(source, destination);
    }

    protected <T, K> T convert(Object source, T destination, Class<T> cls, MappingStrategy strategy, K key) {
        if (isNull(source)) {
            return handleNullSource(destination, cls);
        }
        var mapper = getMapper(source.getClass(), cls, key);
        if (isNull(mapper)) {
            mapper = buildMapperClass(source.getClass(), cls, true, true, strategy, key);
        }
        return (T) mapper.map(source, destination);
    }

    protected <T> T handleNullSource(T destination, Class<T> cls) {
        if (nonNull(destination)) {
            return destination;
        } else if (nonNull(cls) && cls.isPrimitive()) {
            return CodeFactory.create(cls);
        }
        return null;
    }

    @Override
    public boolean canMap(Class<?> source, Class<?> destination) {
        return nonNull(getMap(source, destination));
    }

    @Override
    public boolean canMapExactly(Class<?> source, Class<?> destination) {
        return nonNull(getExactMap(source, destination));
    }

    @Override
    public <S, D> Mapping<S, D> getMap(Class<S> source, Class<D> destination) {
        return findMapper(source, destination);
    }

    @Override
    public <S, D> Mapping<S, D> getExactMap(Class<S> source, Class<D> destination) {
        return (Mapping<S, D>) getMapper(source, destination, DEFAULT);
    }

    @Override
    public <S, D, K> Mapping<S, D> getExactMap(Class<S> source, Class<D> destination, K key) {
        return (Mapping<S, D>) getMapper(source, destination, key);
    }

    @Override
    public void registerMapper(Mapping<?, ?> mapping) {
        registerMapper(mapping, DEFAULT);
    }

    @Override
    public synchronized <K> void registerMapper(Mapping<?, ?> mapping, K key) {
        var name = calcMapperName(mapping.getSource(), mapping.getDestination());
        var existing = mappers.get(name);
        if (isNull(existing)) {
            existing = new ConcurrentHashMap<>();
        }
        existing.put(key, mapping);
        mappers.put(name, existing);
    }

    @Override
    public <S, D> List<Mapping<S, D>> findMappings(Class<S> source, Class<D> destination) {
        return findMappings(source, destination, DEFAULT);
    }

    @Override
    public <S, D, K> List<Mapping<S, D>> findMappings(Class<S> source, Class<D> destination, K key) {
        var result = new LinkedHashMap();
        findMappings(result, source, destination, key);
        if (result.isEmpty()) {
            findReverseMappings(result, source, destination, key);
        }
        if (result.isEmpty()) {
            findJoinMappings(result, source, destination, key);
        }
        return result.values().stream().toList();
    }


    @SuppressWarnings("unchecked")
    @Override
    public <S, D> Mapping<S, D> clearMapping(Class<S> source, Class<D> destination) {
        var map = mappers.remove(calcMapperName(source, destination));
        if (nonNull(map)) {
            return map.get(DEFAULT);
        }
        return null;
    }

    @Override
    public void clearAllMappings() {
        mappers.clear();
    }

    @SuppressWarnings("unchecked")
    protected void findMappings(Map<Class, Mapping> map, Class source, Class destination, Object key) {
        var m = mappers.get(calcMapperName(source, destination));
        var mapping = nonNull(m) ? m.get(key) : null;
        if (nonNull(mapping)) {
            map.putIfAbsent(source, mapping);
        } else {
            for (var intf : source.getInterfaces()) {
                findMappings(map, intf, destination, key);
            }

            var superClass = getSuperClass(source);
            if (nonNull(superClass)) {
                findMappings(map, superClass, destination, key);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <S> void findReverseMappings(Map<Class<S>, Mapping<S, ?>> map, Class<S> source, Class<?> destination, Object key) {
        var m = mappers.get(calcMapperName(source, destination));
        var mapping = nonNull(m) ? m.get(key) : null;
        if (nonNull(mapping)) {
            map.putIfAbsent(source, mapping);
        } else {
            for (var intf : destination.getInterfaces()) {
                findReverseMappings(map, source, intf, key);
            }

            var superClass = getSuperClass(destination);
            if (nonNull(superClass)) {
                findReverseMappings(map, source, superClass, key);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <S> void findJoinMappings(Map<Class, Mapping> map, Class<S> source, Class<?> destination, Object key) {
        var m = mappers.get(calcMapperName(source, destination));
        var mapping = nonNull(m) ? m.get(key) : null;
        if (nonNull(mapping)) {
            map.putIfAbsent(source, mapping);
        } else {
            for (var intf : destination.getInterfaces()) {
                for (var dIntf : destination.getInterfaces()) {
                    findMappings(map, intf, dIntf, key);
                    findReverseMappings((Map) map, intf, dIntf, key);
                }
            }

            var superClass = getSuperClass(source);
            while (nonNull(superClass)) {
                var dSuperClass = getSuperClass(destination);
                while (nonNull(dSuperClass)) {
                    findJoinMappings(map, superClass, dSuperClass, key);
                    dSuperClass = getSuperClass(dSuperClass);
                }
                superClass = getSuperClass(superClass);
            }
        }
    }


    protected <T> MapperExecutor buildMapper(Object source, T destination, boolean convert, MappingStrategy strategy, Object key) {
        return buildMapperClass(source.getClass(), destination.getClass(), convert, true, strategy, key);
    }

    @SuppressWarnings("unchecked")
    protected <T> MapperExecutor buildMapperClass(Class source, Class destination, boolean convert, boolean register, MappingStrategy strategy, Object key) {
        var result = new MapperExecutor(source, destination, convert, false, strategy, key);
        if (register) {
            var name = calcMapperName(source, destination);
            var existing = mappers.get(name);
            if (isNull(existing)) {
                existing = new ConcurrentHashMap<>();
            }
            existing.put(key, result);
            mappers.put(name, existing);
        }
        return result;
    }

    protected String calcMapperName(Class source, Class destination) {
        return source.getCanonicalName() + "->" + destination.getCanonicalName();
    }

    @SuppressWarnings("unchecked")
    protected <S, D> Mapping<S, D> findMapper(Class<S> source, Class<D> destination) {
        return (Mapping<S, D>) getMapper(source, destination, DEFAULT);
    }

    protected Class getSuperClass(Class<?> cls) {
        var result = cls.getSuperclass();
        if (isNull(result) && cls.isInterface()) {
            result = Object.class;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected <K> Mapping<Object, Object> getMapper(Class<?> source, Class<?> destination, K key) {
        var map = mappers.get(calcMapperName(source, destination));
        if (nonNull(map)) {
            return map.get(key);
        }
        return null;
    }

}
