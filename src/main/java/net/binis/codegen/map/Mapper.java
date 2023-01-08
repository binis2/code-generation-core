package net.binis.codegen.map;

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
import net.binis.codegen.map.executor.DefaultMapperExecutor;
import net.binis.codegen.map.executor.LambdaMapperExecutor;

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

    public static <T> T convert(Object source, Class<T> destination) {
        return CodeFactory.create(MapperFactory.class).convert(source, destination);
    }

    public static <T> T convert(Object source, T destination) {
        return CodeFactory.create(MapperFactory.class).convert(source, destination);
    }

    public static void registerMapper(Class<?> source, Class<?> destination, Mapping mapping) {
        CodeFactory.create(MapperFactory.class).registerMapper(source, destination, mapping);
    }

    public static <S, D> void registerMapper(Class<S> source, Class<D> destination, BiFunction<S, D, D> func) {
        CodeFactory.create(MapperFactory.class).registerMapper(source, destination, new LambdaMapperExecutor(source, destination, func));
    }

    public static <D> List<Mapping<?, D>> findMappings(Class<?> source, Class<D> destination) {
        return CodeFactory.create(MapperFactory.class).findMappings(source, destination);
    }

    private Mapper() {
        //Do nothing
    }

}
