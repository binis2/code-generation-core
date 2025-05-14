package net.binis.codegen.map.executor;

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

import net.binis.codegen.factory.CodeFactory;
import net.binis.codegen.map.MapperFactory;
import net.binis.codegen.map.Mapping;
import net.binis.codegen.map.MappingStrategy;
import net.binis.codegen.map.builder.CustomMappingBuilder;
import net.binis.codegen.map.builder.DestinationMappingBuilder;
import net.binis.codegen.map.builder.SourceMappingBuilder;

import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class MappingBuilderExecutor implements SourceMappingBuilder, DestinationMappingBuilder, CustomMappingBuilder {

    protected Class source;
    protected Mapping mapping;
    protected boolean producer;

    protected MappingStrategy strategy = MappingStrategy.GETTERS_SETTERS;

    protected Object key = MapperFactory.DEFAULT;

    @Override
    public SourceMappingBuilder strategy(MappingStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    @Override
    public <K> SourceMappingBuilder key(K key) {
        this.key = key;
        return this;
    }

    @Override
    public void custom(BiConsumer mapping) {
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, this.mapping.getDestination(), false, producer, strategy, (source, destination) -> {
            var result = this.mapping.map(source, destination);
            mapping.accept(source, destination);
            return result;
        }), key);
    }

    @Override
    public void producer(Function produce) {
        producer = true;
        CodeFactory.create(MapperFactory.class).registerMapper(new LambdaMapperExecutor(source, this.mapping.getDestination(), false, producer, strategy, (source, destination) -> {
            return produce.apply(source);
        }), key);

    }

    @Override
    public void register() {
        CodeFactory.create(MapperFactory.class).registerMapper(mapping, key);
    }

    @Override
    public CustomMappingBuilder destination(Class destination) {
        mapping = CodeFactory.create(MapperFactory.class).mapping(source, destination, strategy, key);
        return this;
    }

    @Override
    public DestinationMappingBuilder source(Class source) {
        this.source = source;
        return this;
    }

}
