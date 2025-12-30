package net.binis.codegen.map;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 - 2026 Binis Belev
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

import java.util.List;

public interface MapperFactory {

    Object DEFAULT = new Object();

    <T> T map(Object source, Class<T> destination);
    <T> T map(Object source, T destination);
    <T, K> T map(Object source, Class<T> destination, K key);
    <T, K> T map(Object source, T destination, K key);
    <T> T map(Object source, Class<T> destination, MappingStrategy strategy);
    <T> T map(Object source, T destination, MappingStrategy strategy);
    Mapping mapping(Class source, Class destination);
    Mapping mapping(Class source, Class destination, MappingStrategy strategy);
    Mapping mapping(Class source, Class destination, MappingStrategy strategy, Object key);
    <T> T convert(Object source, Class<T> destination);
    <T, K> T convert(Object source, Class<T> destination, K key);
    <T> T convert(Object source, Class<T> destination, Object... params);
    <T> T convert(Object source, T destination);
    <T, K> T convert(Object source, T destination, K key);
    <T> T convert(Object source, Class<T> destination, MappingStrategy strategy);
    <T, K> T convert(Object source, Class<T> destination, MappingStrategy strategy, K key);
    <T> T convert(Object source, Class<T> destination, MappingStrategy strategy, Object... params);
    <T> T convert(Object source, T destination, MappingStrategy strategy);
    <T, K> T convert(Object source, T destination, MappingStrategy strategy, K key);
    boolean canMap(Class<?> source, Class<?> destination);
    boolean canMapExactly(Class<?> source, Class<?> destination);
    <S, D> Mapping<S, D> getMap(Class<S> source, Class<D> destination);
    <S, D> Mapping<S, D> getExactMap(Class<S> source, Class<D> destination);
    <S, D, K> Mapping<S, D> getExactMap(Class<S> source, Class<D> destination, K key);
    void registerMapper(Mapping<?, ?> mapping);
    <K> void registerMapper(Mapping<?, ?> mapping, K key);
    <S, D> List<Mapping<S, D>> findMappings(Class<S> source, Class<D> destination);
    <S, D, K> List<Mapping<S, D>> findMappings(Class<S> source, Class<D> destination, K key);
    <S, D> Mapping<S, D> clearMapping(Class<S> source, Class<D> destination);
    void clearAllMappings();

}
