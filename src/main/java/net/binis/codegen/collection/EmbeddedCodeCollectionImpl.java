package net.binis.codegen.collection;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 Binis Belev
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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EmbeddedCodeCollectionImpl<M, T, R> implements EmbeddedCodeCollection<M, T, R> {

    private final Collection<T> collection;
    protected final R parent;
    protected final Class<T> cls;

    public EmbeddedCodeCollectionImpl(R parent, Collection<T> collection, Class<T> cls) {
        this.parent = parent;
        this.collection = collection;
        this.cls = cls;
    }


    @Override
    public EmbeddedCodeCollection<M, T, R> add(T value) {
        collection.add(value);
        return this;
    }

    @Override
    public EmbeddedCodeCollection<M, T, R> remove(T value) {
        collection.remove(value);
        return this;
    }

    @Override
    public EmbeddedCodeCollection<M, T, R> clear() {
        collection.clear();
        return this;
    }

    @Override
    public EmbeddedCodeCollection<M, T, R> each(Consumer<M> doWhat) {
        collection.forEach(e -> doWhat.accept(CodeFactory.modify(this, e)));
        return this;
    }

    @Override
    public EmbeddedCodeCollection<M, T, R> ifEmpty(Consumer<EmbeddedCodeCollection<M, T, R>> doWhat) {
        if (collection.isEmpty()) {
            doWhat.accept(this);
        }
        return this;
    }

    @Override
    public EmbeddedCodeCollection<M, T, R> ifNotEmpty(Consumer<EmbeddedCodeCollection<M, T, R>> doWhat) {
        if (!collection.isEmpty()) {
            doWhat.accept(this);
        }
        return this;
    }

    @Override
    public EmbeddedCodeCollection<M, T, R> ifContains(T value, Consumer<EmbeddedCodeCollection<M, T, R>> doWhat) {
        if (collection.contains(value)) {
            doWhat.accept(this);
        }
        return this;
    }

    @Override
    public EmbeddedCodeCollection<M, T, R> ifContains(Predicate<T> predicate, Consumer<EmbeddedCodeCollection<M, T, R>> doWhat) {
        if (collection.stream().anyMatch(predicate)) {
            doWhat.accept(this);
        }
        return this;
    }

    @Override
    public EmbeddedCodeCollection<M, T, R> ifNotContains(T value, Consumer<EmbeddedCodeCollection<M, T, R>> doWhat) {
        if (!collection.contains(value)) {
            doWhat.accept(this);
        }
        return this;
    }

    @Override
    public EmbeddedCodeCollection<M, T, R> ifNotContains(Predicate<T> predicate, Consumer<EmbeddedCodeCollection<M, T, R>> doWhat) {
        if (collection.stream().noneMatch(predicate)) {
            doWhat.accept(this);
        }
        return this;
    }

    @Override
    public M add() {
        T value = CodeFactory.create(cls);
        collection.add(value);
        return CodeFactory.modify(this, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<M> find(Predicate<T> predicate) {
        return collection.stream().filter(predicate).map(e -> (M) CodeFactory.modify(this, e)).findFirst();
    }

    @Override
    public List<M> findAll(Predicate<T> predicate) {
        return collection.stream().filter(predicate).map(e -> (M) CodeFactory.modify(this, e)).collect(Collectors.toList());
    }

    @Override
    public R and() {
        return parent;
    }

    public Stream<T> stream() {
        return collection.stream();
    };
}
