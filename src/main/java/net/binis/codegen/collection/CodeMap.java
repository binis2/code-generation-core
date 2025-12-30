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

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface CodeMap<K, V, R> extends BaseModifier<CodeMap<K, V, R>, R> {

    CodeMap<K, V, R> put(K key, V value);

    CodeMap<K, V, R> putAll(Map<? extends K, ? extends V> map);

    CodeMap<K, V, R> remove(Object key);

    CodeMap<K, V, R> clear();

    CodeMap<K, V, R> replaceAll(BiFunction<? super K, ? super V, ? extends V> function);

    CodeMap<K, V, R> putIfAbsent(K key, V value);

    CodeMap<K, V, R> remove(Object key, Object value);

    CodeMap<K, V, R> replace(K key, V oldValue, V newValue);

    CodeMap<K, V, R> replace(K key, V value);

    CodeMap<K, V, R> computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction);

    CodeMap<K, V, R> computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction);

    CodeMap<K, V, R> compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction);

    CodeMap<K, V, R> merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction);

}
