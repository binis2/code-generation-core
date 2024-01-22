package net.binis.codegen.collection;

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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class CodeListImpl<T, R> implements CodeList<T, R> {

    private final R parent;
    private final List<T> list;
    private final Consumer<T> validator;

    public CodeListImpl(R parent, List<T> list) {
        this.parent = parent;
        this.list = list;
        this.validator = null;
    }

    public CodeListImpl(R parent, List<T> list, Consumer<T> validator) {
        this.parent = parent;
        this.list = list;
        this.validator = validator;
    }

    @Override
    public CodeList<T, R> add(T value) {
        validate(value);
        list.add(value);
        return this;
    }

    @Override
    public R done() {
        return parent;
    }

    @Override
    public CodeList<T, R> _if(boolean condition, Consumer<CodeList<T, R>> consumer) {
        if (condition) {
            consumer.accept(this);
        }
        return this;
    }

    @Override
    public CodeList<T, R> _if(boolean condition, BiConsumer<CodeList<T, R>, R> consumer) {
        if (condition) {
            consumer.accept(this, parent);
        }
        return this;
    }

    @Override
    public CodeList<T, R> _if(boolean condition, Consumer<CodeList<T, R>> consumer, Consumer<CodeList<T, R>> elseConsumer) {
        if (condition) {
            consumer.accept(this);
        } else {
            elseConsumer.accept(this);
        }
        return this;
    }

    @Override
    public CodeList<T, R> _if(boolean condition, BiConsumer<CodeList<T, R>, R> consumer, BiConsumer<CodeList<T, R>, R> elseConsumer) {
        if (condition) {
            consumer.accept(this, parent);
        } else {
            elseConsumer.accept(this, parent);
        }
        return this;
    }

    @Override
    public CodeList<T, R> _self(BiConsumer<CodeList<T, R>, R> consumer) {
        consumer.accept(this, parent);
        return this;
    }

    @Override
    public CodeList<T, R> _map(Object source) {
        throw new UnsupportedOperationException("Not implemented yet!");
        //TODO: Implement handling for collections.
    }

    protected void validate(T value) {
        if (nonNull(validator)) {
            validator.accept(value);
        }
    }

}
