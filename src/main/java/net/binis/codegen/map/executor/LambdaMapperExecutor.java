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

import net.binis.codegen.map.Mapping;

import java.util.function.BiFunction;

public class LambdaMapperExecutor implements Mapping, ClassMapping {

    private final Class<?> source   ;
    private final Class<?> destination;
    private final BiFunction lambda;
    private final boolean cls;

    public LambdaMapperExecutor(Class<?> source, Class<?> destination, boolean isClass, BiFunction lambda) {
        this.source = source;
        this.destination = destination;
        this.lambda = lambda;
        this.cls = isClass;
    }
    @Override
    public Class getSource() {
        return source;
    }

    @Override
    public Class getDestination() {
        return destination;
    }

    @Override
    public boolean isClass() {
        return cls;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object map(Object source, Object destination) {
        return lambda.apply(source, destination);
    }
}
