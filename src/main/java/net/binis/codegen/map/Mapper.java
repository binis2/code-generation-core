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

import net.binis.codegen.factory.CodeFactory;
import net.binis.codegen.map.builder.SourceMappingBuilder;
import net.binis.codegen.map.executor.DefaultMapperExecutor;
import net.binis.codegen.map.executor.LambdaMapperExecutor;
import net.binis.codegen.map.executor.MappingBuilderExecutor;

import java.util.List;
import java.util.function.BiFunction;

public class Mapper {

    static {
        CodeFactory.registerType(MapperFactory.class, CodeFactory.singleton(new DefaultMapperExecutor()));
    }

    public static <T> T map(Object source, Class<T> destination) {
        return CodeFactory.create(MapperFactory.class).map(source, destination);
    }

    public static <T> T map(Object source, T destination) {
        return CodeFactory.create(MapperFactory.class).map(source, destination);
    }

    public static <T> T map(Object source, Class<T> destination, MappingStrategy strategy) {
        return CodeFactory.create(MapperFactory.class).map(source, destination, strategy);
    }

    public static <T> T map(Object source, T destination, MappingStrategy strategy) {
        return CodeFactory.create(MapperFactory.class).map(source, destination, strategy);
    }

    public static <T, K> T map(Object source, Class<T> destination, K key) {
        return CodeFactory.create(MapperFactory.class).map(source, destination, key);
    }

    public static <T, K> T map(Object source, T destination, K key) {
        return CodeFactory.create(MapperFactory.class).map(source, destination, key);
    }

    public static <T> T convert(Object source, Class<T> destination, MappingStrategy strategy) {
        return CodeFactory.create(MapperFactory.class).convert(source, destination, strategy);
    }

    public static <T> T convert(Object source, Class<T> destination, MappingStrategy strategy, Object... params) {
        return CodeFactory.create(MapperFactory.class).convert(source, destination, strategy, params);
    }

    public static <T, K> T convert(Object source, T destination, MappingStrategy strategy) {
        return CodeFactory.create(MapperFactory.class).convert(source, destination, strategy);
    }

    public static <T, K> T convert(Object source, Class<T> destination, K key) {
        return CodeFactory.create(MapperFactory.class).convert(source, destination, key);
    }

    public static <T, K> T convert(Object source, T destination, K key) {
        return CodeFactory.create(MapperFactory.class).convert(source, destination, key);
    }


    public static <T> T convert(Object source, Class<T> destination) {
        return CodeFactory.create(MapperFactory.class).convert(source, destination);
    }

    public static <T> T convert(Object source, Class<T> destination, Object... params) {
        return CodeFactory.create(MapperFactory.class).convert(source, destination, params);
    }

    public static <T> T convert(Object source, T destination) {
        return CodeFactory.create(MapperFactory.class).convert(source, destination);
    }


    public static void registerMapper(Mapping mapping) {
        CodeFactory.create(MapperFactory.class).registerMapper(mapping);
    }

    public static <S, D, K> void registerMapper(Class<S> source, Class<D> destination, MappingStrategy strategy, K key, BiFunction<S, D, D> func) {
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, destination, false, false, strategy, func), key);
    }

    public static <S, D, K> void registerMapperClass(Class<S> source, Class<D> destination, MappingStrategy strategy, K key, BiFunction<S, Class<D>, D> func) {
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, destination, true, false, strategy, func), key);
    }

    public static <S, D, K> void registerProducerMapper(Class<S> source, Class<D> destination, MappingStrategy strategy, K key, BiFunction<S, D, D> func) {
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, destination, false, true, strategy, func), key);
    }

    public static <S, D, K> void registerProducerMapperClass(Class<S> source, Class<D> destination, MappingStrategy strategy, K key, BiFunction<S, Class<D>, D> func) {
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, destination, true, true, strategy, func), key);
    }

    public static <S, D> void registerMapper(Class<S> source, Class<D> destination, MappingStrategy strategy, BiFunction<S, D, D> func) {
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, destination, false, false, strategy, func));
    }

    public static <S, D> void registerMapperClass(Class<S> source, Class<D> destination, MappingStrategy strategy, BiFunction<S, Class<D>, D> func) {
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, destination, true, false, strategy, func));
    }

    public static <S, D> void registerProducerMapper(Class<S> source, Class<D> destination, MappingStrategy strategy, BiFunction<S, D, D> func) {
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, destination, false, true, strategy, func));
    }

    public static <S, D> void registerProducerMapperClass(Class<S> source, Class<D> destination, MappingStrategy strategy, BiFunction<S, Class<D>, D> func) {
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, destination, true, true, strategy, func));
    }

    public static <S, D, K> void registerMapperKey(Class<S> source, Class<D> destination, K key, BiFunction<S, D, D> func) {
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, destination, false, false, MappingStrategy.GETTERS_SETTERS, func), key);
    }

    public static <S, D, K> void registerMapperClassKey(Class<S> source, Class<D> destination, K key, BiFunction<S, Class<D>, D> func) {
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, destination, true, false, MappingStrategy.GETTERS_SETTERS, func), key);
    }

    public static <S, D, K> void registerProducerMapperKey(Class<S> source, Class<D> destination, K key, BiFunction<S, D, D> func) {
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, destination, false, true, MappingStrategy.GETTERS_SETTERS, func), key);
    }

    public static <S, D, K> void registerProducerMapperClassKey(Class<S> source, Class<D> destination, K key, BiFunction<S, Class<D>, D> func) {
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, destination, true, true, MappingStrategy.GETTERS_SETTERS, func), key);
    }

    public static <S, D> void registerMapper(Class<S> source, Class<D> destination, BiFunction<S, D, D> func) {
        registerMapper(source, destination, MappingStrategy.GETTERS_SETTERS, func);
    }

    public static <S, D, K> void registerMapper(Class<S> source, Class<D> destination, K key, BiFunction<S, D, D> func) {
        registerMapper(source, destination, MappingStrategy.GETTERS_SETTERS, key, func);
    }

    public static <S, D> void registerMapperClass(Class<S> source, Class<D> destination, BiFunction<S, Class<D>, D> func) {
        registerMapperClass(source, destination, MappingStrategy.GETTERS_SETTERS, func);
    }

    public static <S, D> void registerProducerMapper(Class<S> source, Class<D> destination, BiFunction<S, D, D> func) {
        registerProducerMapper(source, destination, MappingStrategy.GETTERS_SETTERS, func);
    }

    public static <S, D> void registerProducerMapperClass(Class<S> source, Class<D> destination, BiFunction<S, Class<D>, D> func) {
        registerProducerMapperClass(source, destination, MappingStrategy.GETTERS_SETTERS, func);
    }

    public static <S, D> List<Mapping<S, D>> findMappings(Class<S> source, Class<D> destination) {
        return CodeFactory.create(MapperFactory.class).findMappings(source, destination);
    }

    public static SourceMappingBuilder map() {
        return new MappingBuilderExecutor();
    }

    private Mapper() {
        //Do nothing
    }

}
