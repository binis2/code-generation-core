package net.binis.codegen.collection;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 - 2026 Binis Belev
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

import net.binis.codegen.modifier.BaseModifier;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.UnaryOperator;

public interface CodeList<T, R> extends BaseModifier<CodeList<T, R>, R> {

    CodeList<T, R> add(T value);

    CodeList<T, R> add(int index, T value);

    CodeList<T, R> addAll(Collection<? extends T> collection);

    CodeList<T, R> addAll(int index, Collection<? extends T> collection);

    CodeList<T, R> remove(T value);

    CodeList<T, R> remove(int index);

    CodeList<T, R> removeAll(Collection<?> collection);

    CodeList<T, R> retainAll(Collection<?> collection);

    CodeList<T, R> replaceAll(UnaryOperator<T> operator);

    CodeList<T, R> sort(Comparator<? super T> c);

    CodeList<T, R> clear();

    CodeList<T, R> set(int index, T value);

    CodeList<T, R> addFirst(T value);

    CodeList<T, R> removeFirst();

    CodeList<T, R> removeLast();
}