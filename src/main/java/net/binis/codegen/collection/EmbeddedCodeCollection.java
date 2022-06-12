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
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public interface EmbeddedCodeCollection<M, T, R> {

    EmbeddedCodeCollection<M, T, R> _add(T value);

    EmbeddedCodeCollection<M, T, R> _add$(UnaryOperator<M> initializer);

    EmbeddedCodeCollection<M, T, R> _remove(T value);

    EmbeddedCodeCollection<M, T, R> _remove(int index);

    EmbeddedCodeCollection<M, T, R> _clear();

    EmbeddedCodeCollection<M, T, R> _each(Consumer<M> doWhat);

    EmbeddedCodeCollection<M, T, R> _ifEmpty(Consumer<EmbeddedCodeCollection<M, T, R>> doWhat);

    EmbeddedCodeCollection<M, T, R> _ifNotEmpty(Consumer<EmbeddedCodeCollection<M, T, R>> doWhat);

    EmbeddedCodeCollection<M, T, R> _ifContains(T value, Consumer<EmbeddedCodeCollection<M, T, R>> doWhat);

    EmbeddedCodeCollection<M, T, R> _ifContains(Predicate<T> predicate, Consumer<EmbeddedCodeCollection<M, T, R>> doWhat);

    EmbeddedCodeCollection<M, T, R> _ifNotContains(T value, Consumer<EmbeddedCodeCollection<M, T, R>> doWhat);

    EmbeddedCodeCollection<M, T, R> _ifNotContains(Predicate<T> predicate, Consumer<EmbeddedCodeCollection<M, T, R>> doWhat);

    EmbeddedCodeCollection<M, T, R> _sort(Comparator<? super T> comparator);

    Stream<T> _stream();

    M _add();

    M _get(int index);

    M _insert(int index);

    M _first();

    M _last();

    Optional<M> _find(Predicate<T> predicate);

    List<M> _findAll(Predicate<T> predicate);

    R done();

}
