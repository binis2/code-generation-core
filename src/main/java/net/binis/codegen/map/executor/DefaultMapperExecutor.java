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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class DefaultMapperExecutor implements MapperFactory {

    protected final Map<String, Mapping> mappers = new ConcurrentHashMap<>();

    @Override
    public <T> T map(Object source, Class<T> destination) {
        return map(source, CodeFactory.create(destination));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T map(Object source, T destination) {
        var mapper = mappers.get(calcMapperName(source.getClass(), destination.getClass()));
        if (isNull(mapper)) {
            mapper = buildMapper(source, destination, false);
        }
        return (T) mapper.map(source, destination);
    }

    @Override
    public <T> T convert(Object source, Class<T> destination) {
        return convert(source, CodeFactory.create(destination));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T convert(Object source, T destination) {
        var mapper = mappers.get(calcMapperName(source.getClass(), destination.getClass()));
        if (isNull(mapper)) {
            mapper = buildMapper(source, destination, true);
        }
        return (T) mapper.map(source, destination);
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

    @SuppressWarnings("unchecked")
    @Override
    public <S, D> Mapping<S, D> getExactMap(Class<S> source, Class<D> destination) {
        return mappers.get(calcMapperName(source, destination));
    }

    @Override
    public void registerMapper(Class<?> source, Class<?> destination, Mapping<?, ?> mapping) {
        mappers.put(calcMapperName(source, destination), mapping);
    }

    @Override
    public <D> List<Mapping<?, D>> findMappings(Class<?> source, Class<D> destination) {
        var result = new LinkedHashMap<Class<?>, Mapping<?, D>>();
        findMappings(result, source, destination);
        return result.values().stream().toList();
    }

    @SuppressWarnings("unchecked")
    protected <D> void findMappings(Map<Class<?>, Mapping<?, D>> map, Class<?> source, Class<D> destination) {
        var mapping = mappers.get(calcMapperName(source, destination));
        if (nonNull(mapping)) {
            map.putIfAbsent(source, mapping);
        } else {
            for (var intf : source.getInterfaces()) {
                findMappings(map, intf, destination);
            }

            if (nonNull(source.getSuperclass())) {
                findMappings(map, source.getSuperclass(), destination);
            }
        }
    }

    protected <T> MapperExecutor buildMapper(Object source, T destination, boolean convert) {
        var result = new MapperExecutor(source, destination, convert);
        mappers.put(calcMapperName(source.getClass(), destination.getClass()), result);
        return result;
    }

    protected String calcMapperName(Class source, Class destination) {
        return source.getCanonicalName() + "->" + destination.getCanonicalName();
    }

    @SuppressWarnings("unchecked")
    protected <S, D> Mapping<S,D> findMapper(Class<S> source, Class<D> destination) {
        return mappers.get(calcMapperName(source, destination));
    }


}
