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

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class EmbeddedCodeSetImpl<M, T, R> extends EmbeddedCodeCollectionImpl<M, T, R> {

    public EmbeddedCodeSetImpl(R parent, Set<T> set, Class<T> cls) {
        super(parent, set, cls);
    }

    public EmbeddedCodeSetImpl(R parent, List<T> list, Class<T> cls, Consumer<T> validator) {
        super(parent, list, cls, validator);
    }

    @Override
    public EmbeddedCodeCollection<M, T, R> _remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EmbeddedCodeCollection<M, T, R> _sort(Comparator<? super T> comparator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public M _get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public M _insert(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public M _first() {
        throw new UnsupportedOperationException();
    }

    @Override
    public M _last() {
        throw new UnsupportedOperationException();
    }

}
