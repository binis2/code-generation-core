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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

public class DefaultMapperExecutor implements MapperFactory {

    protected final Map<String, MapperExecutor> mappers = new ConcurrentHashMap<>();

    @Override
    public <T> T map(Object source, Class<T> destination) {
        return map(source, CodeFactory.create(destination));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T map(Object source, T destination) {
        var mapper = mappers.get(calcMapperName(source, destination));
        if (isNull(mapper)) {
            mapper = buildMapper(source, destination);
        }
        return (T) mapper.map(source, destination);
    }

    protected <T> MapperExecutor buildMapper(Object source, T destination) {
        var result = new MapperExecutor(source, destination);
        mappers.put(calcMapperName(source, destination), result);
        return result;
    }

    protected String calcMapperName(Object source, Object destination) {
        return source.getClass().getCanonicalName() + "->" + destination.getClass().getCanonicalName();
    }

}
