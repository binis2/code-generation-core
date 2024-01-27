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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static java.util.Objects.nonNull;

public class CodeListImpl<T, R> implements CodeList<T, R> {

    protected final R parent;
    protected final List<T> list;
    protected final Consumer<T> validator;

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
    public CodeList<T, R> add(int index, T value) {
        validate(value);
        list.add(index, value);
        return this;
    }

    @Override
    public CodeList<T, R> addAll(Collection<? extends T> collection) {
        collection.forEach(this::validate);
        list.addAll(collection);
        return this;
    }

    @Override
    public CodeList<T, R> addAll(int index, Collection<? extends T> collection) {
        collection.forEach(this::validate);
        list.addAll(index, collection);
        return this;
    }

    @Override
    public CodeList<T, R> remove(T value) {
        list.remove(value);
        return this;
    }

    @Override
    public CodeList<T, R> remove(int index) {
        list.remove(index);
        return this;
    }

    @Override
    public CodeList<T, R> removeAll(Collection<?> collection) {
        list.removeAll(collection);
        return this;
    }

    @Override
    public CodeList<T, R> retainAll(Collection<?> collection) {
        list.retainAll(collection);
        return this;
    }

    @Override
    public CodeList<T, R> replaceAll(UnaryOperator<T> operator) {
        list.replaceAll(o -> {
            var value = operator.apply(o);
            validate(value);
            return value;
        });
        return this;
    }

    @Override
    public CodeList<T, R> sort(Comparator<? super T> c) {
        list.sort(c);
        return this;
    }

    @Override
    public CodeList<T, R> clear() {
        list.clear();
        return this;
    }

    @Override
    public CodeList<T, R> set(int index, T value) {
        validate(value);
        list.set(index, value);
        return this;
    }

    @Override
    public CodeList<T, R> addFirst(T value) {
        validate(value);
        list.add(0, value);
        return this;
    }

    @Override
    public CodeList<T, R> removeFirst() {
        list.remove(0);
        return this;
    }

    @Override
    public CodeList<T, R> removeLast() {
        list.remove(list.size() - 1);
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
