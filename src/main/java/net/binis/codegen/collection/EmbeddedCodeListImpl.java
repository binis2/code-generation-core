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

import net.binis.codegen.factory.CodeFactory;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class EmbeddedCodeListImpl<M, T, R> extends EmbeddedCodeCollectionImpl<M, T, R> {

    private final List<T> list;

    public EmbeddedCodeListImpl(R parent, List<T> list, Class<T> cls) {
        super(parent, list, cls);
        this.list = list;
    }

    public EmbeddedCodeListImpl(R parent, List<T> list, Class<T> cls, Consumer<T> validator) {
        super(parent, list, cls, validator);
        this.list = list;
    }

    @Override
    public EmbeddedCodeCollection<M, T, R> _remove(int index) {
        list.remove(index);
        return this;
    }

    @Override
    public EmbeddedCodeCollection<M, T, R> _sort(Comparator<? super T> comparator) {
        list.sort(comparator);
        return this;
    }

    @Override
    public M _get(int index) {
        return CodeFactory.modify(this, list.get(index), cls);
    }

    @Override
    public M _insert(int index) {
        T value = CodeFactory.create(cls);
        list.add(index, value);
        return CodeFactory.modify(this, value, cls);
    }

    @Override
    public M _first() {
        return CodeFactory.modify(this, list.get(0), cls);
    }

    @Override
    public M _last() {
        return CodeFactory.modify(this, list.get(list.size() - 1), cls);
    }
}
